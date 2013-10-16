import static org.junit.Assert.*;
import org.junit.Test;

import sketchupblocks.construction.BoundingBox;
import sketchupblocks.construction.ModelBlock;
import sketchupblocks.construction.ModelBlock.ChangeType;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;


public class BoundingBoxTest {

	
	@Test
	public void getBoundingBoxTest()
	{
		SmartBlock _smartBlock= new SmartBlock();
		_smartBlock.vertices = new Vec3[3];
		_smartBlock.vertices[0] = new Vec3(-10,10,10);
		_smartBlock.vertices[1] = new Vec3(0,-30,10);
		_smartBlock.vertices[2] = new Vec3(20,10,100);
		_smartBlock.indices = new int[3];
		_smartBlock.indices[0] = 0;
		_smartBlock.indices[1] = 1;
		_smartBlock.indices[2] = 2;
		_smartBlock.name = "A test block";
		
		Matrix _transformMatrix= Matrix.identity(4);
		ChangeType _type = ModelBlock.ChangeType.UPDATE;
		
		ModelBlock mb = new ModelBlock(_smartBlock,_transformMatrix,_type);
		
		
		BoundingBox bb = BoundingBox.generateBoundingBox(mb);
		assertTrue("Min x",bb.min.x == -10);
		assertTrue("Min y",bb.min.y == -30);
		assertTrue("Min z",bb.min.z == 10);
		
		assertTrue("Max x",bb.max.x == 20);
		assertTrue("Max y",bb.max.y == 10);
		assertTrue("Max z",bb.max.z == 100);
	}
}
