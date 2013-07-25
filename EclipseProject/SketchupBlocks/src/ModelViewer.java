import java.util.ArrayList;

import peasy.PeasyCam;
import processing.core.*;

class ModelViewer implements ModelChangeListener
{
	private PApplet window;
	private Lobby lobby;
	private ArrayList<ModelBlock> blockList = null;
	private PeasyCam cam;
	private PeasyCam[] systemCameras;
	
	public ModelViewer()
	{
		
	}
	
	public void updateCameraPosition(int cameraId, Vec3 pos)
	{
				
	}
	
	public void setSystemCamera(int index, PeasyCam newCamera)
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
		
		cam = new PeasyCam(window, 200);
		cam.setMinimumDistance(100);
		cam.setMaximumDistance(400);
		cam.setWheelScale(2.0f);
		cam.setRotations(0.5, 0.0, 0.0);
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
		//setup scene
		//window.lights();
		window.pointLight(135, 196, 250, 100, -100, 100);
		window.pointLight(200, 200, 200, -500, -1000, 1000);
		window.background(0);
		
		//construction floor
		//###################
		window.pushMatrix();
		
		window.scale(30, 0.1f, 30);    
		window.translate(0, 10, 0);
		
		window.noStroke();
		window.fill(255);
		window.box(10);
		
		window.popMatrix();
		//###################
		
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
	
	private void createConstructionFloor()
	{
		
	}
	
	private void createBlocks()
	{
		
	}
}