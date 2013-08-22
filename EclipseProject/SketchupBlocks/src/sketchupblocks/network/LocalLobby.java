package sketchupblocks.network;

import java.util.ArrayList;

import sketchupblocks.base.Model;
import sketchupblocks.base.ModelBlock;
import sketchupblocks.base.ModelChangeListener;
import sketchupblocks.exception.ModelNotSetException;

public class LocalLobby implements Lobby
{
	private Model model;
	private ArrayList<ModelChangeListener> modelChangeListeners = new ArrayList<ModelChangeListener>();
	  
	public void updateModel(ModelBlock modelBlock)
	{
		model.addModelBlock(modelBlock);
		modelChangeListeners.trimToSize();
		for (int k = 0; k < modelChangeListeners.size(); k++)
		{
			try 
			{
				modelChangeListeners.get(k).fireModelChangeEvent(modelBlock);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	    
	public Model getModel() throws Exception
	{
		if(model == null)
			throw new ModelNotSetException("Local Lobby: Model not set.");
		return model;
	}
	    
	public void setModel(Model _model)
	{
		model = _model;
	}
	    
	public void registerChangeListener(ModelChangeListener _listener)
	{
		modelChangeListeners.add(_listener);
	}
}