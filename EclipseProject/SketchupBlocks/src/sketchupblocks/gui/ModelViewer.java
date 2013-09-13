package sketchupblocks.gui;

import java.util.ArrayList;
import java.util.HashMap;
import processing.core.*;
import processing.event.*;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.ColladaLoader;
import sketchupblocks.base.Model;
import sketchupblocks.base.ModelBlock;
import sketchupblocks.base.ModelChangeListener;
import sketchupblocks.base.Settings;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.exception.BlockNoTypeException;
import sketchupblocks.math.Line;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;
import sketchupblocks.network.Lobby;

public class ModelViewer implements ModelChangeListener
{
	private PApplet window;
	private Lobby lobby;
	private HashMap<Integer,ModelBlock> blockMap;
	private Camera userCamera;
	private Camera[] systemCameras;
	private Camera currentCamera;
	private final ModelViewerEventListener modelViewerEventListener = new ModelViewerEventListener();
	private int selectCamera = 0;
	
	//fiducial
	private double velocityScalar = 2.0;
	
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
	
	//fiducial debug lines
	private boolean showDebugLines = false;
	private HashMap<String, Line> debugLines = new HashMap<>();
	private int lineLength = 80;
	private int lineRate = 1;
	private boolean lineShorter = false;
	private boolean lineLonger = false;
	
	//fiducial debug points
	private boolean showDebugPoints = false;
	private HashMap<String, Vec3> debugPointsMap = new HashMap<>();
	
	//debug model
	private boolean showModel = true;
	private boolean transparentModel = false;
	
	//transparent construction floor
	private boolean alphaBlendFloor = false;
	
	private PImage tilesTexture;
	 
	public ModelViewer()
	{
		Vec3 up = new Vec3(0, 1, 0);
		Vec3 at = new Vec3();
		Vec3 eye = new Vec3();
		
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
	
	public void updateSystemCameraPosition(int cameraId, Vec3 pos)
	{
		systemCameras[cameraId].eye.x = pos.y * 10.2;
		systemCameras[cameraId].eye.y = -pos.z * 10.2;
		systemCameras[cameraId].eye.z = pos.x * 10.2;
	}
	
	public void setSystemCamera(int index, Camera newCamera)
	{
		systemCameras[index] = newCamera;
	}
	
	public void setDebugLines(String[] IDS, Line[] lines)
	{
		if(IDS.length != lines.length)
		{
			System.out.println("ERROR: Debug lines, lengths don't match.");
			return;
		}
		
		for(int x = 0; x < IDS.length; ++x)
		{
			debugLines.put(IDS[x], lines[x]);
		}
	}
	
	public void setDebugPoints(String[] IDS, Vec3[] points)
	{
		if(IDS.length != points.length)
		{
			System.out.println("ERROR: Debug points, lengths don't match.");
			return;
		}
		
		for(int x = 0; x < IDS.length; ++x)
		{
			debugPointsMap.put(IDS[x], points[x]);
		}
	}
	
	public void setWindow(PApplet _window)
	{
		window = _window;
		window.registerMethod("keyEvent", modelViewerEventListener);
		
		tilesTexture = window.loadImage("./images/FloorTile.png");
	}
	
	public void setLobby(Lobby _lobby) throws Exception
	{
	    lobby = _lobby;
	    try
	    {
		    Model model = lobby.getModel();
	    	ArrayList<ModelBlock> blockList = new ArrayList<>(model.getBlocks());
	    	blockMap = new HashMap<>();
	    	for(ModelBlock mBlock: blockList)
	    		blockMap.put(new Integer(mBlock.smartBlock.blockId), mBlock);
	    }
	    catch(Exception e)
	    {
	    	throw e;
	    }
	}
	  
	public void fireModelChangeEvent(ModelBlock change) throws BlockNoTypeException
	{
		if(change.type == ModelBlock.ChangeType.UPDATE)
			blockMap.put(new Integer(change.smartBlock.blockId), change);
		else
			blockMap.remove(new Integer(change.smartBlock.blockId));
	}
	
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
	
	public void drawModel()
	{
		updateCamera();
		
		window.camera((float)currentCamera.eye.x, (float)currentCamera.eye.y, (float)currentCamera.eye.z,
				      (float)currentCamera.at.x, (float)currentCamera.at.y, (float)currentCamera.at.z,
				      (float)currentCamera.up.x, (float)currentCamera.up.y, (float)currentCamera.up.z);
		
		final float fov = PConstants.PI/3;
		final float cameraZ = (float)(window.height/2.0) / (float)Math.tan(fov/2.0);
		final float aspectR = (float)window.width / (float)window.height;
		window.perspective(fov, aspectR, cameraZ/100.0f, cameraZ*100.0f);
		
		window.directionalLight(150, 150, 150, 0.2f, 0.8f, 0f);
		window.pointLight(200, 200, 200, 100, -1000, 400);
		window.ambientLight(50, 50, 50);
		
		window.background(50);
		
		drawDebugLines();
		drawDebugPoints();
		drawBlocks();
		drawConstructionFloor();
	}
	
	private void drawConstructionFloor()
	{
		window.pushMatrix();
		
		window.noStroke();
		window.fill(window.color(255));
		
		if(alphaBlendFloor)
			window.tint(255, 50);
		else
			window.tint(255, 255);
		
		window.beginShape();
		
		window.texture(tilesTexture);
		window.textureMode(PConstants.NORMAL);
		window.textureWrap(PConstants.REPEAT);
		
		final int point = 5000;
		final int repeat = 100;
		
		window.vertex(-point, 0, -point, 0, 0);
		window.vertex(point, 0, -point, repeat, 0);
		window.vertex(point, 0, point, repeat, repeat);
		window.vertex(-point, 0, point, 0, repeat);
				
		window.endShape(PConstants.CLOSE);
		
		window.popMatrix();
	}
	
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
			for(ModelBlock block: new ArrayList<ModelBlock>(blockMap.values()))
			{
				if(block.type == ModelBlock.ChangeType.REMOVE)
					System.out.println("Drawing removed block!");
				
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
			
			if(transparentModel)
				window.fill(255);
		}
	}
	
