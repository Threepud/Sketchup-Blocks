package sketchupblocks.construction;

import static org.junit.Assert.*;

import org.junit.Test;

import sketchupblocks.math.Line;
import sketchupblocks.math.Vec3;

public class EnvironmentAnalyzerTest 
{
	@Test
	public void rayInterectsTestTrue()
	{
		Line line = new Line(new Vec3(0, 0, 0), new Vec3(0, 0, 1));
		Vec3[] corners = new Vec3[]{new Vec3(-1, -1, 1), new Vec3(1, 1, 1), new Vec3(0, -2, 1)};
		assertTrue(EnvironmentAnalyzer.rayIntersectsTriangle(line, corners[0], corners[1], corners[2]));
	}
	
	@Test
	public void rayInterectsTestFalse()
	{
		Line line = new Line(new Vec3(0, 0, 0), new Vec3(0, 0, 1));
		Vec3[] corners = new Vec3[]{new Vec3(-1, -1, 0), new Vec3(1, 1, 0), new Vec3(0, -2, 0)};
		assertTrue(!EnvironmentAnalyzer.rayIntersectsTriangle(line, corners[0], corners[1], corners[2]));
	}
	
	@Test
	public void checkBroad1()
	{
		BoundingBox lower = new BoundingBox();
		lower.min = new Vec3(-5, -2, -10);
		lower.max = new Vec3(-3, -1, -2);
		
		BoundingBox higher = new BoundingBox();
		higher.max = new Vec3(5, 2, 10);
		higher.min = new Vec3(3, 1, 2);
		assertTrue(!EnvironmentAnalyzer.checkBroad(lower, higher));
	}
	
	@Test
	public void checkBroad2()
	{
		BoundingBox lower = new BoundingBox();
		lower.min = new Vec3(-5, -2, -10);
		lower.max = new Vec3(3, 1, 5);
		
		BoundingBox higher = new BoundingBox();
		higher.max = new Vec3(5, 2, 10);
		higher.min = new Vec3(3, 1, 2);
		assertTrue(EnvironmentAnalyzer.checkBroad(lower, higher));
	}

}
