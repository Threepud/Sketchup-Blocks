
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
		
		sessMan = new SessionManager(this);
		for (int k = 0;  k < Settings.numCameras; k++)
		{
			wimpie = new Interpreter(Settings.cameraPorts[k], sessMan, this);
			
		}
	}

	public void draw() 
	{
	
	}
}
