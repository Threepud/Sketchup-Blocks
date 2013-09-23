package sketchupblocks.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import sketchupblocks.base.Model;
import sketchupblocks.base.ModelBlock;
import sketchupblocks.base.ModelChangeListener;
import sketchupblocks.exception.ModelNotSetException;


public class NetworkedLobby extends Thread implements Lobby
{
	private Socket connection;
	private Model model;
	private ArrayList<ModelChangeListener> modelChangeListeners = new ArrayList<ModelChangeListener>();
	private boolean online = true;

	public NetworkedLobby(String server, int port) throws Exception
	{
		connection = new Socket(server,port);
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

	public void registerChangeListener(ModelChangeListener listener)
	{
		modelChangeListeners.add(listener);
	}

	@Override
	public void run()
	{
		while(online)
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
				System.out.println(e);
			}
		}
	}
	
	public void stopLobby()
	{
		online = false;
		try 
		{
			if(!connection.isClosed())
				connection.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}