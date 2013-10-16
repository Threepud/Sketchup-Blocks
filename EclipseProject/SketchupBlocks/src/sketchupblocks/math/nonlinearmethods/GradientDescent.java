/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sketchupblocks.math.nonlinearmethods;

import sketchupblocks.base.Logger;
import sketchupblocks.math.Matrix;

/**
 *
 * @author cravingoxygen
 */

public class GradientDescent 
{
    static double TOL = 0.001;
    static int maxIter = 100;
    
    public static Matrix go(double[] x0, ErrorFunction G)
    {
        int k = 0;
        double g = -1;
        Matrix x = new Matrix(x0);
        try
        {
            while(k < maxIter)
            {
                double g1 = G.calcError(x);
                Matrix z = G.calcDelError(x);
                double z0 = z.norm();
                
                if (z0 == 0)
                {
                	Logger.log("Zero gradient\n"+"Error: "+g1, 1);
                    return x;
                }
                
                z = Matrix.scalar(1.0/z0, z);
                double alpha3 = 1;
                
                double g3 = G.calcError(Matrix.subtract(x, Matrix.scalar(alpha3, z)));
                
                while(g3 >= g1)
                {
                    alpha3 = alpha3/2.0;
                    g3 = G.calcError(Matrix.subtract(x, Matrix.scalar(alpha3, z)));
                    if (alpha3 < TOL/2.0)
                    {
                    	/*Logger.log("No likely improvement\n"+"Error: "+g1, 1);
                        return x;*/
                    	break;
                    }
                }
                    
                double alpha2 = alpha3/2.0;
                double g2 = G.calcError(Matrix.subtract(x, Matrix.scalar(alpha2, z)));

                double h1 = (g2 - g1)/alpha2;
                double h2 = (g3 - g2)/(alpha3 - alpha2);
                double h3 = (h2 - h1)/alpha3;

                double alpha0 = 0.5*(alpha2 - h1/h3);
                double g0 = G.calcError(Matrix.subtract(x, Matrix.scalar(alpha0, z)));

                double alpha;
                if (g0 > g3)
                {
                    g = g3;
                    alpha = alpha3;
                }
                else
                {
                    g = g0;
                    alpha = alpha0;
                }

                x = Matrix.subtract(x, Matrix.scalar(alpha, z));

                /*if (Math.abs(g - g1) < TOL)
                {	
                	/*Logger.log("Success! TOL: "+TOL+" value: "+Math.abs(g - g1\n"+"Error: "+g, 1);
                        return x;
                }*/
                k++;
            }
            Logger.log("Max iterations exceeded\n"+"Error: "+g, 1);
            return x;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
