package sketchupblocks.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
	private double minHeight = -50;
	private boolean rotateLeft = false;
	private boolean rotateRight = false;
	private boolean zoomIn = false;
	private boolean zoomOut = false;
	
	//fiducial debug lines
	private boolean camDebug = true;
	private HashMap<String, Line> debugLines = new HashMap<>();
	private int lineLength = 80;
	private int lineRate = 1;
	private boolean lineShorter = false;
	private boolean lineLonger = false;
	
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
		systemCameras[cameraId].eye.x = pos.y * 10;
		systemCameras[cameraId].eye.y = -pos.z * 10;
		systemCameras[cameraId].eye.z = pos.x * 10;
	}
	
	public void setSystemCamera(int index, Camera newCamera)
	{
		systemCameras[index] = newCamera;
	}
	
	public void setDebugLines(String[] IDS, Line[] lines)
	{
		for(int x = 0; x < IDS.length; ++x)
		{
			debugLines.put(IDS[x], lines[x]);
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
			
		window.pointLight(200, 200, 200, 100, -1000, 400);
		window.ambientLight(50, 50, 50);
		
		window.background(0);
		
		drawBlocks();
		drawDebugLines();
		drawConstructionFloor();
	}
	
	private void drawConstructionFloor()
	{
		
		window.pushMatrix();
		
		window.scale(5f, 1.0f, 5f);
		
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
		
		window.vertex(-100, 0, -100, 0, 0);
		window.vertex(100, 0, -100, 10, 0);
		window.vertex(100, 0, 100, 10, 10);
		window.vertex(-100, 0, 100, 0, 10);
		
		window.endShape(PConstants.CLOSE);
		
		window.popMatrix();
	}
	
	private void drawBlocks()
	{
		window.pushMatrix();
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
	}
	
	private void drawDebugLines()
	{
		//TODO: remove me
		if(lineShorter)
			lineLength -= lineRate;
		if(lineLonger)
			lineLength += lineRate;
		
		window.pushMatrix();
		window.scale(1f);
		if(camDebug)
		{
			window.stroke(255, 0, 0);
			for(Line line: new ArrayList<Line>(debugLines.values()))
			{
				Vec3 start = new Vec3(line.point.y, -line.point.z, line.point.x);
				Vec3 end = new Vec3(line.direction.y, -line.direction.z, line.direction.x);
				
				start = Vec3.scalar(10, start);
				end = Vec3.scalar(lineLength * 10, end);
				end = Vec3.add(start, end);
				System.out.println("END: " + end.toString());
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
		
		if(zoomIn)// && cameraHeight < minHeight)
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
			else if(e.getKey() == 'd')
			{
				if(e.getAction() == KeyEvent.RELEASE)
					camDebug = !camDebug;
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