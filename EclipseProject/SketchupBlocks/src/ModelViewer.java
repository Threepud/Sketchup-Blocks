import java.util.ArrayList;

import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PConstants;

class ModelViewer implements ModelChangeListener
{
	PApplet window;
	Lobby lobby;
	ArrayList<ModelBlock> blockList = null;
	PeasyCam cam;
	PeasyCam[] systemCameras;
	
	public ModelViewer()
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
		
		cam = new PeasyCam(window, 1000);
		cam.setMinimumDistance(50);
		cam.setMaximumDistance(2000);
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
	}
	  
	public void fireModelChangeEvent(ModelBlock change) throws RuntimeException
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
	
	private int findBlockIndex(int blockID)
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
		window.lights();
		window.background(0);
		
		//models here
		//###################
		window.scale(100, 1, 100);    
		
		window.noStroke();
		window.fill(255);
		window.box(10);
		//###################
	}
}