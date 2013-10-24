package sketchupblocks.math.nonlinearmethods;

import static org.junit.Assert.*;

import org.junit.Test;

import sketchupblocks.math.Matrix;
import sketchupblocks.math.NoConvergenceException;

public class NewtonTest 
{

	
	@Test
	public void testAllZero()
	{
		double[] lengths = new double[]{10.0, 14.142135623730951, 10.0, 14.142135623730951, 10.0, 10.0};
		double[] angles = new double[]{0.15770577955941412, 0.17004712781457398, 0.1432623169068218, 0.2121957610531074, 0.11868679562651738, 0.12126664825892033};
		SIS radiiFunction = new SIS(lengths, angles);
	    ErrorFunction radiiErrorFunction = new ErrorFunction(radiiFunction);
	    double[] radiiX0 = new double[]{0, 0, 0, 0};
	    try
	    {
	    	Newton.go(new Matrix(radiiX0), radiiErrorFunction);
	    }
	    catch(Exception e)
	    {
	    	assertTrue("Singular matrix expected", true);
	    }
	}
	
	@Test
	public void testConverge()
	{
		double[] lengths = new double[]{10.0, 14.142135623730951, 10.0, 14.142135623730951, 10.0, 10.0};
		double[] angles = new double[]{0.15770577955941412, 0.17004712781457398, 0.1432623169068218, 0.2121957610531074, 0.11868679562651738, 0.12126664825892033};
		SIS radiiFunction = new SIS(lengths, angles);
	    ErrorFunction radiiErrorFunction = new ErrorFunction(radiiFunction);
	    double[] radiiX0 = new double[]{60, 60, 60, 60};
	    try
	    {
	    	Matrix result = Newton.go(new Matrix(radiiX0), radiiErrorFunction);
	    	assertTrue(result.data[0][0] == 62.52346424295871 && result.data[1][0] == 59.80529606267023 &&
	    			result.data[2][0] == 68.81680884666224 && result.data[3][0] == 66.27215023671909);
	    }
	    catch(Exception e)
	    {
	    	assertTrue("Singular matrix not expected", false);
	    }
	}
	
}
