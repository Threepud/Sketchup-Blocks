
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runners.*;
import org.junit.runner.RunWith;

import processing.core.PApplet;

import TUIO.TuioObject;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.Interpreter;
import sketchupblocks.base.SessionManager;

/*
Run with:
java -cp .;junit.jar;hamcrest-core-1.3.jar org.junit.runner.JUnitCore Matrix

*/

@RunWith(JUnit4.class)
public class InterpreterTest
{

	TuioObject tob = new TuioObject((long)1.0, 91, 50.0f, 80.0f, 0.5f);
	CameraEvent result;
	SessionManagerMock sess = new SessionManagerMock();
	
	
	@Test
	public void testConstructor()
	{
		Interpreter i = new Interpreter(5, sess, null, 10);
		assertTrue("CameraID correctly set", 10 == i.getCameraID());
	}
	
	@Test
	public void testAdd()
	{
		Interpreter i = new Interpreter(6, sess, null, 10);
		i.addTuioObject(tob);
		assertTrue("Camera Event of type ADD correctly generated", checkMatch(CameraEvent.EVENT_TYPE.ADD));
	}
	
	@Test
	public void testRemove()
	{
		Interpreter i = new Interpreter(7, sess, null, 10);
		i.removeTuioObject(tob);
		assertTrue("Camera Event of type REMOVE correctly generated", checkMatch(CameraEvent.EVENT_TYPE.REMOVE));
	}
	
	@Test
	public void testUpdate()
	{
		Interpreter i = new Interpreter(8, sess, null, 10);
		i.updateTuioObject(tob);
		assertTrue("Camera Event of type UPDATE correctly generated", checkMatch(CameraEvent.EVENT_TYPE.UPDATE));
	}
	
	private boolean checkMatch(CameraEvent.EVENT_TYPE t)
	{
		if (result.type != t)
			return false;
		if (result.cameraID != 10)
			return false;
		if (result.fiducialID != tob.getSymbolID())
			return false;
		if (result.rotAcceleration != tob.getRotationAccel())
			return false;
		if (result.rotation != tob.getAngle())
			return false;
		if (result.rotVelocity != tob.getRotationSpeed())
			return false;
		if (result.x != tob.getX())
			return false;
		if (result.y != tob.getY())
			return false;
		if (result.xVelocity != tob.getXSpeed())
			return false;
		return true;
	}
	
	private class SessionManagerMock extends SessionManager
	{

		
		public SessionManagerMock() 
		{
		}
		
		@Override
		public void onCameraEvent(CameraEvent cam)
		{
			result = cam;
		}
		
	}
} 
