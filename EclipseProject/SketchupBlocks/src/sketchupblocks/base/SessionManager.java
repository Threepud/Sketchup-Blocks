package sketchupblocks.base;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.event.KeyEvent;
import sketchupblocks.database.Block;
import sketchupblocks.database.BlockDatabase;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.database.UserBlock;
import sketchupblocks.exception.ModelNotSetException;
import sketchupblocks.gui.Menu;
import sketchupblocks.gui.ModelViewer;
import sketchupblocks.math.Vec3;
import sketchupblocks.network.*;
import sketchupblocks.recording.Feeder;
import sketchupblocks.math.Line;

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
	private Vec3 [] cameraPositions;
	
	private final ModelViewerEventListener modelViewerEventListener = new ModelViewerEventListener();
	
	private boolean spectating = false;
	private boolean connecting = false;
	
	public SessionManager(PApplet _parent)
	{
		parent = _parent;
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
			sarah.setLobby(lobby);
		} 
		catch (Exception e1) 
		{
			System.out.println("The catz of the universe hate our programmer guts. (Just the programmer guts)");
			e1.printStackTrace();
			System.exit(-1);
		}
		sarah.setWindow(parent);
		menu = new Menu(this, parent);
		
		lobby.registerChangeListener(sarah);
		
		jimmy = new ModelConstructor(this);
		jimmy.setLobby(lobby);
		
		dbPaths = new String[3];
		dbPaths[0] = "./dbs/SmartBlock.dat";
		dbPaths[1] = "./dbs/CommandBlock.dat";
		dbPaths[2] = "./dbs/UserBlock.dat";
		
    	try 
    	{
			blockDB = new BlockDatabase(dbPaths[0], dbPaths[1], dbPaths[2]);
		} 
    	catch (Exception e) 
    	{
			e.printStackTrace();
		}
    	
    	cameraPositions = new Vec3[Settings.numCameras];
	}
	
    public void onCameraEvent(CameraEvent cameraEvent)
    {
    	//We still require some sort of menu traversal scheme...
    	Block block = blockDB.findBlock(cameraEvent.fiducialID);
    	if(block instanceof UserBlock)
    	{
    		System.out.println("This feature is not yet supported");
    	}
    	else if (block instanceof CommandBlock)
    	{
    		if (Settings.verbose >= 3)
    			System.out.println("--Recognized command block--");
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
    			menu.handleInput((CommandBlock)block, cameraEvent);
    		}
    	}
    	else if (block instanceof SmartBlock && !spectating)
    	{
    		if (Settings.verbose >= 3)
    		{
    			System.out.println("--Recognized smart block--");
    			System.out.println("Camera ID: " + cameraEvent.cameraID);
    		}
    		InputBlock iblock = new InputBlock(block, cameraEvent);
			jimmy.receiveBlock(iblock);
    	}
    	else if(spectating)
    	{
    		if(Settings.verbose >= 1)
    			System.out.println("Spectating. No live data accepted.");
    	}
    	else
    	{
    		if (Settings.verbose >= 1)
    			System.out.println("--Unrecognized block!!--"+ cameraEvent.fiducialID);
    	}
    }
    
    public void createInterpreters()
    {
    	Interpreter[] wimpie = new Interpreter[Settings.numCameras];
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
    
    public void updateCalibratedCameras(boolean[] calibrated)
    {
    	menu.updateCalibratedCameras(calibrated);
    }
    
    public void updateCameraPosition(int cameraID, Vec3 camPosition)
    {
    	cameraPositions[cameraID] = camPosition;
    	if(sarah != null)
    		sarah.updateSystemCameraPosition(cameraID, camPosition);
    	
    }
    
    public void setModelConstructor(ModelConstructor _jimmy)
    {
    	jimmy = _jimmy;
    	jimmy.setLobby(lobby);
    }
    
    public void setExporter(Exporter _kreshnik)
    {
    	kreshnik = _kreshnik;
    	kreshnik.setLobby(lobby);
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
    	for(int k = 0 ; k < cameraPositions.length ; k++)
    	{
	    	if(cameraPositions[k] != null)
	    		sarah.updateSystemCameraPosition(k, cameraPositions[k]);
    	}
    	
    }
    
    public void setModelLoader(ModelLoader _modelLoader)
    {
    	modelLoader = _modelLoader;
    	modelLoader.setLobby(lobby);
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
			System.out.println(e);
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
			System.out.println(e);
			return;
		}
		ArrayList<ModelBlock> blocks = new ArrayList<>(model.getBlocks());
		ColladaLoader.export(blocks);
    }
    
    public void spectate(UserBlock  user)
    {
    	if(spectating)
    	{
    		((NetworkedLobby)lobby).stopLobby();
    		lobby = null;
    		lobby = new LocalLobby();
    		sarah.clearModel();
    		lobby.setModel(new Model());
    		
    		lobby.registerChangeListener(sarah);
    		
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
    					
    	    			NetworkedLobby temp = new NetworkedLobby("192.168.137.82", Settings.connectPort, menu, sessionManager); 
    	    			lobby = temp;
    	    			
    	    			server.stopServer();
    	    			server = null;
    	        		
    	    			sarah.clearModel();
    	            	lobby.setModel(new Model());
    	            	lobby.registerChangeListener(sarah);
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
    	    			
    	    			System.out.println(e);
    	    			return;
    	    		}
    			}
    		};
    		
    		t.start();
    	}
    }
    
    public void debugLines(String[] IDS, Line[] lines)
    {
    	sarah.setDebugLines(IDS, lines);
    }
    
    public void debugPoints(String[] IDS, Vec3[] points)
    {
    	sarah.setDebugPoints(IDS, points);
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
    
    //keyboard listener
    protected class ModelViewerEventListener
	{
		public void keyEvent(final KeyEvent e) 
		{
			if(e.getKey() == 's')
				if(e.getAction() == KeyEvent.RELEASE)
					spectate(null);
			
			viewerFeedKeyboard(e);
		}
	}
}