package sketchupblocks.base;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import sketchupblocks.database.Block;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.Vec3;

public class ColladaLoaderTest 
{
	SmartBlock testBlock = new SmartBlock();
	
	@Before
	public void setup()
	{
		int[] associatedFiducials = {54, 55, 56, 57, 58, 59};
		int blockID = 9;
		Block.BlockType blockType = Block.BlockType.SMART;
		Vec3[] fiducialCoordinates =
			{
				new Vec3(0.0, 0.0, 6.5),
				new Vec3(-3.25, 0.0, 0.0),
				new Vec3(0.0, -9.75, 0.0),
				new Vec3(3.25, 0.0, 0.0),
				new Vec3(0.0, 0.0, -6.5),
				new Vec3(0.0, 9.75, 0.0)
			};
		Vec3[] fiducialOrientation =
			{
				new Vec3(0.0, 1.0, 0.0),
				new Vec3(0.0, 0.0, 1.0),
				new Vec3(0.0, 0.0, 1.0),
				new Vec3(0.0, 0.0, 1.0),
				new Vec3(0.0, -1.0, 0.0),
				new Vec3(0.0, 0.0, -1.0)
			};
		int[] indices = {0, 1, 2, 1, 0, 3, 4, 5, 6, 5, 4, 7, 8, 9, 10, 9, 8, 11, 12, 13, 14, 13, 12, 15, 16, 17, 18, 17, 16, 19, 20, 21, 22, 21, 20, 23};
		Vec3[] vertices =
			{
				new Vec3(-3.2499999214462347, -9.749999764338733, 6.499999842892485),
				new Vec3(3.2499999214462347, 9.749999764338733, 6.499999842892485),
				new Vec3(-3.24999992144625, 9.74999976433873, 6.499999842892485),
				new Vec3(3.24999992144625, -9.74999976433873, 6.499999842892485),
				new Vec3(-3.2499999214462347, -9.749999764338733, 6.499999842892485),
				new Vec3(-3.24999992144625, 9.74999976433873, -6.499999842892479),
				new Vec3(-3.2499999214462347, -9.749999764338733, -6.499999842892479),
				new Vec3(-3.24999992144625, 9.74999976433873, 6.499999842892485),
				new Vec3(3.24999992144625, -9.74999976433873, -6.499999842892479),
				new Vec3(-3.2499999214462347, -9.749999764338733, 6.499999842892485),
				new Vec3(-3.2499999214462347, -9.749999764338733, -6.499999842892479),
				new Vec3(3.24999992144625, -9.74999976433873, 6.499999842892485),
				new Vec3(3.2499999214462347, 9.749999764338733, 6.499999842892485),
				new Vec3(3.24999992144625, -9.74999976433873, -6.499999842892479),
				new Vec3(3.2499999214462347, 9.749999764338733, -6.499999842892479),
				new Vec3(3.24999992144625, -9.74999976433873, 6.499999842892485),
				new Vec3(3.2499999214462347, 9.749999764338733, 6.499999842892485),
				new Vec3(-3.24999992144625, 9.74999976433873, -6.499999842892479),
				new Vec3(-3.24999992144625, 9.74999976433873, 6.499999842892485),
				new Vec3(3.2499999214462347, 9.749999764338733, -6.499999842892479),
				new Vec3(3.24999992144625, -9.74999976433873, -6.499999842892479),
				new Vec3(-3.24999992144625, 9.74999976433873, -6.499999842892479),
				new Vec3(3.2499999214462347, 9.749999764338733, -6.499999842892479),
				new Vec3(-3.2499999214462347, -9.749999764338733, -6.499999842892479)
			};
		
		testBlock.associatedFiducials = associatedFiducials;
		testBlock.blockId = blockID;
		testBlock.blockType = blockType;
		testBlock.fiducialCoordinates = fiducialCoordinates;
		testBlock.fiducialOrient = fiducialOrientation;
		testBlock.indices = indices;
		testBlock.name = "./models/ColladaTEST.dae";
		testBlock.vertices = vertices;
	}
	
	@AfterClass
	public static void cleanUp()
	{
		File file = new File("./models/ColladaTEST.dae");
		
		if(file.exists())
			file.delete();
	}

	@Test
	public void testColladaImportExport() 
	{
		ColladaLoader.saveSmartBlock(testBlock);
		SmartBlock result = ColladaLoader.getSmartBlock("./models/ColladaTEST.dae");
		
		//test vertices
		for(int x = 0; x < testBlock.vertices.length; ++x)
		{
			assertTrue
			(
					"Collada Loader get smart block test (Indices)", 
					testBlock.vertices[x].x == result.vertices[x].x &&
					testBlock.vertices[x].y == result.vertices[x].y &&
					testBlock.vertices[x].z == result.vertices[x].z
			);
		}
		
		//test indices
		for(int x = 0; x < testBlock.indices.length; ++x)
		{
			assertTrue("Collada Loader get smart block test (Indices)", testBlock.indices[x] == result.indices[x]);
		}
	}
}
