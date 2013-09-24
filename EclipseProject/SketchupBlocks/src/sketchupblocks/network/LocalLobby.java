package sketchupblocks.network;

import java.util.ArrayList;

import sketchupblocks.base.Model;
import sketchupblocks.base.ModelChangeListener;
import sketchupblocks.construction.ModelBlock;
import sketchupblocks.exception.ModelNotSetException;

public class LocalLobby implements Lobby
{
	private Model model;
	private ArrayList<ModelChangeListener> modelChangeListeners = new ArrayList<ModelChangeListener>();
	  
	public void updateModel(ModelBlock modelBlock)
	{
		//System.out.println("In Eddy yo! " + modelChangeListeners.size());
		if(modelBlock.type == ModelBlock.ChangeType.UPDATE)
			model.addModelBlock(modelBlock);
		else
			model.removeModelBlock(modelBlock);
		modelChangeListeners.trimToSize();
		for (int k = 0; k < modelChangeListeners.size(); k++)
		{
			modelChangeListeners.get(k).fireModelChangeEvent(modelBlock);
		}
	}
	    
	public Model getModel() throws ModelNotSetException
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