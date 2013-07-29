import java.util.ArrayList;

import peasy.CameraState;
import peasy.PeasyCam;
import peasy.org.apache.commons.math.geometry.CardanEulerSingularityException;
import peasy.org.apache.commons.math.geometry.Rotation;
import peasy.org.apache.commons.math.geometry.RotationOrder;
import peasy.org.apache.commons.math.geometry.Vector3D;
import processing.core.*;

class ModelViewer implements ModelChangeListener
{
	private PApplet window;
	private Lobby lobby;
	private ArrayList<ModelBlock> blockList = null;
	private PeasyCam cam;
	private Camera[] systemCameras;
	
	public ModelViewer()
	{
		systemCameras = new Camera[Settings.numCameras];
		for(int x = 0; x < Settings.numCameras; ++x)
		{
			systemCameras[x] = new Camera();
		}
	}
	
	private int counter = -100;
	private Vector3D oldPos = null;
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
		cam.setSuppressRollRotationMode();
		cam.setActive(false);
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
	    SmartBlock smartBlock = new SmartBlock();
	    
	    Vec3[] vertices = new Vec3[24];
	    int i = 0;
	    vertices[i++] = new Vec3(-1.000000, 1.000000, -1.000000);
	    vertices[i++] = new Vec3(1.000000, 1.000000, -1.000000);
	    vertices[i++] = new Vec3(1.000000, 1.000000, 1.000000);
	    vertices[i++] = new Vec3(-1.000000, 1.000000, 1.000000);
	    vertices[i++] = new Vec3(-1.000000, -1.000000, -0.999999);
	    vertices[i++] = new Vec3(0.999999, -1.000000, -1.000001);
	    vertices[i++] = new Vec3(1.000000, -1.000000, 1.000000);
	    vertices[i++] = new Vec3(-1.000000, -1.000000, 1.000000);
	    smartBlock.vertices = vertices;
	    
	    int[] indices = {4,8,7,4,7,3,  8,5,6,8,6,7,   3,7,6,3,6,2,   4,2,1,4,3,2,   4,5,8,4,1,5,   1,6,5,1,2,6};
	    
	    smartBlock.indices = indices;
	    
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
		window.camera((float)systemCameras[0].eye.x, (float)systemCameras[0].eye.y, (float)systemCameras[0].eye.z,
			   (float)systemCameras[0].at.x, (float)systemCameras[0].at.y, (float)systemCameras[0].at.z,
			   (float)systemCameras[0].up.x, (float)systemCameras[0].up.y, (float)systemCameras[0].up.z);
		
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
			window.scale(25, 25, 25);
			window.beginShape(PConstants.TRIANGLES);
			
			for(int x = 0; x < smartBlock.indices.length; ++x)
			{
				Vec3 vertex = smartBlock.vertices[smartBlock.indices[x] - 1];
				window.vertex((float)vertex.x, (float)vertex.y, (float)vertex.z);
			}
			
			window.endShape();
		}
	}
}