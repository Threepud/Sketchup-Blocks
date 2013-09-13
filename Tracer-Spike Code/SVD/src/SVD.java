/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author cravingoxygen
 */
public class SVD {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        try
        {
            double anglez = Math.PI/3.0;
            Vec3[] pointsA = new Vec3[]{new Vec3(2,1,3), new Vec3(2,2,-2), new Vec3(5,0,2)};
            Vec3 centroidA = calculateCentroid(pointsA);
            Matrix setA = new Matrix(pointsA, true);
            System.out.println("A = "+setA);

            RotationMatrix3D Rknown = new RotationMatrix3D(anglez);
            System.out.println("Rknown = "+Rknown);
            Matrix setB = Matrix.multiply(Rknown, setA.transpose());
            setB = setB.transpose();
            System.out.println("B = "+setB);

            Vec3[] pointsB = setB.toVec3Array();
            Vec3 centroidB = calculateCentroid(pointsB);
            System.out.println("CentroidA: "+centroidA);
            System.out.println("CentroidB: "+centroidB);

            Matrix H = calculateCovariance(setA, setB, centroidA.toArray(), centroidB.toArray());
            System.out.println("H = "+H);

            //So now we need only do the actual svd decomposition...
            //R = V*U.'
            //Check for reflection case.

            Matrix [] usv = SVDecomposer.decompose(H); //U, S, V
            System.out.println("U: "+usv[0]);
            System.out.println("S: "+usv[1]);
            System.out.println("V: "+usv[2]);
            Matrix proposedR = Matrix.multiply(usv[2], usv[0].transpose());
            
            System.out.println("Proposed R "+proposedR);
            
            if(calculateDeterminant(proposedR) < 0)
            {
                int COLUMN_THREE = 2;
                for (int k = 0; k < proposedR.rows; k++)
                {
                    usv[2].data[COLUMN_THREE][k] *= -1;
                }
                proposedR = Matrix.multiply(usv[2], usv[0].transpose());
            }
            System.out.println("Proposed R "+proposedR);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    public static double calculateDeterminant(Matrix R)
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
    
    public static Matrix calculateCovariance(Matrix A, Matrix B, double[] midA, double[] midB)
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
    
    public static Vec3 calculateCentroid(Vec3[] points)
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
