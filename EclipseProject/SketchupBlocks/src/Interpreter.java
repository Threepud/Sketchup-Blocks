import processing.core.PApplet;
import processing.net.*;
import TUIO.*;
import java.util.*;

class Interpreter
{
	private SessionManager sessionMan;
	private PApplet parent;
	private TuioProcessing tuioClient;
	  
	public Interpreter(int port, SessionManager _sessMan, PApplet _parent)
	{
		sessionMan = _sessMan;
		parent = _parent;
		tuioClient  = new TuioProcessing(this, port);
	}
  
	void addTuioObject(TuioObject tobj) 
	{
		CameraEvent came = new CameraEvent();
		came.rotation = tobj.getAngle();
		came.x = tobj.getX();
		came.y = tobj.getY();
		came.type = CameraEvent.EVENT_TYPE.ADD;
		came.fiducialID = tobj.getSymbolID();
		sessionMan.onCameraEvent(came);
	}

	// called when an object is removed from the scene
	void removeTuioObject(TuioObject tobj) 
	{
		CameraEvent came = new CameraEvent();
		came.rotation = tobj.getAngle();
		came.x = tobj.getX();
		came.y = tobj.getY();
		came.type = CameraEvent.EVENT_TYPE.REMOVE;
		came.fiducialID = tobj.getSymbolID();
		sessionMan.onCameraEvent(came);
	}
	
	// called when an object is moved
	void updateTuioObject (TuioObject tobj) 
	{
		CameraEvent came = new CameraEvent();
		came.rotation = tobj.getAngle();
		came.x = tobj.getX();
		came.y = tobj.getY();
		came.type = CameraEvent.EVENT_TYPE.UPDATE;
		came.fiducialID = tobj.getSymbolID();
		sessionMan.onCameraEvent(came);
	}
	
	// called when a cursor is added to the scene
	void addTuioCursor(TuioCursor tcur) 
	{
		//println("add cursor "+tcur.getCursorID()+" ("+tcur.getSessionID()+ ") " +tcur.getX()+" "+tcur.getY());
	}
	
	// called when a cursor is moved
	void updateTuioCursor (TuioCursor tcur) 
	{
		//println("update cursor "+tcur.getCursorID()+" ("+tcur.getSessionID()+ ") " +tcur.getX()+" "+tcur.getY()
	         // +" "+tcur.getMotionSpeed()+" "+tcur.getMotionAccel());
	}
	
	// called when a cursor is removed from the scene
	void removeTuioCursor(TuioCursor tcur) 
	{
		//println("remove cursor "+tcur.getCursorID()+" ("+tcur.getSessionID()+")");
	}

	// called after each message bundle
	// representing the end of an image frame
	void refresh(TuioTime bundleTime) 
	{ 
		parent.redraw();
	}
}