	private void drawDebugPoints()
	{
		if(showDebugPoints)
		{
			window.noStroke();
			window.fill(0, 255, 0);
			for(Vec3 point: debugPointsMap.values())
			{
				window.pushMatrix();
				
				point = Vec3.scalar(10, point);
				window.translate((float)point.y, (float)-point.z, (float)point.x);
				window.sphere(5);
				
				window.popMatrix();
			}
			window.fill(255);
		}
	}
	
	private void drawDebugLines()
	{
		if(lineShorter)
			lineLength -= lineRate;
		if(lineLonger)
			lineLength += lineRate;
		
		window.pushMatrix();
		window.scale(1f);
		if(showDebugLines)
		{
			window.stroke(255, 0, 0);
			for(Line line: debugLines.values())
			{
				Vec3 start = new Vec3(line.point.y, -line.point.z, line.point.x);
				Vec3 end = new Vec3(line.direction.y, -line.direction.z, line.direction.x);
				
				start = Vec3.scalar(10, start);
				end = Vec3.scalar(lineLength * 10, end);
				end = Vec3.add(start, end);
				window.line((float)start.x, (float)start.y, (float)start.z, (float)end.x, (float)end.y, (float)end.z);
			}
		}
		window.popMatrix();
	}

	private void switchCamera()
	{
		if(selectCamera == 0)
			currentCamera = userCamera;
		else if(selectCamera <= Settings.numCameras)
		{
			currentCamera = systemCameras[selectCamera - 1];
		}
	}
	
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
	
	protected class ModelViewerEventListener
	{
		public void keyEvent(final KeyEvent e) 
		{
			if(e.getKeyCode() == 192 || (e.getKeyCode() >= 49 && e.getKeyCode() <= (49 + Settings.numCameras)))
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
					ColladaLoader.export(new ArrayList<ModelBlock>(blockMap.values()));
			}
			else if(e.getKey() == 'm')
			{
				if(e.getAction() == KeyEvent.RELEASE)
					showModel = !showModel;
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
			else if(e.getKey() == 'l')
			{
				if(e.getAction() == KeyEvent.RELEASE)
					showDebugLines = !showDebugLines;
			}
			else if(e.getKey() == 'p')
			{
				if(e.getAction() == KeyEvent.RELEASE)
					showDebugPoints = !showDebugPoints;
			}
			else if(e.getKey() == 't')
			{
				if(e.getAction() == KeyEvent.RELEASE)
					alphaBlendFloor = !alphaBlendFloor;
			}
			else if(e.getKey() == '[')
			{
				if(e.getAction() == KeyEvent.PRESS)
					lineShorter = true;
				else if(e.getAction() == KeyEvent.RELEASE)
					lineShorter = false;
			}
			else if(e.getKey() == ']')
			{
				if(e.getAction() == KeyEvent.PRESS)
					lineLonger = true;
				else if(e.getAction() == KeyEvent.RELEASE)
					lineLonger = false;
			}
		}
	}
}