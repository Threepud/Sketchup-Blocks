import java.util.ArrayList;

class LocalLobby implements Lobby
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
	    
	public Model getModel()
	{
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