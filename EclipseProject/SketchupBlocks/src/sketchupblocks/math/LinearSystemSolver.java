package sketchupblocks.math;

/**
 *
 * @author cravingoxygen
 */
public class LinearSystemSolver 
{

    /**
     * @param args the command line arguments
     */
    
    public static Matrix solve(Matrix input, double[] b)
    {
    	//r1*c1*r2*c2 = r1*c2
    	if (b.length != input.rows)
    		throw new RuntimeException("No unique solution");
    	
    	Matrix A = input;
    	Matrix B = new Matrix(b);
    	
    	if (!input.isSquare())
    	{
    	
	        Matrix At = A.transpose();
	        
	        Matrix AtB = Matrix.multiply(At, B);
	        Matrix AtA = Matrix.multiply(At, A);
	        B = AtB;
	        A = AtA;
    	}
        
        Matrix[] lup = LUDecomposer.decompose(A);
    	double[] res = LUDecomposer.solve(B.toArray(), lup);
        return new Matrix(res);
    }
}
