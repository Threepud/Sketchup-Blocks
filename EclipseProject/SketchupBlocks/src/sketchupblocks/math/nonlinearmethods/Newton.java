/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sketchupblocks.math.nonlinearmethods;

import sketchupblocks.base.Logger;
import sketchupblocks.math.LinearSystemSolver;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.SingularMatrixException;

/**
 *
 * @author user
 */
public class Newton 
{
    static public double TOL = 0.00005;
    static public int maxIter = 150;
    static public double[] defaultX0 = new double[]{60 , 60 , 60 , 60};
    
    public static Matrix go(ErrorFunction G) throws SingularMatrixException
    {
        return go(new Matrix(defaultX0), G);
    }
    
    public static Matrix go(Matrix x0, ErrorFunction G) throws SingularMatrixException
    {
        int k = 0;
        Matrix x = x0;
        while(k < maxIter)
        {
            Matrix LHS = G.calcJacobian(x);
            Matrix RHS = Matrix.scalar(-1, G.calcFunction(x));
            if(LHS.isSingular())
            	throw new SingularMatrixException();
            if (RHS.isSingular())
            	throw new SingularMatrixException();
            Matrix y = LinearSystemSolver.solve(LHS, RHS.toArray());
            if (y.isSingular())
            	throw new SingularMatrixException();
            
        	x = Matrix.add(x, y);
        	if (x.isSingular())
            	throw new SingularMatrixException();
            
            if (y.norm() < TOL)
            {
            	Logger.log("Success! "+k+"\nError: "+G.calcError(x), 20);
                return x;
            }
            k++;
        }
            
       // Logger.log("Max iterations exceeded in Newton Method", 1);
        Logger.log("--Math error"+G.calcError(x)+"--", 6);
        //throw new NoConvergenceException();
        return x;
        
    }
}
