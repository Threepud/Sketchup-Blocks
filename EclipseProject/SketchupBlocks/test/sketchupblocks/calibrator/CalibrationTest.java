package sketchupblocks.calibrator;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.*;
import org.junit.runners.*;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;

import processing.data.XML;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.InputBlock;
import sketchupblocks.base.RuntimeData;
import sketchupblocks.base.Settings;
import sketchupblocks.calibrator.Calibrator;
import sketchupblocks.database.Block;
import sketchupblocks.database.CommandBlock;
import sketchupblocks.math.Vec3;



/*
Run with:
java -cp .;junit.jar;hamcrest-core-1.3.jar org.junit.runner.JUnitCore CalibrationTest

*/

@RunWith(JUnit4.class)
public class CalibrationTest
{
	private static Calibrator cally;
	private static String prevNumCams;
	
	@BeforeClass
	public static void setupBeforeClass()
	{
		//Edit settings to one camera
		
		
		XML settings = null;
		try 
		{
			 settings = new XML(new File("Settings.xml"));
			
		}
		catch (IOException | ParserConfigurationException | SAXException e) 
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		XML cameras = settings.getChild("Cameras");
		XML numCameras = cameras.getChild("NumberOfCameras");
		prevNumCams = numCameras.getContent();
		numCameras.setContent("1");
		
		settings.save(new File("Settings.xml"), "");
		Settings.readSettings("Settings.xml");
		RuntimeData.init();
		//Create callibrator with new Settings.xml
		cally = new Calibrator();
	}
	
	@AfterClass
	public static void afterClass()
	{
		//Undo edit
		XML settings = null;
		try 
		{
			 settings = new XML(new File("Settings.xml"));
			
		}
		catch (IOException | ParserConfigurationException | SAXException e) 
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		XML cameras = settings.getChild("Cameras");
		XML numCameras = cameras.getChild("NumberOfCameras");
		numCameras.setContent(prevNumCams);
		
		settings.save(new File("Settings.xml"), "");
	}
	
	@Test
	public void testInitialNotCalibrated()
	{
		assertTrue(!RuntimeData.isSystemCalibrated());
	}
	
	
	@Test
	public void testCameraPostition()
	{
	//So. Find 4 sets of inputs. Measure the actual position. Get The difference. Is greater than well.....
	//60
		CameraEvent event = new CameraEvent();
		event.x = 0.46247488f;
		event.y = 0.6389536f;
		event.rotation = 0.9450448f;
		event.cameraID = 0;
		event.fiducialID = 60;
		event.type = CameraEvent.EVENT_TYPE.ADD;
		
		CommandBlock block = new CommandBlock();
		block.associatedFiducials = new int[]{60};
		block.type = CommandBlock.CommandType.CALIBRATE;
		block.blockType = Block.BlockType.COMMAND;
		block.blockId = 60;
		
		InputBlock input = new InputBlock(block,event);
		try
		{
			cally.processBlock(input);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
	 //61
		event = new CameraEvent();
		event.x = 0.5618164f;
		event.y =  0.7237733f;
		event.rotation = 0.896361f;
		event.cameraID = 0;
		event.fiducialID = 61;
		event.type = CameraEvent.EVENT_TYPE.ADD;
		
		block = new CommandBlock();
		block.associatedFiducials = new int[]{61};
		block.type = CommandBlock.CommandType.CALIBRATE;
		block.blockType = Block.BlockType.COMMAND;
		block.blockId = 61;
		
		input = new InputBlock(block,event);
		try
		{
			cally.processBlock(input);
		}
		catch(Exception e)
		{
			
		}
	//62
		event = new CameraEvent();
		event.x =  0.464791f;
		event.y =  0.83094186f;
		event.rotation = 0.8858087f;
		event.cameraID = 0;
		event.fiducialID = 62;
		event.type = CameraEvent.EVENT_TYPE.ADD;
		
		block = new CommandBlock();
		block.associatedFiducials = new int[]{62};
		block.type = CommandBlock.CommandType.CALIBRATE;
		block.blockType = Block.BlockType.COMMAND;
		block.blockId = 62;
		
		input = new InputBlock(block,event);
		try
		{
			cally.processBlock(input);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	//63
		event = new CameraEvent();
		event.x = 0.3637672f;
		event.y =  0.7328904f;
		event.rotation = 0.9657693f;
		event.cameraID = 0;
		event.fiducialID = 63;
		event.type = CameraEvent.EVENT_TYPE.ADD;
		
		block = new CommandBlock();
		block.associatedFiducials = new int[]{63};
		block.type = CommandBlock.CommandType.CALIBRATE;
		block.blockType = Block.BlockType.COMMAND;
		block.blockId = 63;
		
		input = new InputBlock(block,event);
		try
		{
			long before = System.currentTimeMillis();
			cally.processBlock(input);
			long after = System.currentTimeMillis();
			assertTrue(after - before < 20);
			System.out.println("Time for calibration: "+(after - before));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		assertTrue("Is not calibrated", RuntimeData.isSystemCalibrated());
	    Vec3 camPosition = RuntimeData.getCameraPosition(0);
	    assertTrue("X component differs by "+Math.abs(camPosition.x - 0.5) ,Math.abs(camPosition.x - 0.5) < 5);
	    assertTrue("Y component differs by "+Math.abs(camPosition.y - 52)   ,Math.abs(camPosition.y - 52) < 5);
	    assertTrue("Z component differs by "+Math.abs(camPosition.z - -4)   ,Math.abs(camPosition.z - -4) < 5);
	}

}