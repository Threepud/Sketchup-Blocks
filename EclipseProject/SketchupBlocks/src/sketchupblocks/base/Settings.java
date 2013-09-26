package sketchupblocks.base;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import processing.data.XML;
import sketchupblocks.math.Vec3;

public class Settings 
{
	//System
	public static String versionNr = "1.0";
	public static int verbose = 0; //0 is default. From there, higher is more verbose.

	//Cameras
	public static int numCameras = 0;
	public static CameraSettings [] cameraSettings;
	
	//Calibration
	public static Vec3[] landmarks;
	
	//GUI
	public static boolean showSplash = false;
	public static int splashTTL = 3000;
	public static int commandWaitTime = 3000;
	public static int progressBarRotationSpeed = 500;
	
	//Debug recording
	public static boolean liveData = true;
	public static String recordingInputFileName = "debugOutput(JustSomeData)/output";
	public static boolean timeDelay = true;
	
	//Network
	public static short hostPort = 5555;
	public static short connectPort = 5555;
	
	public Settings()
	{
		readSettings("Settings.xml");
	}
	
	public Settings(String fileName)
	{
		readSettings(fileName);
	}
	
	public static boolean readSettings(String fileName)
	{
		boolean result = true;
		
		XML settings = null;
		try 
		{
			 settings = new XML(new File(fileName));
			
		}
		catch (IOException | ParserConfigurationException | SAXException e) 
		{
			Logger.log("ERROR: Set settings.", 1);
			e.printStackTrace();
			result = false;
		}
		
		//System
		XML system = settings.getChild("System");
		XML versionNumber = system.getChild("VersionNumber");
		XML debugVerbose = system.getChild("DebugVerbose");
		versionNr = versionNumber.getContent();
		verbose = Integer.parseInt(debugVerbose.getContent());
		
		//Cameras
		XML cameras = settings.getChild("Cameras");
		XML numberOfCameras = cameras.getChild("NumberOfCameras");
		numCameras = Integer.parseInt(numberOfCameras.getContent());
		cameraSettings = new CameraSettings[numCameras];
		
		int cameraCount = 0;
		for(int x = 0; x < cameras.getChildCount(); ++x)
		{
			XML camera = cameras.getChild(x);
			if(camera.getName().equals("CameraSettings") && cameraCount < numCameras)
			{
				try
				{
					double fov = Double.parseDouble(camera.getChild("FOV").getContent());
					double width = Double.parseDouble(camera.getChild("Width").getContent());
					double height = Double.parseDouble(camera.getChild("Height").getContent());
					int port = Integer.parseInt(camera.getChild("Port").getContent());
					cameraSettings[cameraCount++] = new CameraSettings(fov, width/height, port);
				}
				catch(NumberFormatException e)
				{
					Logger.log("ERROR: Set settings.", 1);
					e.printStackTrace();
					result = false;
				}
			}
		}
		
		if(numCameras > cameraCount)
		{
			Logger.log("ERROR: Set settings.", 1);
			System.err.println("Settings: Too many suggested number of cameras.");
			result = false;
		}
		
		//Calibration
		XML calibration = settings.getChild("Calibration");
		int numLandmarks = 0;
		//count landmarks
		for(int x = 0; x < calibration.getChildCount(); ++x)
		{
			if(calibration.getChild(x).getName().equals("Landmark"))
				numLandmarks++;
		}
		
		
		landmarks = new Vec3[numLandmarks];
		int landmarkCount = 0;
		for(int x = 0; x < calibration.getChildCount(); ++x)
		{
			if(calibration.getChild(x).getName().equals("Landmark"))
			{
				String line = calibration.getChild(x).getContent();
				String[] coordsString = line.replaceAll("[" + " " + "\t" + "]", "").split(",");
				
				if(coordsString.length != 3)
				{
					System.err.println("Landmark coordinates incomplete.");
					result = false;
				}
				
				try
				{
					double[] coords = 
					{
						Double.parseDouble(coordsString[0]),
						Double.parseDouble(coordsString[1]),
						Double.parseDouble(coordsString[2])
					};
					landmarks[landmarkCount++] = new Vec3(coords[0], coords[1], coords[2]);
				}
				catch(NumberFormatException e)
				{
					Logger.log("ERROR: Set settings.", 1);
					e.printStackTrace();
					result = false;
				}
			}
		}
		
		//GUI
		XML gui = settings.getChild("GUI");
		
		XML showSplashNode = gui.getChild("ShowSplash");
		XML splashTTLNode = gui.getChild("SplashTTL");
		XML commandWaitTimeNode = gui.getChild("CommandWaitTime");
		XML progressBarRotationSpeedNode = gui.getChild("ProgressBarRotationSpeed");
		
		try
		{
			showSplash = Boolean.parseBoolean(showSplashNode.getContent());
			splashTTL = Integer.parseInt(splashTTLNode.getContent());
			commandWaitTime = Integer.parseInt(commandWaitTimeNode.getContent());
			progressBarRotationSpeed = Integer.parseInt(progressBarRotationSpeedNode.getContent());
		}
		catch(Exception e)
		{
			Logger.log("ERROR: Set settings.", 1);
			e.printStackTrace();
			result = false;
		}
		
		//Debug recording
		XML debugRecordingNode = settings.getChild("DebugRecording");
		XML liveDataNode = debugRecordingNode.getChild("LiveData");
		XML recordingInputFilenameNode = debugRecordingNode.getChild("RecordingInputFilename");
		XML timeDelayNode = debugRecordingNode.getChild("TimeDelay");
		
		liveData = Boolean.parseBoolean(liveDataNode.getContent());
		recordingInputFileName = recordingInputFilenameNode.getContent();
		timeDelay = Boolean.parseBoolean(timeDelayNode.getContent());
		
		//Network
		XML network = settings.getChild("Network");
		XML hostPortNode = network.getChild("HostPort");
		XML connectPortNode = network.getChild("ConnectPort");
		
		try
		{
			hostPort = Short.parseShort(hostPortNode.getContent());
			connectPort = Short.parseShort(connectPortNode.getContent());
		}
		catch(NumberFormatException e)
		{
			Logger.log("ERROR: Set settings.", 1);
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	public static class CameraSettings
	{
		public double fov;
		public double aspectRatio;
		public int port;
		CameraSettings(double _fov, double _aspectRatio, int _port)
		{
			fov = _fov;
			aspectRatio = _aspectRatio;
			port = _port;
		}
	}
}
