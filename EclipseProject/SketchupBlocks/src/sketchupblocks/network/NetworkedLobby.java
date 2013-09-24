package sketchupblocks.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import sketchupblocks.base.Model;
import sketchupblocks.base.ModelBlock;
import sketchupblocks.base.ModelChangeListener;
import sketchupblocks.exception.ModelNotSetException;
import sketchupblocks.gui.Menu;


public class NetworkedLobby extends Thread implements Lobby
{
	private Socket connection;
	private Model model;
	private ArrayList<ModelChangeListener> modelChangeListeners = new ArrayList<ModelChangeListener>();
	private boolean online = true;
	private Menu menu;

	public NetworkedLobby(String server, int port, Menu _menu) throws Exception
	{
		connection = new Socket(server,port);
		menu = _menu;
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
				InputStream is = connection.getInputStream();
				ObjectInputStream inp = new ObjectInputStream(is);
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
				e.printStackTrace();
				try 
				{
					online = false;
					connection.close();
					return;
				}
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
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