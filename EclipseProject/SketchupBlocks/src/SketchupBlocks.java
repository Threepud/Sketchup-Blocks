
import processing.core.*;

public class SketchupBlocks extends PApplet 
{
	Interpreter wimpie;
	SessionManager sessMan;
	
	public void setup()
	{
		size(displayWidth, displayHeight, P3D);
		frameRate(60);
		
		if (frame != null) 
		{
			frame.setResizable(false);
		}
		
		sessMan = new SessionManager(this);
		sessMan.setModelConstructor(new ModelConstructor(sessMan));
		for (int k = 0;  k < Settings.numCameras; k++)
		{
			wimpie = new Interpreter(Settings.cameraSettings[k].port, sessMan, this,k);
		}
	}

	public void draw() 
	{
		sessMan.drawGUI();
	}
	
	public static void main(String args[]) 
	{
		String[] arguments = {"--present"};
		PApplet.main("SketchupBlocks", arguments);
	}
	
	public boolean sketchFullScreen() 
	{
		return true;
	}
}
