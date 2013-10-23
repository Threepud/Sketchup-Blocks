package sketchupblocks.gui;

import java.util.ArrayList;
import processing.core.*;
import processing.event.*;
import processing.opengl.PShader;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.ColladaLoader;
import sketchupblocks.base.Logger;
import sketchupblocks.base.Model;
import sketchupblocks.base.RuntimeData;
import sketchupblocks.base.Settings;
import sketchupblocks.construction.ModelBlock;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.exception.ModelNotSetException;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;
import sketchupblocks.network.Lobby;

/**
 * @author Jacques Coetzee
 * The model viewer class displays the current
 * model being built by the user in a real-time
 * 3D OpenGL window.
 */
public class ModelViewer
{
	private DebugViewer debugViewer;
	protected PApplet window;
	protected Lobby lobby;
	private Camera userCamera;
	private Camera[] systemCameras;
	private Camera currentCamera;
	private int selectCamera = 0;
	
	//shaders
	private PShader fog;
	
	//fiducial
	private double velocityScalar = 5.0;
	
	//camera
	private double currentRotation = 0;
	private double rightVel = 0;
	private double leftVel = 0;
	private double maxVel = 0.08;
	private double rotationIncrement = 0.03;
	private double rotationDecrement = 0.003;
	private double cameraHeight = -500;
	private double cameraRadius = 700;
	
	private double zoomVel = 30;
	private double maxHeight = -2000;
	private double minHeight = -100;
	private boolean rotateLeft = false;
	private boolean rotateRight = false;
	private boolean zoomIn = false;
	private boolean zoomOut = false;
	
	//debug model
	private boolean showModel = true;
	private boolean transparentModel = false;
	
	//transparent construction floor
	private boolean transparentConstructionFloor = false;
	
	//debug line intersections
	//private boolean showDebugLineIntersection = false;
	
	private PImage tilesTexture;
	 
	/**
	 * Constructor for the ModelViewer class.
	 * The constructor sets up the current view camera
	 * and initializes some other member variables.
	 */
	public ModelViewer()
	{
		Vec3 up = new Vec3(0, 1, 0);
		Vec3 at = new Vec3();
		Vec3 eye = new Vec3();
		
		currentRotation = 2.5;
		eye.x = cameraRadius * Math.cos(currentRotation);
		eye.z = cameraRadius * Math.sin(currentRotation);
		eye.y = cameraHeight;
		
		userCamera = new Camera(up, at, eye);
		
		systemCameras = new Camera[Settings.numCameras];
		for(int x = 0; x < Settings.numCameras; ++x)
		{
			systemCameras[x] = new Camera();
		}
		
		currentCamera = userCamera;
	}
	
	/**
	 * This method sets the parent PApplet object.
	 * This PApplet is referenced to send all the draw
	 * commands for the graphics.
	 * @param _window Parent PApplet object.
	 */
	public void setWindow(PApplet _window)
	{
		window = _window;
		
		tilesTexture = window.loadImage("./images/FloorTile.png");
		
		fog = window.loadShader("./shaders/constructionFloor/fogFrag.glsl", "./shaders/constructionFloor/fogVert.glsl");
	}
	
	/**
	 * This function provides the ModelViewer with a reference to the
	 * lobby to get the latest model being built.
	 * @param _lobby Lobby Class.
	 */
	public void setLobby(Lobby _lobby)
	{
	    lobby = _lobby;
	}
	
	/**
	 * This method passes the keyboard input to
	 * the ModelViewer. The method then checks if
	 * any relevant keys have been pressed and makes the
	 * appropriate changes in the class.
	 * @param e Keyboard event.
	 */
	public void setKeyboardInput(KeyEvent e)
	{
		if(e.getKeyCode() == 192 || (e.getKeyCode() >= 49 && e.getKeyCode() < (49 + Settings.numCameras)))
		{
			if(e.getKeyCode() == 192)
				selectCamera = 0;
			else
				selectCamera = e.getKeyCode() - 48;
			switchCamera();
		}
		//zoom in
		else if(e.getKeyCode() == 38)
		{
			if(e.getAction() == KeyEvent.PRESS)
				zoomIn = true;
			else if(e.getAction() == KeyEvent.RELEASE)
				zoomIn = false;
		}
		//zoom out
		else if(e.getKeyCode() == 40)
		{
			if(e.getAction() == KeyEvent.PRESS)
				zoomOut = true;
			else if(e.getAction() == KeyEvent.RELEASE)
				zoomOut = false;
		}
		//right
		else if(e.getKeyCode() == 39)
		{
			if(e.getAction() == KeyEvent.PRESS)
				rotateRight = true;
			else if(e.getAction() == KeyEvent.RELEASE)
				rotateRight = false;
		}
		//left
		else if(e.getKeyCode() == 37)
		{
			if(e.getAction() == KeyEvent.PRESS)
				rotateLeft = true;
			else if(e.getAction() == KeyEvent.RELEASE)
				rotateLeft = false;
		}
		else if(e.getKey() == 'e')
		{
			if(e.getAction() == KeyEvent.RELEASE)
			{
				Model model;
				try 
				{
					model = lobby.getModel();
				}
				catch (ModelNotSetException e1) 
				{
					e1.printStackTrace();
					return;
				}
				ColladaLoader.export(new ArrayList<ModelBlock>(model.getBlocks()));
			}
		}
		else if(e.getKey() == 'm')
		{
			if(e.getAction() == KeyEvent.RELEASE)
				showModel = !showModel;
		}
		else if(e.getKey() == 'n')
		{
			if(e.getAction() == KeyEvent.RELEASE)
				transparentModel = !transparentModel;
		}
		else if(e.getKey() == 't')
		{
			if(e.getAction() == KeyEvent.RELEASE)
				transparentConstructionFloor = !transparentConstructionFloor;
		}
		/*else if(e.getKey() == 'i')
		{
			if(e.getAction() == KeyEvent.RELEASE)
			{
				showDebugLineIntersection = !showDebugLineIntersection;
			}
		}*/
		else
		{
			debugViewer.setKeyboardInput(e);
		}
	}
	
