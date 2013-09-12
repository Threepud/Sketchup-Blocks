package sketchupblocks.base;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.SVDecomposer;
import sketchupblocks.math.Vec3;


public class RotationMatrixCalculator 
{
	public static Matrix[] calculateTransformationMatrices(Vec3[] pointsA, Vec3[] pointsB)
	{
		try
		{
			Vec3 centroidA = calculateCentroid(pointsA);
	        Matrix setA = new Matrix(pointsA, false);
	        Vec3 centroidB = calculateCentroid(pointsB);
	        Matrix setB = new Matrix(pointsB, false);
	        
	        Matrix H = calculateCovariance(setA, setB, centroidA.toArray(), centroidB.toArray());

	        Matrix [] usv = SVDecomposer.decompose(H); //U, S, V
	        
	        Matrix proposedR = Matrix.multiply(usv[2], usv[0].transpose());
	        
	        if(Matrix.determinant(proposedR) < 0)
	        {
	            int COLUMN_THREE = 2;
	            for (int k = 0; k < proposedR.rows; k++)
	            {
	                usv[2].data[COLUMN_THREE][k] *= -1;
	            }
	            proposedR = Matrix.multiply(usv[2], usv[0].transpose());
	        }
	        
	        Matrix t = Matrix.add(Matrix.multiply(Matrix.scalar(-1, proposedR), new Matrix(centroidA)), new Matrix(centroidB));
	        
	        
	        double[][] data = new double[4][4];
	        for (int k = 0; k < 4; k++)
	        {
	        	data[k][k] = 1;
	        }
	        for (int k = 0; k < 3; k++)
	        {
	        	data[k][3] = t.data[k][0];
	        }
	        t = new Matrix(data);
	        if (Settings.verbose > 2)
	        {
		        System.out.println("Translation: "+t);
		        System.out.println("Rotation: "+proposedR);
	        }
	        
	       return new Matrix[]{proposedR, t};
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
    
	private static Matrix calculateCovariance(Matrix A, Matrix B, double[] midA, double[] midB) throws Exception
    {
        Matrix centroidA = new Matrix(3, 3);
        centroidA.repeatAsRows(new Vec3(midA), A.rows);
        Matrix centroidB = new Matrix(3, 3);
        centroidB.repeatAsRows(new Vec3(midB), B.rows);
        
        Matrix H = Matrix.multiply((Matrix.subtract(A, centroidA)).transpose(), Matrix.subtract(B, centroidB));
        
        return H;
    }
    
	private static Vec3 calculateCentroid(Vec3[] points)
    {
		
        double[] center = new double[3];
        for (int k = 0; k < points.length; k++)
        {
            center[0] += points[k].x;
            center[1] += points[k].y;
            center[2] += points[k].z;
        }
        
        
        Vec3 res = new Vec3(center);
        res = Vec3.scalar(1.0/points.length, res);
        return res;
    }
}
