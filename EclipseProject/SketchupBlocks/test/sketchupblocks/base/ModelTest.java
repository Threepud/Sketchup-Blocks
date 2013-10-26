package sketchupblocks.base;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sketchupblocks.construction.ModelBlock;
import sketchupblocks.database.Block;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;

public class ModelTest 
{
	private static ModelBlock testBlock;
	private static Model model;
	
	@BeforeClass
	public static void setup()
	{
		//create mock model block
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
		
		SmartBlock sBlock = new SmartBlock();
		sBlock.associatedFiducials = associatedFiducials;
		sBlock.blockId = blockID;
		sBlock.blockType = blockType;
		sBlock.fiducialCoordinates = fiducialCoordinates;
		sBlock.fiducialOrient = fiducialOrientation;
		sBlock.indices = indices;
		sBlock.name = "./models/ColladaTEST.dae";
		sBlock.vertices = vertices;
		
		testBlock = new ModelBlock();
		testBlock.smartBlock = sBlock;
		testBlock.transformationMatrix = Matrix.identity(4);
		testBlock.type = ModelBlock.ChangeType.UPDATE;
		
		model = new Model();
	}
	
	@AfterClass
	public static void cleanUp()
	{
		model = null;
		testBlock = null;
	}
	
	@Test
	public void testModel() 
	{
		model = new Model();
		ArrayList<ModelBlock> result = new ArrayList<>(model.getBlocks());
		
		assertTrue("Model ID not 0", model.getId().equals("1"));
		assertTrue("Model not empty", result.isEmpty());
	}

	@Test
	public void testModelModel() 
	{
		model.addModelBlock(testBlock);
		Model result = new Model(model);
		int oldID = Integer.parseInt(model.getId());
		int newID = Integer.parseInt(result.getId());
		ArrayList<ModelBlock> oldBlocks = new ArrayList<>(model.getBlocks());
		ArrayList<ModelBlock> newBlocks = new ArrayList<>(result.getBlocks());
		
		assertTrue("New model ID not the same as old model", oldID == newID);
		assertTrue("New model doesn't reflect old model", oldBlocks.size() == newBlocks.size() && oldBlocks.size() == 1);
		for(int x = 0; x < oldBlocks.size(); ++x)
			testBlockEqual(oldBlocks.get(x), newBlocks.get(x));
	}

	@Test
	public void testAddModelBlock() 
	{
		model.addModelBlock(testBlock);
		ArrayList<ModelBlock> blocks = new ArrayList<>(model.getBlocks());
		
		assertTrue("Model size not 1", blocks.size() == 1);
		testBlockEqual(testBlock, blocks.get(0));
	}

	@Test
	public void testRemoveModelBlock() 
	{
		model.addModelBlock(testBlock);
		ArrayList<ModelBlock> blocks = new ArrayList<>(model.getBlocks());
		assertTrue("Model size not 1", blocks.size() == 1);
		
		model.removeModelBlock(testBlock);
		blocks = new ArrayList<>(model.getBlocks());
		assertTrue("Model not empty", blocks.size() == 0);
	}

	@Test
	public void testGetId() 
	{
		assertTrue("Could not get model ID", model.getId().equals("0"));
	}

	@Test
	public void testGetBlocks() 
	{
		model.addModelBlock(testBlock);
		ArrayList<ModelBlock> blocks = new ArrayList<>(model.getBlocks());
		
		assertTrue("Model size not 1", blocks.size() == 1);
		testBlockEqual(testBlock, blocks.get(0));
	}

	@Test
	public void testGetBlockById() 
	{
		model.addModelBlock(testBlock);
		ModelBlock result = model.getBlockById(9);
		testBlockEqual(testBlock, result);
		
		result = model.getBlockById(0);
		assertTrue("Model get model by id fail", result == null);
	}
	
	private void testBlockEqual(ModelBlock oneBlock, ModelBlock twoBlock)
	{
		//test vertices
		for(int x = 0; x < oneBlock.smartBlock.vertices.length; ++x)
		{
			assertTrue
			(
				"Vertices don't match", 
				oneBlock.smartBlock.vertices[x].x == twoBlock.smartBlock.vertices[x].x &&
				oneBlock.smartBlock.vertices[x].y == twoBlock.smartBlock.vertices[x].y &&
				oneBlock.smartBlock.vertices[x].z == twoBlock.smartBlock.vertices[x].z
			);
		}
		
		//test indices
		for(int x = 0; x < testBlock.smartBlock.indices.length; ++x)
		{
			assertTrue("Indices don't match", oneBlock.smartBlock.indices[x] == twoBlock.smartBlock.indices[x]);
		}
		
		assertTrue("Transformation matrices don't match", oneBlock.transformationMatrix.equals(twoBlock.transformationMatrix));
	}
}
