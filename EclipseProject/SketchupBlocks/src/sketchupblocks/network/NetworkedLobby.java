package sketchupblocks.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import sketchupblocks.base.Model;
import sketchupblocks.base.ModelChangeListener;
import sketchupblocks.base.SessionManager;
import sketchupblocks.construction.ModelBlock;
import sketchupblocks.exception.ModelNotSetException;
import sketchupblocks.gui.Menu;


public class NetworkedLobby extends Thread implements Lobby
{
	private Socket connection;
	private Model model;
	private ArrayList<ModelChangeListener> modelChangeListeners = new ArrayList<ModelChangeListener>();
	private boolean online = true;
	private Menu menu;
	private SessionManager sessMan;
	
	//save this for later reconnection
	private String server;
	private int port;

	public NetworkedLobby(String _server, int _port, Menu _menu, SessionManager _sessMan) throws Exception
	{
		server = _server;
		port = _port;
		
		connection = new Socket(server,port);
		menu = _menu;
		sessMan = _sessMan;
	}

	public void updateModel(ModelBlock modelBlock)
	{
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
			catch(Exception e)
			{
				if(online)
					e.printStackTrace();
				try 
				{
					if(online)
					{
						online = false;
						connection.close();
						
						//reconnect here
						menu.createReconnectPopup();
						
						Thread t = new Thread()
						{
							public void run()
							{
								try 
								{
									connection = new Socket(server,port);
									menu.updateNetworkStatus(true);
									sessMan.clearState();
									online = true;
								} 
								catch (Exception e) 
								{
									menu.updateNetworkStatus(false);
									menu.checkCalibrated();
									sessMan.spectate(null);
									e.printStackTrace();
								}
							}
						};
						
						t.start();
						
						try 
						{
							t.join();
						} 
						catch (InterruptedException e1) 
						{
							e1.printStackTrace();
							online = false;
						}
					}
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