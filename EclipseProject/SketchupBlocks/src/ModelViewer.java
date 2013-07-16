import java.util.ArrayList;
import processing.core.PApplet;

class ModelViewer implements ModelChangeListener
{
	PApplet window;
	Lobby lobby;
	ArrayList<ModelBlock> blockList = null;
	
	public ModelViewer()
	{
		
	}
	
	public void setWindow(PApplet _window)
	{
		window = _window;
	}
	
	public void setLobby(Lobby _lobby) throws RuntimeException
	{
	    lobby = _lobby;
	    Model model = lobby.getModel();
	    
	    if(model == null)
	    	throw new ModelNotSetException("Model Viewer: Model not set.");
	    else
	    	blockList = new ArrayList<>(model.getBlocks());
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
		window.background(0);
		window.translate(300, 300, 0);
		    
		window.lights();
		    
		window.noStroke();
		window.fill(255);
		window.sphere(50);
	}
}