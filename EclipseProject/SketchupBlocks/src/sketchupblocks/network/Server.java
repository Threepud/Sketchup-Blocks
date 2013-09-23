package sketchupblocks.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.io.ObjectOutputStream;

import sketchupblocks.base.ModelBlock;
import sketchupblocks.base.ModelChangeListener;

public class Server extends Thread implements ModelChangeListener 
{
	private int port;
	private ServerSocket listener;
	private Lobby lobby;
	private ArrayList<Socket> clients;
	private boolean online = true;
	private ConcurrentHashMap<Integer,ModelBlock> blockMap;
  
	public Server(Lobby _lobby, int _port) throws Exception
	{
		lobby = _lobby;
		port = _port;
		listener = new ServerSocket(port);
		
		clients = new ArrayList<>();
		lobby.registerChangeListener(this);
		
		blockMap = new ConcurrentHashMap<>();
	}
  
	@Override
	public void fireModelChangeEvent(ModelBlock change)
	{
		if(change.type == ModelBlock.ChangeType.UPDATE)
			blockMap.put(new Integer(change.smartBlock.blockId), change);
		else
			blockMap.remove(new Integer(change.smartBlock.blockId));
		
		//relay to each client
		Socket[] connections = new Socket[0];
		connections = clients.toArray(connections);
		
		for(int k = 0 ; k < connections.length ; k++)
		{
			sendData(connections[k], change);
		}
	}
	
	private void sendData(Socket socket, ModelBlock block)
	{
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(block);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	@Override
	public void run()
	{
		while(online)
		{
			try
			{
				clients.add(listener.accept());
				
				//TODO: replace to run in separate thread
				//check for late registeration - flush model to client
				if(!blockMap.isEmpty())
				{
					for(ModelBlock block: blockMap.values())
					{
						sendData(clients.get(clients.size() - 1), block);
					}
				}
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
	}
	
	public void stopServer()
	{
		online = false;
	}
}