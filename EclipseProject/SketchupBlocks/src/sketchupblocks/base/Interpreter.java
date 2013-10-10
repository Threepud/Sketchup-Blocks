package sketchupblocks.base;

import processing.core.PApplet;
import TUIO.*;

public class Interpreter
{
	private PApplet parent;
	private TuioProcessing tuioClient;
	protected int cameraID;
	protected SessionManager sessMan;
	protected boolean paused;
	  
	public Interpreter(int port, SessionManager _sessMan, PApplet _parent, int _id)
	{
		sessMan = _sessMan;
		parent = _parent;
		tuioClient  = new TuioProcessing(this, port);
		cameraID = _id;
		paused = false;
	}
	
	public Interpreter(SessionManager _sessMan, PApplet _parent, int _id)
	{
		sessMan = _sessMan;
		parent = _parent;
		cameraID = _id;
		paused = false;
	}
  
	public int getCameraID()
	{
		return cameraID;
	}

	
	public void addTuioObject(TuioObject tobj) 
	{
		if (paused)
			return;
		CameraEvent came = new CameraEvent();
		came.rotation = tobj.getAngle();
		came.rotAcceleration = tobj.getRotationAccel();
		came.rotVelocity = tobj.getRotationSpeed();
		came.xVelocity = tobj.getXSpeed();
		came.x = tobj.getX();
		came.y = tobj.getY();
		came.type = CameraEvent.EVENT_TYPE.ADD;
		came.fiducialID = tobj.getSymbolID();
		came.cameraID = cameraID;
		sessMan.onCameraEvent(came);
	}

	// called when an object is removed from the scene
	public void removeTuioObject(TuioObject tobj) 
	{
		if (paused)
			return;
		CameraEvent came = new CameraEvent();
		came.rotation = tobj.getAngle();
		came.rotAcceleration = tobj.getRotationAccel();
		came.rotVelocity = tobj.getRotationSpeed();
		came.xVelocity = tobj.getXSpeed();
		came.x = tobj.getX();
		came.y = tobj.getY();
		came.type = CameraEvent.EVENT_TYPE.REMOVE;
		came.fiducialID = tobj.getSymbolID();
		came.cameraID = cameraID;
		sessMan.onCameraEvent(came);
	}
	
	// called when an object is moved
	public void updateTuioObject (TuioObject tobj) 
	{
		if (paused)
			return;
		CameraEvent came = new CameraEvent();
		came.rotation = tobj.getAngle();
		came.rotAcceleration = tobj.getRotationAccel();
		came.rotVelocity = tobj.getRotationSpeed();
		came.xVelocity = tobj.getXSpeed();
		came.x = tobj.getX();
		came.y = tobj.getY();
		came.type = CameraEvent.EVENT_TYPE.UPDATE;
		came.fiducialID = tobj.getSymbolID();
		came.cameraID = cameraID;
		sessMan.onCameraEvent(came);
	}
	
	// called when a cursor is added to the scene
	public void addTuioCursor(TuioCursor tcur) 
	{
		//println("add cursor "+tcur.getCursorID()+" ("+tcur.getSessionID()+ ") " +tcur.getX()+" "+tcur.getY());
	}
	
	// called when a cursor is moved
	public void updateTuioCursor (TuioCursor tcur) 
	{
		//println("update cursor "+tcur.getCursorID()+" ("+tcur.getSessionID()+ ") " +tcur.getX()+" "+tcur.getY()
	         // +" "+tcur.getMotionSpeed()+" "+tcur.getMotionAccel());
	}
	
	// called when a cursor is removed from the scene
	public void removeTuioCursor(TuioCursor tcur) 
	{
		//println("remove cursor "+tcur.getCursorID()+" ("+tcur.getSessionID()+")");
	}

	// called after each message bundle
	// representing the end of an image frame
	public void refresh(TuioTime bundleTime) 
	{
		parent.redraw();
	}
}