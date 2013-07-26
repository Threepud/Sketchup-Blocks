
import java.util.Date;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author cravingoxygen
 */
public class LinearSystemSolver 
{

    /**
     * @param args the command line arguments
     */
    public static void solve(Vec3[] input, double[] angles) 
    {
        //Date date = new Date();
       // Vec3[] input = new Vec3[4];
        input[0] = new Vec3(-32.03277887835761, -0.8759331741728624, -34.06169071347373);
        input[1] = new Vec3(-26.03277887835761, -0.8759331741728624, -34.06169071347373);
        input[2] = new Vec3(-26.03277887835761, 5.124066825827137, -34.06169071347373);
        input[3] = new Vec3(-32.03277887835761, 5.124066825827137, -34.06169071347373);
        
        Matrix A = new Matrix(input, false);
        
      //  double[] angles = {Math.toRadians(21.798792791484427), Math.toRadians(27.617864086498315), Math.toRadians(28.13690263152082), Math.toRadians(22.147824762956063)};
        //Vec3 est = new Vec3(0.8577986101074306, -0.5111635657760251, 0.05378990166285618);
        
        
        double [] bVecData = new double[4];
        for (int k = 0; k < 4; k++)
        {
            bVecData[k] = input[k].length()*Math.cos(angles[0]);
        }
        Vec4 BVec = new Vec4(bVecData);
        
       
        
        Matrix At = A.transpose();
        
        Matrix BMat = new Matrix(BVec);
        Matrix AtB = Matrix.multiply(At, BMat);
        Matrix AtA = Matrix.multiply(At, A);
        
        LUDecomposer.decompose(AtA.data);
        double[] res = LUDecomposer.solve(AtB.toVec3().toArray());
        
       // System.out.println(date.getTime() - (new Date()).getTime());
        
        /*Matrix result = new Matrix(new Vec3(res));
        
       
        
        Matrix supB = Matrix.multiply(A, result);
        System.out.println("****************************");
        System.out.println(supB.toString());
        System.out.println("****************************");
        System.out.println(BMat.toString());
        System.out.println("****************************");
        System.out.println(Matrix.multiply(A, (new Matrix(est))));
        
        for (int k = 0; k < est.toArray().length; k++)
        {
            System.out.println("Received: "+res[k]+"\tExpected: "+est.toArray()[k]);
        }*/
        
    }
}
