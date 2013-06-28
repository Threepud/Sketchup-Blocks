import processing.core.PApplet;

class SessionManager
{
	private PApplet parent;
	private ModelConstructor jimmy;
	private Exporter kreshnik;
	private ModelViewer sarah;
	private BlockDatabase blockDB;
	private Menu menu;
	
	public SessionManager(PApplet _parent)
	{
		parent = _parent;
    	blockDB = new BlockDatabase();
    	menu = new Menu(this);
	}
	
    public void onCameraEvent(CameraEvent cameraEvent)
    {
    	parent.println("Received camera event");
    	//We still require some sort of menu traversal scheme...
    	Block block = blockDB.findBlock(cameraEvent.fiducialID);
    	if(block instanceof UserBlock)
    	{
    		System.out.println("This feature is not yet supported");
    	}
    	else if (block instanceof CommandBlock)
    	{
    		menu.handleInput((CommandBlock)block);
    	}
    	else if (block instanceof SmartBlock)
    	{
    		ConstructionBlock cblock = new ConstructionBlock((SmartBlock)block, cameraEvent);
    		jimmy.receiveBlock(cblock);
    	}
    }
    
    public void setModelConstructor(ModelConstructor _jimmy)
    {
    	jimmy = _jimmy;
    }
    
    public void setExporter(Exporter _kreshnik)
    {
    	kreshnik = _kreshnik;
    }
    
    public void setModelViewer(ModelViewer _sarah)
    {
    	sarah = _sarah;
    }
    
    public void loadProject()
    {
      
    }
    
    public void newProject()
    {
      
    }
    
    public void exportToFile()
    {
      
    }
    
    public void saveProject()
    {
    }
    
    public void spectate()
    {
      
    }
}