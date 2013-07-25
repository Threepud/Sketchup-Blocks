
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runners.*;
import org.junit.runner.RunWith;



/*
Run with:
java -cp .;junit.jar;hamcrest-core-1.3.jar org.junit.runner.JUnitCore CalibrationTest

*/

@RunWith(JUnit4.class)
public class CalibrationTest
{
	Calibrator cally;
	
	@Before
	public void setup()
	{
	cally = new Calibrator();	
	}
	
	@Test
	public void testInitialNotCalibrated()
	{
		assertTrue(!cally.isCalibrated());
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
		cally.processBlock(input);
	
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
		cally.processBlock(input);
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
		cally.processBlock(input);
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
		cally.processBlock(input);
		assertTrue("Is not calibrated",cally.isCalibrated());
	    Vec3 camPosition = cally.cameraPositions[0];
	    assertTrue("X component differs by "+Math.abs(camPosition.x - 22.5) ,Math.abs(camPosition.x - 22.5) < 5);
	    assertTrue("Y component differs by "+Math.abs(camPosition.y - 25)   ,Math.abs(camPosition.y - 25) < 5);
	    assertTrue("Z component differs by "+Math.abs(camPosition.z - 32)   ,Math.abs(camPosition.z - 32) < 5);
	}

}