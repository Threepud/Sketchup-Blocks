package sketchupblocks.recording;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import TUIO.TuioCursor;
import TUIO.TuioObject;
import TUIO.TuioProcessing;
import TUIO.TuioTime;
import processing.core.PApplet;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.Settings;

public class Recorder
{
	private PApplet parent;
	@SuppressWarnings("unused")
	private TuioProcessing tuioClient;
	private int cameraID;
	
	private static String outputFile = Settings.recordingInputFileName;
	private boolean recording = false;
	private String outputString;
	private Date prev;
	
	
	public Recorder(int port, PApplet _parent, int _id)
	{
		parent = _parent;
		tuioClient  = new TuioProcessing(this, port);
		cameraID = _id;
		prev = new Date();
	}
	
	public void startRecording()
	{
		outputString = "";
		recording = true;
		prev = new Date();
	}
	
	public void stopRecording()
	{
		recording = false;
		try
		{
			File folder = new File((outputFile));
			if (!folder.exists())
				folder.mkdirs();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile+"/output"+cameraID)));
			
			//It would be nice to have some pre-processing here.
			writer.write(outputString);
			
			writer.close();
			outputString = "";
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private String parseTuioObject(TuioObject tobj, CameraEvent.EVENT_TYPE ev)
	{
		String res = tobj.getAngle()+"\t";
		res += tobj.getX()+"\t";
		res += tobj.getY()+"\t";
		res += ev.name()+"\t";
		res += tobj.getSymbolID()+"\t";
		res += cameraID+"\t";
		return res;
	}
	
	public void addTuioObject(TuioObject tobj) 
	{
		if (recording)
		{
			outputString += parseTuioObject(tobj, CameraEvent.EVENT_TYPE.ADD);
			outputString += (new Date()).getTime() - prev.getTime()+"\n";
			prev = new Date();
		}
	}

	// called when an object is removed from the scene
	public void removeTuioObject(TuioObject tobj) 
	{
		if (recording)
		{
			outputString += parseTuioObject(tobj, CameraEvent.EVENT_TYPE.REMOVE);
			outputString += (new Date()).getTime() - prev.getTime()+"\n";
			prev = new Date();
		}
	}
	
	// called when an object is moved
	public void updateTuioObject (TuioObject tobj) 
	{
		if (recording)
		{
			outputString += parseTuioObject(tobj, CameraEvent.EVENT_TYPE.UPDATE);
			outputString += (new Date()).getTime() - prev.getTime()+"\n";
			prev = new Date();
		}
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
