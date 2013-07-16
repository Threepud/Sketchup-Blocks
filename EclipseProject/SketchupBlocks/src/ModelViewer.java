import processing.core.PApplet;

class ModelViewer implements ModelChangeListener
{
	PApplet window;
	Lobby lobby;
	
	public ModelViewer()
	{
		
	}
	
	public void setWindow(PApplet _window)
	{
		window = _window;
	}
	
	public void setLobby(Lobby _lobby)
	{
	    lobby = _lobby;
	}
	  
	public void fireModelChangeEvent(ModelBlock change)
	{
	    
	}
	
	public void drawModel()
	{
		window.background(0);
		window.translate(300, 300, 0);
		    
		window.lights();
		    
		window.noStroke();
		window.fill(255);
		window.sphere(50);
	}
}