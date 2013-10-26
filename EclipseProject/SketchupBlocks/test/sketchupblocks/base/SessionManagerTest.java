package sketchupblocks.base;
import org.junit.Test;

import processing.core.PApplet;
import sketchupblocks.base.SessionManager;


public class SessionManagerTest 
{

	@Test
	public void testConstructor()
	{
		PApplet p = new PApplet();
		SessionManager sess = new SessionManager(p, false);
	}
	
}
