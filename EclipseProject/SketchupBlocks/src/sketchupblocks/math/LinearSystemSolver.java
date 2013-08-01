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
    public static Vec3 solve(Vec3[] input, double[] angles) 
    {
    	if (angles.length != 3)
    	{
    		System.out.println("Invalid solve attemped");
    		return null;
    	}
    	
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
        
        DecompositionResult dcres = LUDecomposer.decompose(AtA.data);
        try
        {
        	double[] res = LUDecomposer.solve(AtB.toVec3().toArray(), dcres);
            return new Vec3(res);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        
        return null;
    }
}
