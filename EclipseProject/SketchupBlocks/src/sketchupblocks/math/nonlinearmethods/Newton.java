/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sketchupblocks.math.nonlinearmethods;

import sketchupblocks.base.Logger;
import sketchupblocks.math.LinearSystemSolver;
import sketchupblocks.math.Matrix;

/**
 *
 * @author user
 */
public class Newton 
{
    static public double TOL = 0.0000000000005;
    static public int maxIter = 150;
    static public double[] defaultX0 = new double[]{60 , 60 , 60 , 60};
    
    public static Matrix go(ErrorFunction G)
    {
        return go(new Matrix(defaultX0), G);
    }
    
    public static Matrix go(Matrix x0, ErrorFunction G)
    {
        int k = 0;
        Matrix x = x0;
        try
        {
            while(k < maxIter)
            {
                Matrix LHS = G.calcJacobian(x);
                Matrix RHS = Matrix.scalar(-1, G.calcFunction(x));
                if(LHS.isSingular())
                	return null;
                if (RHS.isSingular())
                	return null;
                Matrix y = LinearSystemSolver.solve(LHS, RHS.toArray());
                if (y.isSingular())
                	return null;
                try
                {
                	x = Matrix.add(x, y);
                	if (x.isSingular())
                		return null;
                }
                catch(Exception e)
                {
                	//e.printStackTrace();
                	//Logger.log("x: "+x+"\ny: "+y, 1);
                	System.exit(-1);
                	return null;
                }
                
                if (y.norm() < TOL)
                {
                	Logger.log("Success! "+k+"\nError: "+G.calcError(x), 20);
                    return x;
                }
                k++;
            }
            
            Logger.log("Max iterations exceeded in Newton Method", 1);
            //System.out.println(x);
            Logger.log("Math error"+G.calcError(x), 30);
            return null;
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