	/**
	 * This function creates a debug viewer for the model viewer.
	 * The debug viewer has the ability to display additional
	 * debug information along with the model in the OpenGL view.
	 * @throws Exception Exception for if lobby or parent PApplet are not set.
	 */
	public void createDebugViewer() throws Exception
	{
		if(lobby == null || window == null)
			throw new Exception("Lobby or window not set.");
		
		debugViewer = new DebugViewer(lobby, window);
	}
	
	/**
	 * This function is the draw call to draw the model.
	 * This function calls all other relevant sub functions
	 * to fully display all graphical components.
	 */
	public void drawModel()
	{
		updateCamera();
		
		window.camera((float)currentCamera.eye.x, (float)currentCamera.eye.y, (float)currentCamera.eye.z,
				      (float)currentCamera.at.x, (float)currentCamera.at.y, (float)currentCamera.at.z,
				      (float)currentCamera.up.x, (float)currentCamera.up.y, (float)currentCamera.up.z);
		
		final float fov = PApplet.radians(68.5f);
		final float cameraZ = (float)(window.height/2.0) / (float)Math.tan(fov/2.0);
		//final float aspectR = (float)window.width / (float)window.height;
		final float aspectR = 1280.0f / 720.0f;
		window.perspective(fov, aspectR, cameraZ/100.0f, cameraZ*100.0f);
		
		float pwr = 150.0f;
		if(!Settings.fancyShaders)
			pwr = 200.0f;
		
		window.directionalLight(pwr, pwr, pwr, 0.2f, 0.8f, 0f);
		window.pointLight(pwr, pwr, pwr, 100, -1000, 400);
		window.ambientLight(50, 50, 50);
		
		window.background(100);
		
		debugViewer.drawDebugInformation();
		
		drawBlocks();
		drawSkyBox();
		drawConstructionFloor();
	}
	
	/**
	 * This function draws the actual model blocks graphical
	 * component.
	 */
	private void drawBlocks()
	{
		if(showModel)
		{
			window.pushMatrix();
			window.noStroke();
			if(transparentModel)
				window.fill(255, 255, 255, 100);
			else
				window.fill(255);
			window.scale(10, 10, 10);
			
			//draw block list
			Model model;
			try 
			{
				model = lobby.getModel();
			} 
			catch (ModelNotSetException e) 
			{
				e.printStackTrace();
				return;
			}
			for(ModelBlock block: new ArrayList<ModelBlock>(model.getBlocks()))
			{
				//TODO: Please remove this...
				//###
				if(block.smartBlock.blockId == RuntimeData.blockID)
				{
					if(transparentModel)
						window.fill(0, 0, 255, 100);
					else
						window.fill(0, 0, 255);
					
				}
				else
				{
					if(transparentModel)
						window.fill(255, 255, 255, 100);
					else
						window.fill(255);
				}
				//###
				
				/*if(showDebugLineIntersection)
				{
					Line line = RuntimeData.debugLine;
					if(line != null)
					{
						if(EnvironmentAnalyzer.isIntersecting(line, block))
						{
							window.fill(0, 255, 0);
						}
						else
						{
							window.fill(255);
						}
					}
				}*/
				if (block.type == ModelBlock.ChangeType.REMOVE)
				{
					Logger.log("Drawing removed block!", 1);
					return;
				}
				
				SmartBlock smartBlock = block.smartBlock;
				window.beginShape(PConstants.TRIANGLES);
				for(int x = 0; x < smartBlock.indices.length; ++x)
				{
					Vec3 vertex = smartBlock.vertices[smartBlock.indices[x]];
					
					vertex = Matrix.multiply(block.transformationMatrix, vertex.padVec3()).toVec3();
					window.vertex((float)vertex.y, -(float)vertex.z, (float)vertex.x);
				}
				
				window.endShape();
			}
			window.popMatrix();
		}
	}
	
