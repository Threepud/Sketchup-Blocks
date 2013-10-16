package sketchupblocks.recording;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

import processing.core.PApplet;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.Interpreter;
import sketchupblocks.base.Logger;
import sketchupblocks.base.SessionManager;
import sketchupblocks.base.Settings;

public class Feeder extends Interpreter
{
	private LinkedList<CameraEvent> events = new LinkedList<CameraEvent>();
	private LinkedList<Double> waitTimes = new LinkedList<Double>();
	
	
	public Feeder(SessionManager _sessMan, PApplet _parent, int _id) 
	{
		super(_sessMan, _parent, _id);
		parseInput();
	}
	
	public void start()
	{
		Firer fire = new Firer(events, waitTimes, sessMan, cameraID);
		fire.start();
		
		
	}
	
	private void parseInput()
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(new File(Settings.recordingInputFileName + cameraID)));
			
			String line = "";
			
			while((line = reader.readLine()) != null)
			{
				String[] split = line.split("\t");
				if (split.length > 0)
				{
					CameraEvent event = new CameraEvent();
					event.rotation = Float.parseFloat(split[0]);
					event.x = Float.parseFloat(split[1]);
					event.y = Float.parseFloat(split[2]);
					event.type = CameraEvent.EVENT_TYPE.valueOf(split[3]);
					event.fiducialID = Integer.parseInt(split[4]);
					event.cameraID = cameraID;
					events.add(event);
					
					if (Settings.timeDelay)
						waitTimes.add(Double.parseDouble(split[6]));
					else 
						waitTimes.add(0.0);
				}
			}
			
			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	class Firer extends Thread
	{
		LinkedList<CameraEvent> events;
		LinkedList<Double> waitTimes;
		SessionManager sessMan;
		int cameraID;
		
		
		Firer(LinkedList<CameraEvent> _events, LinkedList<Double> _waitTimes, SessionManager _sessMan, int _cameraID)
		{
			events = _events;
			waitTimes = _waitTimes;
			cameraID = _cameraID;
			sessMan = _sessMan;
		}
		
		@Override
		public void run()
		{
			while(!events.isEmpty())
			{
				try
				{
					if(!paused)
					{
						sleep(waitTimes.remove(0).longValue());
						sessMan.onCameraEvent(events.remove(0));
					}
					else sleep(1);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
			Logger.log(cameraID+" is done", 10);
		}
	}
}
