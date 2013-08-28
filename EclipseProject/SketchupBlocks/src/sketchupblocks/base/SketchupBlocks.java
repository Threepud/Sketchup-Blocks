package sketchupblocks.base;

import processing.core.*;
import sketchupblocks.recording.Feeder;
import sketchupblocks.recording.Recorder;

public class SketchupBlocks extends PApplet 
{
	private static final long serialVersionUID = 1L;
	
	Interpreter[] wimpie;
	SessionManager sessMan;
	Settings settings;
	
	public void setup()
	{
		size(displayWidth, displayHeight, P3D);
		frameRate(60);
		
		if (frame != null) 
		{
			frame.setResizable(false);
		}
		
		settings = new Settings("Settings.xml");
		sessMan = new SessionManager(this);
		//sessMan.setModelConstructor(new ModelConstructor(sessMan));
		wimpie = new Interpreter[Settings.numCameras];
		if (Settings.liveData)
		{
			for (int k = 0;  k < Settings.numCameras; k++)
			{
				wimpie[k] = new Interpreter(Settings.cameraSettings[k].port, sessMan, this, k);
			}
		}
		else
		{
			for (int k = 0;  k < Settings.numCameras; k++)
			{
				wimpie[k] = new Feeder(sessMan, this, k);
				((Feeder)wimpie[k]).start();
			}
		}
	}

	public void draw() 
	{
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
