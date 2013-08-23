package sketchupblocks.base;
import sketchupblocks.exception.UnexpectedNonSquareMatrixException;
import sketchupblocks.math.LUDecomposer;
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
	        Matrix setA = new Matrix(pointsA, true);
	        Vec3 centroidB = calculateCentroid(pointsB);
	        Matrix setB = new Matrix(pointsB, true);
	        
	        Matrix H = calculateCovariance(setA, setB, centroidA.toArray(), centroidB.toArray());
	        Matrix [] usv = SVDecomposer.decompose(H); //U, S, V
	        
	        Matrix proposedR = Matrix.multiply(usv[2], usv[0].transpose());
	        
	        if(calculateDeterminant(proposedR) < 0)
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
	        t.data = data;
	        t = new Matrix(data);
	        
	       return new Matrix[]{proposedR, t};
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	private static double calculateDeterminant(Matrix R)
    {
        Matrix[] lup = LUDecomposer.decompose(R);
        
        
        double res= 1;
        for (int k = 0; k < lup[2].rows; k++)
        {
            res *= lup[1].data[k][k];
        }
        
        int numInversions = lup[3].cols;
        return numInversions % 2 != 0 ? -1*res : res;
    }
    
	private static Matrix calculateCovariance(Matrix A, Matrix B, double[] midA, double[] midB) throws Exception
    {
        Matrix centroidA = new Matrix(3, 3);
        centroidA.repeatAsRows(new Vec3(midA));
        Matrix centroidB = new Matrix(3, 3);
        centroidB.repeatAsRows(new Vec3(midB));
        
        if (A.cols != B.cols || A.rows != 3 || A.rows != B.rows)
        {
            System.out.println("Error. Nonmatching datasets");
            throw new Exception();
        }
        
        if (!A.isSquare() )
        {
        	System.out.println(A);
        	throw new UnexpectedNonSquareMatrixException("Cannot calculate covariance of nonsquare matrices");
        }
        else if (!B.isSquare() )
        {
        	System.out.println(B);
        	throw new UnexpectedNonSquareMatrixException("Cannot calculate covariance of nonsquare matrices");
        }
        else if (!centroidA.isSquare() )
        {
        	System.out.println(centroidA);
        	throw new UnexpectedNonSquareMatrixException("Cannot calculate covariance of nonsquare matrices");
        }
        else if (!centroidB.isSquare())
        {
        	System.out.println(centroidB);
        	throw new UnexpectedNonSquareMatrixException("Cannot calculate covariance of nonsquare matrices");
        }
        Matrix H = Matrix.multiply((Matrix.subtract(A, centroidA)).transpose(), Matrix.subtract(B, centroidB));
        
        return H;
    }
    
	private static Vec3 calculateCentroid(Vec3[] points)
    {
		
        double[] center = new double[3];
        System.out.println("-----------------------------");
        for (int k = 0; k < points.length; k++)
        {
        	System.out.println("Received point; "+points[k]);
            center[k] += points[k].x;
            center[k] += points[k].y;
            center[k] += points[k].z;
            center[k] /= 3.0;
        }
        
        Vec3 res = new Vec3(center);
        System.out.println("Calculated centroid: "+res);
        System.out.println("-----------------------------");
        return res;
    }
}
