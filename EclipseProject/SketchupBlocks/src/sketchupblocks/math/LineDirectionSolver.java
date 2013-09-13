package sketchupblocks.math;

public class LineDirectionSolver
{
	public static Vec3 solve(Vec3[] input, double[] angles) 
    {
    	if (angles.length != 4)
    	{
    		throw new RuntimeException();
    	}
    	
        Matrix A = new Matrix(input, false);
        
        double [] bVecData = new double[4];
        for (int k = 0; k < 4; k++)
        {
            bVecData[k] = input[k].length()*Math.cos(angles[k]);
        }
        //Vec4 BVec = new Vec4(bVecData);
        
       
       /* Matrix At = A.transpose();
        
        Matrix BMat = new Matrix(BVec);
        Matrix AtB = Matrix.multiply(At, BMat);
        Matrix AtA = Matrix.multiply(At, A);
        
        Matrix[] lup = LUDecomposer.decompose(AtA);*/
        try
        {
        	//double[] res = LUDecomposer.solve(AtB.toArray(), lup);
            //return new Vec3(res);
        	
        	return LinearSystemSolver.solve(A, bVecData).toVec3();
        	
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        
        return null;
    }
}
