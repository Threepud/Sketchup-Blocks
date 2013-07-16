import java.net.Socket;
import java.util.ArrayList;


class NetworkedLobby extends Thread implements Lobby
{
	Socket connection;
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
			modelChangeListeners.get(k).fireModelChangeEvent(modelBlock);
		}
  }
  
  public Model getModel()
  {
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
	  
	  
  }
  
}