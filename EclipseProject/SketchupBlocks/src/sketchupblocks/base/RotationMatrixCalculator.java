package sketchupblocks.base;
import sketchupblocks.math.LUDecomposer;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.SVDecomposer;
import sketchupblocks.math.Vec3;


public class RotationMatrixCalculator 
{
	public static Matrix calculateRotationMatrix(Vec3[] pointsA, Vec3[] pointsB)
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
        return proposedR;
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
    
	private static Matrix calculateCovariance(Matrix A, Matrix B, double[] midA, double[] midB)
    {
        Matrix centroidA = new Matrix(3, 3);
        centroidA.repeatAsRows(new Vec3(midA));
        Matrix centroidB = new Matrix(3, 3);
        centroidB.repeatAsRows(new Vec3(midB));
        
        if (A.cols != B.cols || A.rows != 3 || A.rows != B.rows)
        {
            System.out.println("Error. Nonmatching datasets");
            return null;
        }
        Matrix H = Matrix.multiply((Matrix.subtract(A, centroidA)).transpose(), Matrix.subtract(B, centroidB));
        
        return H;
    }
    
	private static Vec3 calculateCentroid(Vec3[] points)
    {
        double[] center = new double[3];
        for (int k = 0; k < points.length; k++)
        {
            center[k] += points[k].x;
            center[k] += points[k].y;
            center[k] += points[k].z;
            center[k] /= 3.0;
        }
        
        
        return new Vec3(center);
    }
}
