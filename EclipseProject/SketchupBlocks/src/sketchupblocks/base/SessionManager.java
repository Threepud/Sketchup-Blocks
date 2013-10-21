package sketchupblocks.base;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.event.KeyEvent;
import sketchupblocks.construction.EnvironmentAnalyzer;
import sketchupblocks.construction.ModelBlock;
import sketchupblocks.construction.ModelConstructor;
import sketchupblocks.database.Block;
import sketchupblocks.database.BlockDatabase;
import sketchupblocks.database.CommandBlock;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.database.UserBlock;
import sketchupblocks.exception.ModelNotSetException;
import sketchupblocks.gui.Menu;
import sketchupblocks.gui.ModelViewer;
import sketchupblocks.network.*;
import sketchupblocks.recording.Feeder;

public class SessionManager
{
	private PApplet parent;
	private ModelConstructor jimmy;
	private Exporter kreshnik;
	private Lobby lobby;
	private Server server;
	private ModelViewer sarah;
	private ModelLoader modelLoader;
	private BlockDatabase blockDB;
	private Menu menu;
	private String[] dbPaths;
	private Interpreter[] wimpie;
	
	private final ModelViewerEventListener modelViewerEventListener = new ModelViewerEventListener();
	
	private boolean spectating = false;
	private boolean connecting = false;
	
	private boolean startupStatus = false;
	
	protected SessionManager()
	{
	}
	
	public SessionManager(PApplet _parent, boolean status)
	{
		parent = _parent;
		startupStatus = status;
		parent.registerMethod("keyEvent", modelViewerEventListener);
		
		lobby = new LocalLobby();
		lobby.setModel(new Model());
		
		try
		{
			server = new Server(lobby, Settings.hostPort);
		}
		catch(Exception e)
		{
			System.out.println(e);
			System.exit(-1);
		}
		server.start();
		
		sarah = new ModelViewer();
		
		try 
		{
			EnvironmentAnalyzer.setLobby(lobby);
			
			sarah.setLobby(lobby);
			sarah.setWindow(parent);
			sarah.createDebugViewer();
			menu = new Menu(this, parent, startupStatus);
			
			jimmy = new ModelConstructor(this);
			jimmy.setLobby(lobby);
			
			dbPaths = new String[3];
			dbPaths[0] = "./dbs/SmartBlock.dat";
			dbPaths[1] = "./dbs/CommandBlock.dat";
			dbPaths[2] = "./dbs/UserBlock.dat";
		
			blockDB = new BlockDatabase(dbPaths[0], dbPaths[1], dbPaths[2]);
		} 
    	catch (Exception e) 
    	{
			e.printStackTrace();
		}
	}
	
    public void onCameraEvent(CameraEvent cameraEvent)
    {
    	Block block = blockDB.findBlock(cameraEvent.fiducialID);
    	
    	if(block instanceof UserBlock)
    	{
    		menu.handleInput(block, cameraEvent);
    	}
    	else if (block instanceof CommandBlock)
    	{
    		Logger.log("--Recognized command block--", 99);
    		//Rotate model viewer
    		if(((CommandBlock) block).type == CommandBlock.CommandType.ROTATE)
    		{
    			sarah.rotateView(cameraEvent);
    		}
    		else if(((CommandBlock) block).type == CommandBlock.CommandType.CALIBRATE && !spectating)
    		{
    			InputBlock iblock = new InputBlock(block, cameraEvent);
        		jimmy.receiveBlock(iblock);
    		}
    		else
    		{
    			menu.handleInput(block, cameraEvent);
    		}
    	}
    	else if (block instanceof SmartBlock && !spectating)
    	{
    		Logger.log("--Recognized smart block--\n"+"Camera ID: " + cameraEvent.cameraID, 98);
    		InputBlock iblock = new InputBlock(block, cameraEvent);
			jimmy.receiveBlock(iblock);
    	}
    	else if(spectating)
    	{
    		Logger.log("Spectating. No live data accepted.", 90);
    	}
    	else
    	{
    		Logger.log("--Unrecognized block!!--"+ cameraEvent.fiducialID, 87);
    	}
    }
    
    public void createInterpreters()
    {
    	wimpie = new Interpreter[Settings.numCameras];
		if (Settings.liveData)
		{
			for (int k = 0;  k < Settings.numCameras; k++)
			{
				wimpie[k] = new Interpreter(Settings.cameraSettings[k].port, this, parent, k);
			}
		}
		else
		{
			for (int k = 0;  k < Settings.numCameras; k++)
			{
				wimpie[k] = new Feeder(this, parent, k);
				((Feeder)wimpie[k]).start();
			}
		}
    }
    
    public void clearState()
    {
    	lobby.setModel(new Model());
    }
    
    public void setModelConstructor(ModelConstructor _jimmy)
    {
    	jimmy = _jimmy;
    	jimmy.setLobby(lobby);
    }
    
    public ModelConstructor getModelConstructor()
    {
    	return jimmy;
    }
    
    public void setExporter(Exporter _kreshnik)
    {
    	kreshnik = _kreshnik;
    	kreshnik.setLobby(lobby);
    }
    
    public Exporter getExporter()
    {
    	return kreshnik;
    }
    
    public void setModelViewer(ModelViewer _sarah)
    {
    	sarah = _sarah;
    	try
    	{
			sarah.setLobby(lobby);
		} 
    	catch (Exception e) 
    	{
			e.printStackTrace();
			System.exit(-1);
		}
    }
    
