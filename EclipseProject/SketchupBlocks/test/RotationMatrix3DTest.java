import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import sketchupblocks.math.Matrix;
import sketchupblocks.math.RotationMatrix3D;
import sketchupblocks.math.Vec3;


public class RotationMatrix3DTest 
{
	@Before
	public void setup()
	{
	}
	@Test
	public void testConstructor1()
	{
		RotationMatrix3D rotX = new RotationMatrix3D(new Vec3(1, 0, 0), Math.PI/2.0);
		RotationMatrix3D rotY = new RotationMatrix3D(new Vec3(0, 1, 0), Math.PI/2.0);
		RotationMatrix3D rotZ = new RotationMatrix3D(new Vec3(0, 0, 1), Math.PI/2.0);
		
		Vec3 toRot = new Vec3(-1, 2, 3);
		Vec3 toRot2 = new Vec3(1, 1, 1);
		Vec3 toRot3 = new Vec3(1, 0, -1);
		
		Vec3 toRotX = Matrix.multiply(rotX, toRot);
		Vec3 toRotY = Matrix.multiply(rotY, toRot);
		Vec3 toRotZ = Matrix.multiply(rotZ, toRot);
		
		Vec3 toRot2X = Matrix.multiply(rotX, toRot2);
		Vec3 toRot2Y = Matrix.multiply(rotY, toRot2);
		Vec3 toRot2Z = Matrix.multiply(rotZ, toRot2);
		
		Vec3 toRot3X = Matrix.multiply(rotX, toRot3);
		Vec3 toRot3Y = Matrix.multiply(rotY, toRot3);
		Vec3 toRot3Z = Matrix.multiply(rotZ, toRot3);
		
		RotationMatrix3D rotXCheck = new RotationMatrix3D(Math.PI/2.0, Matrix.Axis.X_AXIS);
		RotationMatrix3D rotYCheck = new RotationMatrix3D(Math.PI/2.0, Matrix.Axis.Y_AXIS);
		RotationMatrix3D rotZCheck = new RotationMatrix3D(Math.PI/2.0, Matrix.Axis.Z_AXIS);
		
		System.out.println(Matrix.determinant(rotXCheck));
		System.out.println(Matrix.determinant(rotYCheck));
		System.out.println(Matrix.determinant(rotZCheck));

		System.out.println(Matrix.determinant(rotX));
		System.out.println(Matrix.determinant(rotY));
		System.out.println(Matrix.determinant(rotZ));
		
		assertTrue("Rotation about x-axis (toRot1)", equalMargin(Matrix.multiply(rotXCheck, toRot), toRotX));
		assertTrue("Rotation about y-axis (toRot1)", equalMargin(Matrix.multiply(rotYCheck, toRot), toRotY));
		assertTrue("Rotation about z-axis (toRot1)", equalMargin(Matrix.multiply(rotZCheck, toRot), toRotZ));
		
		assertTrue("Rotation about x-axis (toRot2)", equalMargin(Matrix.multiply(rotXCheck, toRot2), toRot2X));
		assertTrue("Rotation about y-axis (toRot2)", equalMargin(Matrix.multiply(rotYCheck, toRot2), toRot2Y));
		assertTrue("Rotation about z-axis (toRot2)", equalMargin(Matrix.multiply(rotZCheck, toRot2), toRot2Z));
		
		assertTrue("Rotation about x-axis (toRot3)", equalMargin(Matrix.multiply(rotXCheck, toRot3), toRot3X));
		assertTrue("Rotation about y-axis (toRot3)", equalMargin(Matrix.multiply(rotYCheck, toRot3), toRot3Y));
		assertTrue("Rotation about z-axis (toRot3)", equalMargin(Matrix.multiply(rotZCheck, toRot3), toRot3Z));
	}
	
	private boolean equalMargin(Matrix one, Matrix two)
	{
		Matrix m = Matrix.subtract(one,  two);
		
		for (int k = 0; k < m.rows; k++)
			for (int i = 0; i < m.cols; i++)
				if (Math.abs(m.data[k][i]) > 0.01)
					return false;
		
		return true;
	}
	
	private boolean equalMargin(Vec3 one, Vec3 two)
	{
		double[] d = Vec3.subtract(one,  two).toArray();
		
		for (int k = 0; k < d.length; k++)
				if (Math.abs(d[k]) > 0.01)
				{
					System.out.println(Math.abs(d[k]));
					return false;
				}
		
		return true;
	}

}
