package sketchupblocks.construction;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import sketchupblocks.math.Face;
import sketchupblocks.math.Vec3;

public class BoundingBoxTest 
{
	@Test
	public void testGenSepAxes()
	{
		BoundingBox bb = new BoundingBox();
		bb.worldVertices = new Vec3[]{new Vec3(1, 1, -1), new Vec3(1, 0, 1), new Vec3(-1, 1, 1)};
		ArrayList<Vec3> normals = new ArrayList<Vec3>();
		normals = bb.generate2DSeparationAxes(normals);
		Face f = new Face(bb.worldVertices);
		Vec3 fnormal = Vec3.normalize(f.normal());
		Vec3 calcNorm = normals.get(0);
		assertTrue(Math.abs(calcNorm.x - fnormal.x) < 0.5  && Math.abs(calcNorm.y - fnormal.y) < 0.5 && Math.abs(calcNorm.z - fnormal.z) < 0.5);
	}
	
	@Test
	public void test2DWorldVerts()
	{
		BoundingBox bb = new BoundingBox();
		bb.worldVertices = new Vec3[]{new Vec3(1, 1, -1), new Vec3(1, 0, 1), new Vec3(-1, 1, 1)};
		Vec3[] proj = bb.generate2DWorldVertices();
		for (int k = 0; k < proj.length; k++)
		{
			assertTrue(proj[k].x == bb.worldVertices[k].x && proj[k].y == bb.worldVertices[k].y && proj[k].z == 0);
		}
	}
	
}
