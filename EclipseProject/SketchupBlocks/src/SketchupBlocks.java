
import processing.core.*;

public class SketchupBlocks extends PApplet 
{

	/*public static void main(String args[]) 
	{
		    PApplet.main(new String[] { "--present", "SketchupBlocks" });
	}*/
	
	Interpreter wimpie;
	SessionManager sessMan;
	
	public void setup()
	{
		size(displayWidth - 15, displayHeight - 110, P3D);
		frameRate(60);
		
		if (frame != null) {
			frame.setResizable(false);
		}
		
		sessMan = new SessionManager(this);
		for (int k = 0;  k < Settings.numCameras; k++)
		{
			wimpie = new Interpreter(Settings.cameraSettings[k].port, sessMan, this,k);
		}
	}

	public void draw() 
	{
		sessMan.drawGUI();
	}
}
