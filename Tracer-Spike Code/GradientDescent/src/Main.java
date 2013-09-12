
import java.util.Date;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.

/**
 *
 * @author cravingoxygen
 */
public class Main 
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        try
        {
            //Date before = new Date();
            double[] d = new double[]{5.8, 5.8, 5.8, 5.8, 8.202438661763951, 8.202438661763951};
            double[] mu = new double[]{4.650836993950945, 4.729261363550332, 4.5443320923419614, 4.61621145450314, 8.193630236405271, 4.331947419318293};

            Vec3[] lPos = new Vec3[]{new Vec3(-2.9, -2.9, 0),new Vec3(-2.9, 2.9, 0),new Vec3(2.9, 2.9, 0), new Vec3(2.9, -2.9, 0)};

            for (int k = 0; k < mu.length; k++)
            {
                mu[k] = Math.toRadians(mu[k]);
            }

            SIS f = new SIS(d, mu);
            ErrorFunction g = new ErrorFunction(f);
            double[] x0 = new double[]{57, 53, 57, 60};

            Matrix x = Newton.go(new Matrix(x0), g);
            System.out.println("XN: "+x);

            CP cp = new CP(lPos, x.toArray());
            ErrorFunction cpError = new ErrorFunction(cp);
            x0 = new double[]{10, 10, 30};
            
            Matrix pos = Newton.go(new Matrix(x0), cpError);
            
            System.out.println("Final pos: "+pos);
            System.out.println("Error on final pos: "+cpError.calcG(pos));
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //Date then = new Date();
        //System.out.println("Time: "+(then.getTime() - before.getTime()));
    }
    
}
