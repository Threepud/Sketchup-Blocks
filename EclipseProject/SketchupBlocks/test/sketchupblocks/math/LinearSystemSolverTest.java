package sketchupblocks.math;

import static org.junit.Assert.*;
import org.junit.Test;

public class LinearSystemSolverTest 
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
			Matrix res = LinearSystemSolver.solve(new Matrix(d), b);
			assertTrue(Math.abs(res.data[0][0] -1) < 0.2  && Math.abs(res.data[1][0] - 3) < 0.2);
		}
		catch(Exception e)
		{
		}
	}
	
	@Test
	public void testRectangularSystem()
	{
		double[] b = new double[]{1, 2, 3};
		double[][] d = new double[2][];
		d[0] = new double[]{1, 2};
		d[1] = new double[]{3, 4};
		try
		{
			LinearSystemSolver.solve(new Matrix(d), b);
			assertTrue(false);
		}
		catch(Exception e)
		{
			assertTrue(true);
		}
		
	}
}