    public ModelViewer getModelViewer()
    {
    	return sarah;
    }
    
    public void setModelLoader(ModelLoader _modelLoader)
    {
    	modelLoader = _modelLoader;
    	modelLoader.setLobby(lobby);
    }
    
    public ModelLoader getModelLoader()
    {
    	return modelLoader;
    }
    
    public boolean checkModelExists()
    {
    	try 
		{
			Model tempModel;
			tempModel = lobby.getModel();
			ArrayList<ModelBlock> tempArr = new ArrayList<>(tempModel.getBlocks());
			
			return !tempArr.isEmpty();
		}
		catch (ModelNotSetException e) 
		{
			e.printStackTrace();
			return false;
		}
    }
    
    public void exportToFile()
    {
    	if(!checkModelExists())
			return;
		
		Model model = null;
		try
		{
			model = lobby.getModel();
		}
		catch(ModelNotSetException e)
		{
			e.printStackTrace();
			return;
		}
		ArrayList<ModelBlock> blocks = new ArrayList<>(model.getBlocks());
		ColladaLoader.export(blocks);
    }
    
    public void spectate(final UserBlock uBlock)
    {
    	if(spectating)
    	{
    		((NetworkedLobby)lobby).stopLobby();
    		lobby = null;
    		lobby = new LocalLobby();
    		lobby.setModel(new Model());
    		try 
    		{
				sarah.setLobby(lobby);
				sarah.createDebugViewer();
				EnvironmentAnalyzer.setLobby(lobby);
				jimmy.setLobby(lobby);
			} 
    		catch (Exception e1) 
    		{
				e1.printStackTrace();
				System.exit(-1);
			}
    		
    		try
    		{
    			server = new Server(lobby, Settings.hostPort);
    		}
    		catch(Exception e)
    		{
    			System.out.println(e);
    			System.exit(-1);
    		}
    		server.start();
    		
    		//menu.forceStopConnectPopup();
    		menu.checkCalibrated();
    		
    		spectating = !spectating;
    		
    		if(Settings.verbose >= 3)
    			System.out.println("Disconnected.");
    	}
    	else
    	{
    		if(!spectating && connecting)
    			return;
    		
    		connecting = true;
    		final SessionManager sessionManager = this;
    		
    		Thread t = new Thread()
    		{
    			public void run()
    			{
    				try
    	    		{
    					menu.createConnectPopup();
    					
    					NetworkedLobby temp;
    					if(uBlock == null)
    						//debug: manual address here
    						temp = new NetworkedLobby("192.168.137.1", Settings.connectPort, menu, sessionManager);
    					else
    						temp = new NetworkedLobby(uBlock.address, Settings.connectPort, menu, sessionManager); 
    	    			lobby = temp;
    	    			
    	    			server.stopServer();
    	    			server = null;
    	        		
    	            	lobby.setModel(new Model());
    	            	sarah.setLobby(lobby);
    	            	sarah.createDebugViewer();
    	            	((NetworkedLobby)lobby).start();
    	            	
    	            	spectating = !spectating;
    	            	connecting = false;
    	            	
    	            	menu.updateNetworkStatus(true);
    	            	
    	            	if(Settings.verbose >= 3)
    	        			System.out.println("Connected.");
    	    		}
    	    		catch(Exception e)
    	    		{
    	    			menu.updateNetworkStatus(false);
    	    			connecting = false;
    	    			menu.checkCalibrated();
    	    			
    	    			System.out.println(e);
    	    			return;
    	    		}
    			}
    		};
    		
    		t.start();
    	}
    }
    
    public void drawGUI()
    {
    	sarah.drawModel();
    	menu.drawMenuOverlay();
    }
    
    private void viewerFeedKeyboard(KeyEvent event)
    {
    	sarah.setKeyboardInput(event);
    }
    
    private void resetSystem()
    {
    	String resetString = 
    			  "#################################\n"
    			+ "#             RESET             #\n"
    			+ "#################################\n";
    	Logger.log(resetString, 1);
    	
    	pauseInput();
    	
    	lobby = new LocalLobby();
		lobby.setModel(new Model());
		
		server.replaceLobby(lobby);
		
		EnvironmentAnalyzer.setLobby(lobby);
		
		sarah.setLobby(lobby);
		sarah.setWindow(parent);
		try 
		{
			sarah.createDebugViewer();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		jimmy = null;
		jimmy = new ModelConstructor(this);
		jimmy.setLobby(lobby);
		
		createInterpreters();
    }
    
    private void pauseInput()
    {
    	for (int k = 0; k < wimpie.length; k++)
		{
			wimpie[k].paused = !wimpie[k].paused;
		}
    }
    
    //keyboard listener
    protected class ModelViewerEventListener
	{
		public void keyEvent(final KeyEvent e) 
		{
			if(e.getKey() == 's')
			{
				if(e.getAction() == KeyEvent.RELEASE)
					spectate(null);
			}
			else if(e.getKeyCode() == 19)
			{
				if(e.getAction() == KeyEvent.PRESS)
				{
					pauseInput();
				}
			}
			else if(e.getKey() == 'r')
			{
				if(e.getAction() == KeyEvent.RELEASE)
				{
					resetSystem();
				}
			}
			else
			{
				viewerFeedKeyboard(e);
			}
		}
	}
}