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
	public static String versionNr;
	public static int verbose; //0 is default. From there, higher is more verbose.

	//Cameras
	public static int numCameras;
	public static CameraSettings [] cameraSettings;
	
	//Calibration
	public static Vec3[] landmarks;
	
	//GUI
	public static boolean showSplash;
	public static int splashTTL;
	public static int commandWaitTime;
	public static int progressBarRotationSpeed;
	
	//Debug recording
	public static boolean liveData;
	public static String recordingInputFileName;
	public static boolean timeDelay;
	
	public Settings()
	{
		readSettings("Settings.xml");
	}
	
	public Settings(String fileName)
	{
		readSettings(fileName);
	}
	
	public static void readSettings(String fileName)
	{
		XML settings = null;
		try 
		{
			 settings = new XML(new File(fileName));
			
		}
		catch (IOException | ParserConfigurationException | SAXException e) 
		{
			e.printStackTrace();
			System.exit(-1);
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
				double fov = Double.parseDouble(camera.getChild("FOV").getContent());
				double width = Double.parseDouble(camera.getChild("Width").getContent());
				double height = Double.parseDouble(camera.getChild("Height").getContent());
				int port = Integer.parseInt(camera.getChild("Port").getContent());
				cameraSettings[cameraCount++] = new CameraSettings(fov, width/height, port);
			}
		}
		
		if(numCameras > cameraCount)
		{
			System.err.println("Settings: Too many suggested number of cameras.");
			System.exit(-1);
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
					System.exit(-1);
				}
				
				double[] coords = 
				{
					Double.parseDouble(coordsString[0]),
					Double.parseDouble(coordsString[1]),
					Double.parseDouble(coordsString[2])
				};
				landmarks[landmarkCount++] = new Vec3(coords[0], coords[1], coords[2]);
			}
		}
		
		//GUI
		XML gui = settings.getChild("GUI");
		
		XML showSplashNode = gui.getChild("ShowSplash");
		XML splashTTLNode = gui.getChild("SplashTTL");
		XML commandWaitTimeNode = gui.getChild("CommandWaitTime");
		XML progressBarRotationSpeedNode = gui.getChild("ProgressBarRotationSpeed");
		
		showSplash = Boolean.parseBoolean(showSplashNode.getContent());
		splashTTL = Integer.parseInt(splashTTLNode.getContent());
		commandWaitTime = Integer.parseInt(commandWaitTimeNode.getContent());
		progressBarRotationSpeed = Integer.parseInt(progressBarRotationSpeedNode.getContent());
		
		//Debug recording
		XML debugRecordingNode = settings.getChild("DebugRecording");
		XML liveDataNode = debugRecordingNode.getChild("LiveData");
		XML recordingInputFilenameNode = debugRecordingNode.getChild("RecordingInputFilename");
		XML timeDelayNode = debugRecordingNode.getChild("TimeDelay");
		
		liveData = Boolean.parseBoolean(liveDataNode.getContent());
		recordingInputFileName = recordingInputFilenameNode.getContent();
		timeDelay = Boolean.parseBoolean(timeDelayNode.getContent());
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
