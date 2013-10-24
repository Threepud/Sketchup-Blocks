package sketchupblocks.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.io.ObjectOutputStream;

import sketchupblocks.base.ModelChangeListener;
import sketchupblocks.base.Settings;
import sketchupblocks.construction.ModelBlock;

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
  
	public void replaceLobby(Lobby _lobby)
	{
		lobby = _lobby;
		lobby.registerChangeListener(this);
		clearNetworkModel();
		blockMap.clear();
	}
	
	private void clearNetworkModel()
	{
		ArrayList<ModelBlock> blocks = new ArrayList<>(blockMap.values());
		
		//relay to each client
		Socket[] connections = new Socket[0];
		connections = clients.toArray(connections);
		
		for(int k = connections.length - 1 ; k >= 0; k--)
		{
			try 
			{
				for(int x = 0; x < blocks.size(); ++x)
				{
					blocks.get(x).type = ModelBlock.ChangeType.REMOVE;
					sendData(connections[k], blocks.get(x));
				}
			}
			catch (Exception e) 
			{
				System.out.println(e);
				if(Settings.verbose >= 3)
					System.out.println("Closing connection to client: " + k);
				
				clients.remove(k);
			}
		}
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
		
		for(int k = connections.length - 1 ; k >= 0; k--)
		{
			try 
			{
				sendData(connections[k], change);
			}
			catch (Exception e) 
			{
				System.out.println(e);
				if(Settings.verbose >= 3)
					System.out.println("Closing connection to client: " + k);
				
				clients.remove(k);
			}
		}
	}
	
	private void sendData(Socket socket, ModelBlock block) throws Exception
	{
		synchronized(socket)
		{
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(block);
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
				
				if(!blockMap.isEmpty())
				{
					Thread t = new Thread()
					{
						public void run()
						{
							for(ModelBlock block: blockMap.values())
							{
								System.out.println("Flush: " + block.smartBlock.blockId);
								try 
								{
									sendData(clients.get(clients.size() - 1), block);
								}
								catch (Exception e) 
								{
									if(online)
									{
										System.out.println(e);
										if(Settings.verbose >= 3)
											System.out.println("Closing connection to client: " + (clients.size() - 1));
										
										clients.remove(clients.size() - 1);
									}
								}
							}
						}
					};
					
					t.start();
				}
			}
			catch(Exception e)
			{
				if(online)
					e.printStackTrace();
			}
		}
	}
	
	public void stopServer()
	{
		online = false;
		try 
		{
			if(!listener.isClosed())
				listener.close();
		} 
		catch (IOException e) 
		{
			System.out.println(e);
		}
	}
}