package sketchupblocks.network;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import sketchupblocks.base.Model;
import sketchupblocks.base.ModelBlock;
import sketchupblocks.base.ModelChangeListener;
import sketchupblocks.exception.ModelNotSetException;


class NetworkedLobby extends Thread implements Lobby
{
	private Socket connection;
	private Model model;
	private ArrayList<ModelChangeListener> modelChangeListeners = new ArrayList<ModelChangeListener>();
	  	
  public NetworkedLobby(String server, int port)
  {
	  try
	  {
	  connection = new Socket(server,port);
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	  
  }
	
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
  
  	public Model getModel() throws ModelNotSetException
	{
		if(model == null)
			throw new ModelNotSetException("Local Lobby: Model not set.");
		return model;
	}
  
  public void setModel(Model model)
  {
	  
  }
  
  public void registerChangeListener(ModelChangeListener listener)
  {
	  modelChangeListeners.add(listener);
  }
  
  @Override
  public void run()
  {
	 while(true)
	 {
		 try
		 {
		 ObjectInputStream inp = new ObjectInputStream(connection.getInputStream());
		 ModelBlock modelBlock = (ModelBlock) inp.readObject();
		 	model.addModelBlock(modelBlock);
		 	
			modelChangeListeners.trimToSize();
			for (int k = 0; k < modelChangeListeners.size(); k++)
			{
				modelChangeListeners.get(k).fireModelChangeEvent(modelBlock);
			}
		 }
		 catch(Exception e)
		 {
			 
		 }
		 
	 }
	  
  }
  
}