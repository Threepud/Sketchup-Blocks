package sketchupblocks.math;

import static org.junit.Assert.*;

import org.junit.Test;


public class LUDecomposerTest 
{
	@Test
	public void testNormalSystem()
	{
		double[] b = new double[]{7, 3};
		double[][] d = new double[2][];
		d[0] = new double[]{1, 2};
		d[1] = new double[]{5, -1};
		try
		{
			Matrix[] res = LUDecomposer.decompose(new Matrix(d));
			Matrix LUP = Matrix.multiply(res[0], res[1]);
			assertTrue(LUP.data[0][0] == 5  && LUP.data[1][0] == 1 && LUP.data[0][1] == -1 && LUP.data[1][1] == 2);
		}
		catch(Exception e)
		{
		}
	}
	
	@Test
	public void testRectangularSystem()
	{
		double[] b = new double[]{1, 2, 3};
		double[][] d = new double[3][];
		d[0] = new double[]{1, 2};
		d[1] = new double[]{3, 4};
		d[2] = new double[]{3, 4};
		try
		{
			LUDecomposer.decompose(new Matrix(d));
			assertTrue(false);
		}
		catch(Exception e)
		{
			assertTrue(true);
		}
		
	}
}
