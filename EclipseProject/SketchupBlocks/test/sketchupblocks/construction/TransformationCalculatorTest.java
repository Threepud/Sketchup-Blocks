package sketchupblocks.construction;

import static org.junit.Assert.*;

import org.junit.Test;

import sketchupblocks.math.Matrix;
import sketchupblocks.math.RotationMatrix3D;
import sketchupblocks.math.Vec3;

public class TransformationCalculatorTest 
{
	@Test
	public void testRotation()
	{
		Vec3[] pointsA = new Vec3[]{new Vec3(0, 0, 1), new Vec3(0, 1, 0), new Vec3(1, 0, 0)};
		RotationMatrix3D rot = new RotationMatrix3D(Math.PI/3.0, Matrix.Axis.Y_AXIS);
		Vec3[] pointsB = new Vec3[]{Matrix.multiply(rot, pointsA[0]), Matrix.multiply(rot, pointsA[1]), Matrix.multiply(rot, pointsA[2])};
		Matrix[] trans = TransformationCalculator.calculateTransformationMatrices(pointsA, pointsB);
		Matrix transform = Matrix.multiply(trans[1], trans[0].padMatrix());
		System.out.println(Matrix.multiply(transform, pointsA[0].padVec3()));
		System.out.println(pointsB[0]);
		
		assertTrue(Matrix.multiply(transform, pointsA[0].padVec3()).x == pointsB[0].x);
	}
	
	@Test
	public void testTranslation()
	{
		Vec3[] pointsA = new Vec3[]{new Vec3(0, 0, 1), new Vec3(0, 1, 0), new Vec3(1, 0, 0)};
		Vec3[] pointsB = new Vec3[]{new Vec3(0, 0, 2), new Vec3(0, 1, 1), new Vec3(1, 0, 1)};
		Matrix[] trans = TransformationCalculator.calculateTransformationMatrices(pointsA, pointsB);
		Matrix transform = Matrix.multiply(trans[1], trans[0].padMatrix());
		assertTrue(Matrix.multiply(transform, pointsA[2].padVec3()).z == 1 && Matrix.multiply(transform, pointsA[1].padVec3()).z == 1 && Matrix.multiply(transform, pointsA[0].padVec3()).z == 2);
	}

}
