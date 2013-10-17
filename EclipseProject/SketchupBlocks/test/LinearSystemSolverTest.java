import org.junit.Test;

import sketchupblocks.math.LinearSystemSolver;
import sketchupblocks.math.Matrix;


public class LinearSystemSolverTest 
{
	@Test
	public void testManyPossibleSolutions()
	{
		double[][] d = new double[3][];
		d[0] = new double[]{1, 0, 0};
		d[1] = new double[]{0, 2, 4};
		d[2] = new double[]{0, 1, 2};
		Matrix A = new Matrix(d);
		double[] b = new double[]{5, 6, 3};
		
		try
		{
			Matrix res = LinearSystemSolver.solve(A, b);
			System.out.println(res);
		}
		catch(Exception e)
		{
		}
		
	}
}
