/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
public class Newton 
{
    static private double TOL = 0.0000000000005;
    static private int maxIter = 150;
    
    public static Matrix go(Matrix x0, ErrorFunction G)
    {
        int k = 0;
        Matrix x = x0;
        try
        {
            while(k < maxIter)
            {
                Matrix LHS = G.calcJ(x);
                Matrix RHS = Matrix.scalar(-1, G.calcF(x));
                Matrix y = LinearSystemSolver.solve(LHS, RHS.toArray());
                
                x = Matrix.add(x, y);
                
                if (y.norm() < TOL)
                {
                    System.out.println("Success! "+k);
                    System.out.println("Error: "+G.calcG(x));
                    return x;
                }
            k++;
            }
            System.out.println("Max iterations exceeded");
            return x;
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
