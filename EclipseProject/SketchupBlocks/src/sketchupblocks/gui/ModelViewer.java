package sketchupblocks.gui;

import java.util.ArrayList;
import java.util.HashMap;

import processing.core.*;
import processing.event.*;
import sketchupblocks.base.ColladaLoader;
import sketchupblocks.base.Model;
import sketchupblocks.base.ModelBlock;
import sketchupblocks.base.ModelChangeListener;
import sketchupblocks.base.Settings;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.exception.BlockNoTypeException;
import sketchupblocks.exception.BlockNotFoundException;
import sketchupblocks.exception.ModelNotSetException;
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
	
	private double currentRotation = 0;
	private double rotationIncrement = 0.02;
	private double cameraHeight = -500;
	private double cameraRadius = 500;
	private boolean rotateLeft = false;
	private boolean rotateRight = false;
	
	PImage tilesTexture;
	
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
	
	public void setWindow(PApplet _window)
	{
		window = _window;
		window.registerMethod("keyEvent", modelViewerEventListener);
		
		tilesTexture = window.loadImage("./images/newTile.png");
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
	    /*
	    //################
	    //debug for viewer
	    blockList = new ArrayList<>();
	    SmartBlock smartBlock = ColladaLoader.getSmartBlock("./models/PaperCube.dae");
	    ModelBlock modelBlock = new ModelBlock();
	    modelBlock.smartBlock = smartBlock;
	    //################
	    
	    blockList.add(modelBlock);*/
	}
	  
	public void fireModelChangeEvent(ModelBlock change) throws BlockNoTypeException
	{
		if(change.type == ModelBlock.ChangeType.UPDATE)
			blockMap.put(new Integer(change.smartBlock.blockId), change);
		else
			blockMap.remove(new Integer(change.smartBlock.blockId));
	}
	
	public void drawModel()
	{
		rotateCamera();
		
		window.camera((float)currentCamera.eye.x, (float)currentCamera.eye.y, (float)currentCamera.eye.z,
				      (float)currentCamera.at.x, (float)currentCamera.at.y, (float)currentCamera.at.z,
				      (float)currentCamera.up.x, (float)currentCamera.up.y, (float)currentCamera.up.z);
			
		window.pointLight(200, 200, 200, 100, -1000, 400);
		window.ambientLight(50, 50, 50);
		
		window.background(0);
		
		drawConstructionFloor();
		drawBlocks();
	}
	
	private void drawConstructionFloor()
	{
		window.pushMatrix();
		
		window.scale(5f, 1.0f, 5f);
		
		window.noStroke();
		window.fill(255);
		
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
		//draw block list
		for(ModelBlock block: new ArrayList<ModelBlock>(blockMap.values()))
		{
			SmartBlock smartBlock = block.smartBlock;
			window.scale(10, 10, 10);
			window.beginShape(PConstants.TRIANGLES);
			
			for(int x = 0; x < smartBlock.indices.length; ++x)
			{
				Vec3 vertex = smartBlock.vertices[smartBlock.indices[x]];
				vertex = Matrix.multiply(block.transformationMatrix, vertex);
				window.vertex((float)vertex.y, -(float)vertex.z, (float)vertex.x);
			}
			
			window.endShape();
		}
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
	
	private void rotateCamera()
	{
		if(selectCamera != 0)
			return;
		
		if(rotateRight)
			currentRotation -= rotationIncrement;
		if(rotateLeft)
			currentRotation += rotationIncrement;
		
		if(rotateRight || rotateLeft)
		{
			userCamera.eye.x = cameraRadius * Math.cos(currentRotation);
			userCamera.eye.z = cameraRadius * Math.sin(currentRotation);
			userCamera.eye.y = cameraHeight;
		}
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
		}
	}
}