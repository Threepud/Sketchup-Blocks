import processing.core.PApplet;

class SessionManager
{
	private PApplet parent;
	
	public SessionManager(PApplet _parent)
	{
		parent = _parent;
	}
	
    public void onCameraEvent(CameraEvent cameraEvent)
    {
    	parent.println("Received camera event");
    }
    
    public void setModelConstructor(ModelConstructor jimmy)
    {
      
    }
    
    public void setExporter(Exporter kreshnik)
    {
      
    }
    
    public void setModelViewer(ModelViewer sarah)
    {
      
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