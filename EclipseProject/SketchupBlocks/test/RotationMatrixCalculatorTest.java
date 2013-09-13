import static org.junit.Assert.*;

import org.junit.*;

import sketchupblocks.base.TransformationCalculator;
import sketchupblocks.math.Vec3;
import sketchupblocks.math.Matrix;

public class RotationMatrixCalculatorTest {

	@Test
	public void tranformTest()
	{
		Vec3 [] toRotate =   new Vec3[]{new Vec3(1,2,3),						new Vec3(1,-2,3),						new Vec3(-2,-2,3)};
		Vec3 [] RotatedTo = new Vec3[]{new Vec3(1.701142,1.183503,3.115355),new Vec3(2.94361,-2.035448,1.091838),	new Vec3(0.529397,-3.553086,2.023689)};
		Matrix [] res=  TransformationCalculator.calculateTransformationMatrices(toRotate,RotatedTo);
		
		
		double error = 0;
		try
		{
		for(int k = 0 ; k < 3 ; k++)
		{
			error += RotatedTo[k].distance( Matrix.multiply( Matrix.multiply(res[1], res[0].padMatrix()) , toRotate[k].padVec3() ).toVec3() );
		}
		}
		catch(Exception e)
		{
			fail();
		}
		
		assertTrue(error < 0.00001);
	}
	
}
