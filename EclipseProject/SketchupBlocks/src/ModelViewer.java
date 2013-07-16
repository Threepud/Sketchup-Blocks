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
	
	public void draw()
	{
		window.background(0.5f);
	    window.translate(100, 100, 0);
	    
	    window.lights();
	    
	    window.noStroke();
	    window.fill(0.8f);
	    window.sphere(100);
	}
}