package sketchupblocks.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import peasy.PeasyCam;
import peasy.org.apache.commons.math.geometry.Vector3D;
import processing.core.*;
import sketchupblocks.base.ColladaLoader;
import sketchupblocks.base.ModelBlock;
import sketchupblocks.base.ModelChangeListener;
import sketchupblocks.base.Settings;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.exception.BlockNoTypeException;
import sketchupblocks.exception.BlockNotFoundException;
import sketchupblocks.math.Vec3;
import sketchupblocks.network.Lobby;

public class ModelViewer implements ModelChangeListener, KeyListener
{
	private PApplet window;
	private Lobby lobby;
	private ArrayList<ModelBlock> blockList = null;
	private PeasyCam cam;
	private Camera[] systemCameras;
	private Camera currentCamera;
	
	public ModelViewer()
	{
		systemCameras = new Camera[Settings.numCameras];
		for(int x = 0; x < Settings.numCameras; ++x)
		{
			systemCameras[x] = new Camera();
		}
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
	
	private void switchCamera(PeasyCam newCamera)
	{
		//TODO: Find way to switch to system camera's
	}
	
	public void setWindow(PApplet _window)
	{
		window = _window;
		
		cam = new PeasyCam(window, 500);
		cam.setMinimumDistance(200);
		cam.setMaximumDistance(500);
		cam.setWheelScale(2.0f);
		//cam.setActive(false);
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
	    SmartBlock smartBlock = ColladaLoader.getSmartBlock("./models/GoogleCube.dae");
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
		if(!cam.isActive())
		{
			window.camera((float)currentCamera.eye.x, (float)currentCamera.eye.y, (float)currentCamera.eye.z,
				   (float)currentCamera.at.x, (float)currentCamera.at.y, (float)currentCamera.at.z,
				   (float)currentCamera.up.x, (float)currentCamera.up.y, (float)currentCamera.up.z);
		}
			
		//setup scene
		//window.lights();
		window.pointLight(155, 216, 250, 100, -100, -100);
		window.pointLight(200, 200, 200, -500, -1000, 1000);
		window.background(0);
		
		createConstructionFloor();
		createBlocks();
	}
	
	private void createConstructionFloor()
	{
		window.pushMatrix();
		
		window.scale(50, 0.1f, 50);    
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
			//window.scale(25, 25, 25);
			window.beginShape(PConstants.TRIANGLES);
			
			for(int x = 0; x < smartBlock.indices.length; ++x)
			{
				Vec3 vertex = smartBlock.vertices[smartBlock.indices[x]];
				window.vertex((float)vertex.x, (float)vertex.y, (float)vertex.z);
			}
			
			window.endShape();
		}
	}

	@Override
	public void keyPressed(KeyEvent event) 
	{
		char c = event.getKeyChar();
		System.out.println(c);
	}

	@Override
	public void keyReleased(KeyEvent event) 
	{
		char c = event.getKeyChar();
		System.out.println(c);
	}

	@Override
	public void keyTyped(KeyEvent event) 
	{
		char c = event.getKeyChar();
		System.out.println(c);
	}
}