package sketchupblocks.base;

import processing.core.PApplet;
import sketchupblocks.database.Block;
import sketchupblocks.database.BlockDatabase;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.database.UserBlock;
import sketchupblocks.gui.ModelViewer;
import sketchupblocks.math.Vec3;
import sketchupblocks.menu.Menu;
import sketchupblocks.menu.Slot;
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
	
	public Slot[] projectSlots;
	
	public SessionManager(PApplet _parent)
	{
		dbPaths = new String[3];
		dbPaths[0] = "./dbs/SmartBlock.dat";
		dbPaths[1] = "./dbs/CommandBlock.dat";
		dbPaths[2] = "./dbs/UserBlock.dat";
		
		parent = _parent;
		
		lobby = new LocalLobby();
		
		sarah = new ModelViewer();
		sarah.setLobby(lobby);
		sarah.setWindow(parent);
		
    	try 
    	{
			blockDB = new BlockDatabase(dbPaths[0], dbPaths[1], dbPaths[2]);
		} 
    	catch (Exception e) 
    	{
			e.printStackTrace();
		}
    	
    	menu = new Menu(this);
    	projectSlots = new Slot[Settings.numSlots];
    	for (int k = 0; k < projectSlots.length; k++)
    	{
    		projectSlots[k] = new Slot(Settings.slotName+k);
    	}
    	
    	cameraPositions = new Vec3[Settings.numCameras];
	}
	
    public void onCameraEvent(CameraEvent cameraEvent)
    {
    	PApplet.println("Received camera event");
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
    		InputBlock iblock = new InputBlock(block, cameraEvent);
    		if(jimmy != null)
        		jimmy.receiveBlock(iblock);
    	}
    	else if (block instanceof SmartBlock)
    	{
    		if (Settings.verbose >= 3)
    			System.out.println("--Recognized smart block--");
    		InputBlock iblock = new InputBlock(block, cameraEvent);
    		if(jimmy != null)
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
    	sarah.setLobby(lobby);
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