package sketchupblocks.base;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SettingsTest 
{
	@BeforeClass
	public static void setup()
	{
		String settingsString =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
			"<Settings>" +
			"\t<System>" +
			"\t\t<VersionNumber>0.85</VersionNumber>" +
			"\t\t<DebugVerbose>5</DebugVerbose>" +
			"\t</System>" +
			"\t<Cameras>" +
			"\t\t<NumberOfCameras>4</NumberOfCameras>" +
			"\t\t<CameraSettings id=\"0\">" +
			"\t\t\t<FOV>68.5</FOV>" +
			"\t\t\t<Width>1280.0</Width>" +
			"\t\t\t<Height>720.0</Height>" +
			"\t\t\t<Port>3333</Port>" +
			"\t\t</CameraSettings>" +
			"\t\t<CameraSettings id=\"1\">" +
			"\t\t\t<FOV>68.5</FOV>" +
			"\t\t\t<Width>1280.0</Width>" +
			"\t\t\t<Height>720.0</Height>" +
			"\t\t\t<Port>3334</Port>" +
			"\t\t</CameraSettings>" +
			"\t\t<CameraSettings id=\"2\">" +
			"\t\t\t<FOV>68.5</FOV>" +
			"\t\t\t<Width>1280.0</Width>" +
			"\t\t\t<Height>720.0</Height>" +
			"\t\t\t<Port>3335</Port>" +
			"\t\t</CameraSettings>" +
			"\t\t<CameraSettings id=\"3\">" +
			"\t\t\t<FOV>68.5</FOV>" +
			"\t\t\t<Width>1280.0</Width>" +
			"\t\t\t<Height>720.0</Height>" +
			"\t\t\t<Port>3336</Port>" +
			"\t\t</CameraSettings>" +
			"\t</Cameras>" +
			"\t<Calibration>" +
			"\t\t<Landmark id=\"0\">-5.0,-5.0,0</Landmark>" +
			"\t\t<Landmark id=\"1\">-5.0,5.0,0</Landmark>" +
			"\t\t<Landmark id=\"2\">5.0,-5.0,0</Landmark>" +
			"\t\t<Landmark id=\"3\">5.0,5.0,0</Landmark>" +
			"\t</Calibration>" +
			"\t<GUI>" +
			"\t\t<FancyShaders>true</FancyShaders>" +
			"\t\t<ShowSplash>false</ShowSplash>" +
			"\t\t<SplashTTL>3000</SplashTTL>" +
			"\t\t<CommandWaitTime>3000</CommandWaitTime>" +
			"\t\t<ProgressBarRotationSpeed>500</ProgressBarRotationSpeed>" +
			"\t</GUI>" +
			"\t<DebugRecording>" +
			"\t\t<LiveData>false</LiveData>" +
			"\t\t<RecordingInputFilename>dbo/Jumping2</RecordingInputFilename>" +
			"\t\t<TimeDelay>true</TimeDelay>" +
			"\t</DebugRecording>" +
			"\t<Network>" +
			"\t\t<HostPort>5555</HostPort>" +
			"\t\t<ConnectPort>5555</ConnectPort>" +
			"\t</Network>" +
			"</Settings>";
		File testFile = new File("SettingsTest.xml");
		try 
		{
			PrintWriter pw = new PrintWriter(testFile);
			pw.println(settingsString);
			pw.close();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void cleanUp()
	{
		File testFile = new File("SettingsTest.xml");
		if(testFile.exists())
			testFile.delete();
	}

	@Test
	public void testReadSettings() 
	{
		Settings.readSettings("SettingsTest.xml");
		
		assertTrue(Settings.versionNr.equals("0.85"));
		assertTrue(Settings.verbose == 5);
		assertTrue(Settings.numCameras == 4);
		
		int baseCameraPort = 3333;
		for(int x = 0; x < Settings.cameraSettings.length; ++x)
		{
			assertTrue(Settings.cameraSettings[x].fov == 68.5);
			assertTrue(Settings.cameraSettings[x].aspectRatio == (1280.0 / 720.0));
			assertTrue(Settings.cameraSettings[x].port == baseCameraPort++);
		}
		
		assertTrue
		(
			Settings.landmarks[0].x == -5.0 &&
			Settings.landmarks[0].y == -5.0 &&
			Settings.landmarks[0].z == 0
		);
		assertTrue
		(
			Settings.landmarks[1].x == -5.0 &&
			Settings.landmarks[1].y == 5.0 &&
			Settings.landmarks[1].z == 0
		);
		assertTrue
		(
			Settings.landmarks[2].x == 5.0 &&
			Settings.landmarks[2].y == -5.0 &&
			Settings.landmarks[2].z == 0
		);
		assertTrue
		(
			Settings.landmarks[3].x == 5.0 &&
			Settings.landmarks[3].y == 5.0 &&
			Settings.landmarks[3].z == 0
		);
		
		assertTrue(Settings.fancyShaders);
		assertTrue(!Settings.showSplash);
		assertTrue(Settings.splashTTL == 3000);
		assertTrue(Settings.commandWaitTime == 3000);
		assertTrue(Settings.progressBarRotationSpeed == 500);
		
		assertTrue(!Settings.liveData);
		assertTrue(Settings.recordingInputFileName.equals("dbo/Jumping2"));
		assertTrue(Settings.timeDelay);
		
		assertTrue(Settings.hostPort == 5555);
		assertTrue(Settings.connectPort == 5555);
	}
}
