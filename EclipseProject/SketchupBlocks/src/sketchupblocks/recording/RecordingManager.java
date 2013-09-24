package sketchupblocks.recording;

import java.util.Date;
import java.util.Scanner;

import processing.core.PApplet;
import sketchupblocks.base.Logger;
import sketchupblocks.base.Settings;

public class RecordingManager extends PApplet
{	
	/**
	 * 
	 */
	static Settings settings;
	private static final long serialVersionUID = 1L;
	static Recorder[] recorders;
	static double timetoRecord = 30000;
	static Date timeStarted;
	static boolean timedRecording = false;
	
	public void setup()
	{
		settings = new Settings("Settings.xml");
		recorders = new Recorder[Settings.numCameras];
		Logger.log("Num recorders created; "+recorders.length+" "+Settings.numCameras, 10);
		for (int k = 0; k < recorders.length; k++)
		{
			recorders[k] = new Recorder(Settings.cameraSettings[k].port, this, k);
		}
		
		for (int k = 0; k < recorders.length; k++)
		{
			recorders[k].startRecording();
		}
		Logger.log("Started recording", 1);
		timeStarted = new Date();
	}
	
	public static void main(String args[]) 
	{
		String[] arguments = {"--present"};
		PApplet.main("sketchupblocks.recording.RecordingManager", arguments);
		
		if (!timedRecording)
		{
			Scanner in = new Scanner(System.in);
			in.nextLine();
			
			for (int k = 0; k < recorders.length; k++)
			{
				recorders[k].stopRecording();
			}
			Logger.log("Stopped recording", 1);
			in.close();
		}
		else
		{
			while((new Date()).getTime() - timeStarted.getTime() < timetoRecord)
			{
				
			}
			for (int k = 0; k < recorders.length; k++)
			{
				recorders[k].stopRecording();
			}
			Logger.log("Stopped recording", 1);
			
		}
	}
	
}
