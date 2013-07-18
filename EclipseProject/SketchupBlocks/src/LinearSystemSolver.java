
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
    public static Vec3 solve(Vec3[] input, double[] angles) 
    {
        
        Matrix A = new Matrix(input, false);
        
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
        
       return new Vec3(res);
        
    }
}
