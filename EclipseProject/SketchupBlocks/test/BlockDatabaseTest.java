import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sketchupblocks.database.Block;
import sketchupblocks.database.BlockDatabase;
import sketchupblocks.database.CommandBlock;
import sketchupblocks.database.SmartBlock;

public class BlockDatabaseTest 
{
	public static BlockDatabase db;
	
	@BeforeClass
	public static void setupBeforeClass()
	{
		try 
		{
			String smartData = 
				"0\t" +
				"0,1,2,3,4,5\t" +
				"0,0,3.25,-3.25,0,0,0,-3.25,0,3.25,0,0,0,0,-3.25,0,3.25,0\t" +
				"0,0,1,-1,0,0,0,-1,0,1,0,0,0,0,-1,0,1,0\t" +
				"./models/PaperCube.dae";
			String commandData =
				"1003\t" +
				"203\t" + 
				"EXPORT";
			String userData = "";
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("testSmart.dat")));
			bw.write(smartData);
			bw.close();
			
			bw = new BufferedWriter(new FileWriter(new File("testCommand.dat")));
			bw.write(commandData);
			bw.close();
			
			bw = new BufferedWriter(new FileWriter(new File("testUser.dat")));
			bw.write(userData);
			bw.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(-1);
		}
		
		try 
		{
			db = new BlockDatabase("testSmart.dat", "testCommand.dat", "testUser.dat");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	@Test
	public void testFindBlock() 
	{
		Block result = db.findBlock(0);
		
		//Test smart block
		assertTrue("Block Database: Not smart block.", result instanceof SmartBlock);
		
		//Test id
		SmartBlock smartBlock = (SmartBlock)result;
		assertTrue("Block Database: Block ID doesn't match.", smartBlock.blockId == 0);
		
		//Test associated fiducials
		assertTrue
		(
			"Block Database: Associated fiducials don't match.",
			0 == smartBlock.associatedFiducials[0] &&
			1 == smartBlock.associatedFiducials[1] &&
			2 == smartBlock.associatedFiducials[2] &&
			3 == smartBlock.associatedFiducials[3] &&
			4 == smartBlock.associatedFiducials[4] &&
			5 == smartBlock.associatedFiducials[5]
		);
		
		//Test fiducial coordinates
		assertTrue
		(
			"Block Database: Fiducial coordinates don't match.",
			0 == smartBlock.fiducialCoordinates[0].x &&
			0 == smartBlock.fiducialCoordinates[0].y &&
			3.25 == smartBlock.fiducialCoordinates[0].z &&
			
			-3.25 == smartBlock.fiducialCoordinates[1].x &&
			0 == smartBlock.fiducialCoordinates[1].y &&
			0 == smartBlock.fiducialCoordinates[1].z &&

			0 == smartBlock.fiducialCoordinates[2].x &&
			-3.25 == smartBlock.fiducialCoordinates[2].y &&
			0 == smartBlock.fiducialCoordinates[2].z &&
			
			3.25 == smartBlock.fiducialCoordinates[3].x &&
			0 == smartBlock.fiducialCoordinates[3].y &&
			0 == smartBlock.fiducialCoordinates[3].z &&
			
			0 == smartBlock.fiducialCoordinates[4].x &&
			0 == smartBlock.fiducialCoordinates[4].y &&
			-3.25 == smartBlock.fiducialCoordinates[4].z &&
			
			0 == smartBlock.fiducialCoordinates[5].x &&
			3.25 == smartBlock.fiducialCoordinates[5].y &&
			0 == smartBlock.fiducialCoordinates[5].z
		);
		
		//Test fiducial orientations
		assertTrue
		(
			"Block Database: Fiducial orientations don't match.",
			0 == smartBlock.fiducialOrient[0].x &&
			0 == smartBlock.fiducialOrient[0].y &&
			1 == smartBlock.fiducialOrient[0].z &&
			
			-1 == smartBlock.fiducialOrient[1].x &&
			0 == smartBlock.fiducialOrient[1].y &&
			0 == smartBlock.fiducialOrient[1].z &&

			0 == smartBlock.fiducialOrient[2].x &&
			-1 == smartBlock.fiducialOrient[2].y &&
			0 == smartBlock.fiducialOrient[2].z &&
			
			1 == smartBlock.fiducialOrient[3].x &&
			0 == smartBlock.fiducialOrient[3].y &&
			0 == smartBlock.fiducialOrient[3].z &&
			
			0 == smartBlock.fiducialOrient[4].x &&
			0 == smartBlock.fiducialOrient[4].y &&
			-1 == smartBlock.fiducialOrient[4].z &&
			
			0 == smartBlock.fiducialOrient[5].x &&
			1 == smartBlock.fiducialOrient[5].y &&
			0 == smartBlock.fiducialOrient[5].z
		);
		
		//Test model file location
		assertTrue("Block Database: model file doesn't match.", smartBlock.name.equals("./models/PaperCube.dae"));
		
		//test fiducial mapping to fiducials
		int[] tempFids = {0, 1, 2, 3, 4, 5};
		for(int x = 0; x < tempFids.length; ++x)
		{
			result = db.findBlock(tempFids[x]);
			
			assertTrue("Block Database: Not smart block.", result instanceof SmartBlock);
			assertTrue("Block Database: Block ID doesn't match.", result.blockId == 0);
		}
		
		//Test command block
		result = db.findBlock(203);
		
		//Test command block type
		assertTrue("Block Database: Not command block.", result instanceof CommandBlock);
		
		CommandBlock commandBlock = (CommandBlock)result;
		//Test block ID
		assertTrue("Block Database: Block ID doesn't match.", 1003 == commandBlock.blockId);
		
		//Test associated fiducials
		assertTrue
		(
			"Block Database: Associated fiducials don't match.",
			203 == commandBlock.associatedFiducials[0]
		);
		
		//Test command block type
		assertTrue
		(
			"Block Database: Command type doesn't match", 
			CommandBlock.CommandType.EXPORT == commandBlock.type
		);
	}
	
	@AfterClass
	public static void afterClass()
	{
		File file = new File("testSmart.dat");
		file.delete();
		
		file = new File("testCommand.dat");
		file.delete();
		
		file = new File("testUser.dat");
		file.delete();
	}
}
