import processing.core.PApplet;

class SessionManager
{
	private PApplet parent;
	private ModelConstructor jimmy;
	private Exporter kreshnik;
	private ModelViewer sarah;
	private BlockDatabase blockDB;
	
	public SessionManager(PApplet _parent)
	{
		parent = _parent;
	}
	
    public void onCameraEvent(CameraEvent cameraEvent)
    {
    	parent.println("Received camera event");
    	//We still require some sort of menu traversal scheme...
    	
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
    
    public void spectate()
    {
      
    }
}