	/**
	 * This function draws the sky dome graphical
	 * component.
	 */
	private void drawSkyBox()
	{
		window.pushMatrix();
		
		window.translate(0, -1000, 0);
		window.fill(74, 154, 225);
		window.noStroke();
		window.sphere(10000);
		
		window.popMatrix();	
	}
	
	/**
	 * This function draws the construction floor
	 * graphical component.
	 */
	private void drawConstructionFloor()
	{
		window.pushMatrix();
		
		if(!Settings.fancyShaders)
		{
			if(transparentConstructionFloor)
				window.tint(255, 100);
			else
				window.tint(255);
		}
		
		window.noStroke();
		window.fill(window.color(255));
		
		window.beginShape();
		
		window.texture(tilesTexture);
		window.textureMode(PConstants.NORMAL);
		window.textureWrap(PConstants.REPEAT);
		
		final int point = 100000;
		final int repeat = 2000;
		
		if(Settings.fancyShaders)
		{
			fog.set("trans", (transparentConstructionFloor) ? 1 : 0);
			window.shader(fog);
		}
		
		window.vertex(-point, 0, -point, 0, 0);
		window.vertex(point, 0, -point, repeat, 0);
		window.vertex(point, 0, point, repeat, repeat);
		window.vertex(-point, 0, point, 0, repeat);
				
		window.endShape(PConstants.CLOSE);
		
		if(Settings.fancyShaders)
			window.resetShader();
		
		window.popMatrix();
	}
	
	/**
	 * This function rotates the camera view when the
	 * rotate command block is presented.
	 * @param event Camera event from ReacTIVision
	 */
	public void rotateView(CameraEvent event)
	{
		if(event.type == CameraEvent.EVENT_TYPE.ADD || event.type == CameraEvent.EVENT_TYPE.UPDATE)
		{
			if(event.xVelocity < 0)
				rightVel = -1 * (event.xVelocity / velocityScalar);
			else if(event.xVelocity > 0)
				leftVel = (event.xVelocity / velocityScalar);
		}
	}
	
	/**
	 * This function switches between the available cameras
	 * when the appropriate keyboard input is provided.
	 */
	private void switchCamera()
	{
		if(selectCamera == 0)
			currentCamera = userCamera;
		else if(selectCamera <= Settings.numCameras && RuntimeData.isSystemCalibrated())
		{
			Vec3 up = new Vec3(0, 1, 0);
			Vec3 eye = new Vec3(RuntimeData.getCameraPosition(selectCamera - 1).y, -RuntimeData.getCameraPosition(selectCamera - 1).z, RuntimeData.getCameraPosition(selectCamera - 1).x);
			eye = Vec3.scalar(10, eye);
			Vec3 at = new Vec3(RuntimeData.getCameraViewVector(selectCamera - 1).y, -RuntimeData.getCameraViewVector(selectCamera - 1).z, RuntimeData.getCameraViewVector(selectCamera - 1).x);
			at = Vec3.add(at, eye);
			currentCamera = new Camera(up, at, eye);
		}
	}
	
	/**
	 * This function updates the target rotation to
	 * realize by matching the target and current rotations.
	 */
	private void changeTarget()
	{
		if(selectCamera != 0)
			return;
		
		if(rotateRight && rightVel < maxVel)
			rightVel += rotationIncrement;
		if(rotateLeft && leftVel < maxVel)
			leftVel += rotationIncrement;
		
		if(rightVel > maxVel)
			rightVel = maxVel;
		if(leftVel > maxVel)
			leftVel = maxVel;
	}
	
	/**
	 * This function matches up the current
	 * rotation when it differs from the target
	 * rotation.
	 */
	private void updateCamera()
	{
		changeTarget();
		
		if(zoomIn && cameraHeight < minHeight)
		{
			cameraHeight += zoomVel;
			cameraRadius -= zoomVel;
		}
		if(zoomOut && cameraHeight > maxHeight)
		{
			cameraHeight -= zoomVel;
			cameraRadius += zoomVel;
		}
		
		rightVel -= rotationDecrement;
		leftVel -= rotationDecrement;
		if(rightVel < 0)
			rightVel = 0;
		if(leftVel < 0)
			leftVel = 0;
		
		currentRotation -= rightVel - leftVel;
		
		userCamera.eye.x = cameraRadius * Math.cos(currentRotation);
		userCamera.eye.z = cameraRadius * Math.sin(currentRotation);
		userCamera.eye.y = cameraHeight;
	}
}