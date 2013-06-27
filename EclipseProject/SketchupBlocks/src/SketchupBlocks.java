
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
	    wimpie = new Interpreter(3333, sessMan, this);
	}

	public void draw() 
	{
	
	}
}
