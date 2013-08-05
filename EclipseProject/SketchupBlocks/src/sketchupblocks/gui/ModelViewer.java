package sketchupblocks.gui;

import java.util.ArrayList;

import processing.core.*;
import processing.event.*;
import sketchupblocks.base.ColladaLoader;
import sketchupblocks.base.ModelBlock;
import sketchupblocks.base.ModelChangeListener;
import sketchupblocks.base.Settings;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.exception.BlockNoTypeException;
import sketchupblocks.exception.BlockNotFoundException;
import sketchupblocks.math.Vec3;
import sketchupblocks.network.Lobby;

public class ModelViewer implements ModelChangeListener
{
	private PApplet window;
	private Lobby lobby;
	private ArrayList<ModelBlock> blockList = null;
	private Camera userCamera;
	private Camera[] systemCameras;
	private Camera currentCamera;
	private final ModelViewerEventListener modelViewerEventListener = new ModelViewerEventListener();
	private int selectCamera = 0;
	
	private double currentRotation = 0;
	private double rotationIncrement = 0.07;
	private double cameraHeight = -500;
	private double cameraRadius = 500;
	private boolean rotateLeft = false;
	private boolean rotateRight = false;
	
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
	
	public void updateCameraPosition(int cameraId, Vec3 pos)
	{
		systemCameras[cameraId].eye = new Vec3(pos.y * 10, -pos.z * 10, pos.x * 10);
		systemCameras[cameraId].up = new Vec3(0, 1, 0);
		systemCameras[cameraId].at = new Vec3(0, 0, 0);
				/*
		if(oldPos == null)
		{
			float[] camPos = cam.getPosition();
			oldPos = new Vector3D(camPos[0], camPos[1], camPos[2]);
		}
		
		Vector3D newPos = new Vector3D(pos.y, -pos.z, pos.x);
		
		if(Settings.verbose >= 3)
		{
			System.out.println("OLD VEC: " + oldPos.getX() + ", " + oldPos.getY() + ", " + oldPos.getZ());
			System.out.println("NEW VEC: " + newPos.getX() + ", " + newPos.getY() + ", " + newPos.getZ());
		}
		
		Rotation rot = new Rotation(oldPos.normalize(), newPos.normalize());
		Vector3D center = new Vector3D(0, 0, 0);
		CameraState state = new CameraState(rot, center, cam.getDistance());
		cam.setState(state, 500);*/
	}
	
	public void setSystemCamera(int index, Camera newCamera)
	{
		systemCameras[index] = newCamera;
	}
	
	public void setWindow(PApplet _window)
	{
		window = _window;
		window.registerMethod("keyEvent", modelViewerEventListener);
	}
	
	public void setLobby(Lobby _lobby) throws RuntimeException
	{
	    lobby = _lobby;
	    /*
	    Model model = lobby.getModel();
	    
	    if(model == null)
	    	throw new ModelNotSetException("Model Viewer: Model not set.");
	    else
	    	blockList = new ArrayList<>(model.getBlocks());
	    	*/
	    
	    //debug for viewer
	    blockList = new ArrayList<>();
	    SmartBlock smartBlock = ColladaLoader.getSmartBlock("./models/PaperCube.dae");
	    ModelBlock modelBlock = new ModelBlock();
	    modelBlock.smartBlock = smartBlock;
	    
	    blockList.add(modelBlock);
	}
	  
	public void fireModelChangeEvent(ModelBlock change) throws Exception
	{
		int index;
		
	    switch(change.type)
	    {
		    case ADD:
		    	blockList.add(change);
		    	break;
		    case UPDATE:
		    	index = findBlockIndex(change.smartBlock.blockId);
		    	blockList.set(index, change);
		    	break;
		    case DELETE:
		    	index = findBlockIndex(change.smartBlock.blockId);
		    	blockList.remove(index);
		    	break;
	    	default:
	    		throw new BlockNoTypeException("Model Viewer: No Model Block Type.");
	    }
	}
	
	private int findBlockIndex(int blockID) throws Exception
	{
    	int index = -1;
    	for(ModelBlock tempBlock: blockList)
    	{
    		if(tempBlock.smartBlock.blockId == blockID)
    		{
    			index++;
    			break;
    		}
    		else
    		{
    			index++;
    		}
    	}
    	
    	if(index == -1 || index == blockList.size())
    		throw new BlockNotFoundException("Model Viewer: Update block not found.");
    	
    	return index;
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
		
		createConstructionFloor();
		createBlocks();
	}
	
	private void createConstructionFloor()
	{
		window.pushMatrix();
		
		window.scale(70, 0.1f, 70);    
		window.translate(0, 10, 0);
		
		window.noStroke();
		window.fill(255);
		window.box(10);
		
		window.popMatrix();
	}
	
	private void createBlocks()
	{
		//draw block list
		for(ModelBlock block: blockList)
		{
			SmartBlock smartBlock = block.smartBlock;
			window.scale(10, 10, 10);
			window.beginShape(PConstants.TRIANGLES);
			
			for(int x = 0; x < smartBlock.indices.length; ++x)
			{
				Vec3 vertex = smartBlock.vertices[smartBlock.indices[x]];
				window.vertex((float)vertex.x, (float)vertex.y, (float)vertex.z);
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