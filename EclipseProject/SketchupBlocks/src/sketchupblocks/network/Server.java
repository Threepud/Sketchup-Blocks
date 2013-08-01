package sketchupblocks.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.ObjectOutputStream;

import sketchupblocks.base.ModelBlock;
import sketchupblocks.base.ModelChangeListener;

class Server extends Thread implements ModelChangeListener 
{
	private int port;
	private ServerSocket listner;
	private Lobby lobby;
	private ArrayList<Socket> clients;
  
	public Server(Lobby _lobby, int _port)
	{
		lobby = _lobby;
		port = _port;
		try
		{
		listner = new ServerSocket(_port);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		clients = new ArrayList<Socket>();
		lobby.registerChangeListener(this);
	}
  
	@Override
	public
	void fireModelChangeEvent(ModelBlock change)
	{
		//relay to each client
		Socket [] connections = new Socket[0];
		connections = clients.toArray(connections);
		
		for(int k = 0 ; k < connections.length ; k++)
		{
			try
			{
			ObjectOutputStream out = new ObjectOutputStream(connections[k].getOutputStream());
			out.writeObject(change);
			}
			catch(Exception e)
			{
				
			}
		}
		
	}
	
	@Override
	public void run()
	{
		while(true)
		try
		{
		clients.add(listner.accept());
		}
		catch(Exception e)
		{
			
		}
	
	}
}