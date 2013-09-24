package sketchupblocks.base;

import processing.core.*;

public class SketchupBlocks extends PApplet 
{
	private static final long serialVersionUID = 1L;
	
	private static SessionManager sessMan;
	
	private static boolean created = false;
	private static long startTime;
	
	public void setup()
	{
		size(displayWidth, displayHeight, P3D);
		frameRate(60);
		
		if (frame != null) 
		{
			frame.setResizable(false);
		}
		
		Settings.readSettings("Settings.xml");
		sessMan = new SessionManager(this);
		startTime = System.currentTimeMillis();
	}

	public void draw() 
	{
		if(Settings.showSplash)
		{
			if(System.currentTimeMillis() - startTime > Settings.splashTTL && !created)
			{
				sessMan.createInterpreters();
				created = true;
			}
		}
		else if(!created)
		{
			sessMan.createInterpreters();
			created = true;
		}
		
		sessMan.drawGUI();
	}
	
	public static void main(String args[]) 
	{
		String[] arguments = {"--present"};
		PApplet.main("sketchupblocks.base.SketchupBlocks", arguments);
	}
	
	public boolean sketchFullScreen() 
	{
		return true;
	}
}
