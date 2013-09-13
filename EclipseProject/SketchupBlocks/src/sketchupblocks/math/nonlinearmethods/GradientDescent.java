/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sketchupblocks.math.nonlinearmethods;

import sketchupblocks.base.Settings;
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
                double g1 = G.calcG(x);
                Matrix z = G.calcDelG(x);
                double z0 = z.norm();
                
                if (z0 == 0)
                {
                	if (Settings.verbose < 0)
	            	{
	                    System.out.println("Zero gradient");
	                    System.out.println("Error: "+g1);
	            	}
                    return x;
                }
                
                z = Matrix.scalar(1.0/z0, z);
                double alpha3 = 1;
                
                double g3 = G.calcG(Matrix.subtract(x, Matrix.scalar(alpha3, z)));
                
                while(g3 >= g1)
                {
                    alpha3 = alpha3/2.0;
                    g3 = G.calcG(Matrix.subtract(x, Matrix.scalar(alpha3, z)));
                    //System.out.println("Yay");
                    if (alpha3 < TOL/2.0)
                    {
                    	/*if (Settings.verbose< 0)
                    	{
	                        System.out.println("No likely improvement.");
	                        System.out.println("Error: "+g1);
                    	}
                        return x;*/
                    	break;
                    }
                }
                    
                double alpha2 = alpha3/2.0;
                double g2 = G.calcG(Matrix.subtract(x, Matrix.scalar(alpha2, z)));

                double h1 = (g2 - g1)/alpha2;
                double h2 = (g3 - g2)/(alpha3 - alpha2);
                double h3 = (h2 - h1)/alpha3;

                double alpha0 = 0.5*(alpha2 - h1/h3);
                double g0 = G.calcG(Matrix.subtract(x, Matrix.scalar(alpha0, z)));

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
                	if (Settings.verbose < 0)
	            	{
	                    System.out.println("Success! TOL: "+TOL+" value: "+Math.abs(g - g1));
	                    System.out.println("Error: "+g);
	            	}
                    return x;
                }*/
                k++;
            }

        	if (Settings.verbose < 0)
        	{
        		System.out.println("Max iterations exceeded");
                System.out.println("Error: "+g);
        	}
            return x;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
