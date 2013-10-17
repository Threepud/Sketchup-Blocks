package sketchupblocks.math;

/**
 *
 * @author cravingoxygen
 * 
 * The LinearSystemSolver class solves a given linear system.
 * The linear system provided must be square and must have a unique solution.
 * This class makes use of the LU decomposer to solve the system.
 */
public class LinearSystemSolver 
{
	
    /**
     * This solves a given linear system of the form
     * Ax = b
     * A must be a square matrix with linearly independent rows.
     * If there is no unique solution to the system, the results will be meaningless.
     * If A is not square, a runtime exception will be thrown.
     * @param input - The coefficient matrix of the linear system
     * @param b - The right hand side of the equation for the linear system
     * @return - The solution of the linear system (i.e. x) as a Matrix
     * @throws SingularMatrixException
     */
    public static Matrix solve(Matrix input, double[] b) throws SingularMatrixException
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
