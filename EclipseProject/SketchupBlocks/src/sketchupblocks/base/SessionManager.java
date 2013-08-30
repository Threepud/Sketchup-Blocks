package sketchupblocks.base;

import processing.core.PApplet;
import sketchupblocks.database.Block;
import sketchupblocks.database.BlockDatabase;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.database.UserBlock;
import sketchupblocks.gui.Menu;
import sketchupblocks.gui.ModelViewer;
import sketchupblocks.math.Vec3;
import sketchupblocks.network.Lobby;
import sketchupblocks.network.LocalLobby;

public class SessionManager
{
	private PApplet parent;
	private ModelConstructor jimmy;
	private Exporter kreshnik;
	private Lobby lobby;
	private ModelViewer sarah;
	private ModelLoader modelLoader;
	private BlockDatabase blockDB;
	private Menu menu;
	private String[] dbPaths;
	private Vec3 [] cameraPositions;
	
	public SessionManager(PApplet _parent)
	{
		parent = _parent;
		
		lobby = new LocalLobby();
		lobby.setModel(new Model());
		
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
    		else if(((CommandBlock) block).type == CommandBlock.CommandType.CALIBRATE)
    		{
    			InputBlock iblock = new InputBlock(block, cameraEvent);
        		jimmy.receiveBlock(iblock);
    		}
    		else
    		{
    			menu.handleInput((CommandBlock)block, cameraEvent);
    		}
    	}
    	else if (block instanceof SmartBlock)
    	{
    		if (Settings.verbose >= 3)
    		{
    			System.out.println("--Recognized smart block--");
    			System.out.println("Camera ID: " + cameraEvent.cameraID);
    		}
    		InputBlock iblock = new InputBlock(block, cameraEvent);
			jimmy.receiveBlock(iblock);
    	}
    	else
    	{
    		if (Settings.verbose >= 1)
    			System.out.println("--Unrecognized block!!--"+ cameraEvent.fiducialID);
    	}
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
    
    public void loadProject(int slotNumber)
    {
      
    }
    
    public void newProject()
    {
      
    }
    
    public void exportToFile()
    {
      
    }
    
    public void saveProject(int slotNumber)
    {
    }
    
    public void spectate(UserBlock  user)
    {
      
    }
    
    public void drawGUI()
    {
    	sarah.drawModel();
    	menu.drawMenuOverlay();
    }
}