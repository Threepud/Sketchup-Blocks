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
	
	public Settings()
	{
		readSettings("Settings.xml");
	}
	
	public Settings(String fileName)
	{
		readSettings(fileName);
	}
	
	private void readSettings(String fileName)
	{
		XML settings = null;
		try 
		{
			 settings = new XML(new File(fileName));
			
		}
		catch (IOException | ParserConfigurationException | SAXException e) 
		{
			e.printStackTrace();
			return;
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
		
		for(int x = 0; x < numCameras; ++x)
		{
			XML camera = cameras.getChild(x);
			double fov = Double.parseDouble(camera.getChild("FOV").getContent());
			double width = Double.parseDouble(camera.getChild("Width").getContent());
			double height = Double.parseDouble(camera.getChild("Height").getContent());
			int port = Integer.parseInt(camera.getChild("Port").getContent());
			cameraSettings[x] = new CameraSettings(fov, width/height, port);
		}
		
		//Calibration
		
		
		//GUI
		
	}
	
	public static class CameraSettings
	{
		public double fov;
		public double aspectRatio;
		int port;
		
		CameraSettings(double _fov, double _aspectRatio, int _port)
		{
			fov = _fov;
			aspectRatio = _aspectRatio;
			port = _port;
		}
	}
}
