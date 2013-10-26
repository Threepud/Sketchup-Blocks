package sketchupblocks.math;

/**
 * @author cravingoxygen
 *
 *This class was written to solve the linear system produced during camera calibration.
 *The solution represents 
 */
public class LineDirectionSolver
{
	public static Vec3 solve(Vec3[] input, double[] angles) throws SingularMatrixException
    {
    	if (angles.length != 4)
    		throw new RuntimeException("Invalid pararmeters to line direction solver");
    	
        Matrix A = new Matrix(input, false);
        
        double [] bVecData = new double[4];
        for (int k = 0; k < 4; k++)
        {
            bVecData[k] = input[k].length()*Math.cos(angles[k]);
        }
    	return LinearSystemSolver.solve(A, bVecData).toVec3();
    }
}
