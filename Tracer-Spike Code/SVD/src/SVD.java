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
        double anglez = Math.PI/3.0;
        Vec3[] pointsA = new Vec3[]{new Vec3(2,1,3), new Vec3(2,2,-2), new Vec3(5,0,2)};
        Vec3 centroidA = calculateCentroid(pointsA);
        Matrix setA = new Matrix(pointsA, true);
        System.out.println("A = "+setA);
        
        RotationMatrix3D Rknown = new RotationMatrix3D(anglez);
        System.out.println(Rknown);
        Matrix setB = Matrix.multiply(Rknown, setA.transpose());
        setB = setB.transpose();
        System.out.println("B = "+setB);
        
        Vec3[] pointsB = setB.toVec3Array();
        Vec3 centroidB = calculateCentroid(pointsB);
        System.out.println("CentroidA: "+centroidA);
        System.out.println("CentroidB: "+centroidB);
        
        Matrix H = calculateCovariance(setA, setB, centroidA.toArray(), centroidB.toArray());
        System.out.println("H = "+H);
        
        /*Vec3[] A = new Vec3[]{new Vec3(1, 2, 3), new Vec3(-1, -2, -3), new Vec3(3, 2, 1)};
        Vec3[] B = new Vec3[]{new Vec3(4, 5, 6), new Vec3(6, 4, 5), new Vec3(5, 4, 6)};
        double[] centerA = new double[]{1, 1, 1};
        double[] centerB = new double[]{2, 1, 3};
        calculateCovariance(A, B, centerA, centerB);*/
       
        
    }
    
    public static Matrix calculateCovariance(Matrix A, Matrix B, double[] midA, double[] midB)
    {
        Matrix H = new Matrix(3, 3);
        //double[][] d = new double[3][3];
        
        Matrix centroidA = new Matrix(3, 3);
        centroidA.repeatAsRows(new Vec3(midA));
        Matrix centroidB = new Matrix(3, 3);
        centroidB.repeatAsRows(new Vec3(midB));
        
        if (A.cols != B.cols || A.rows != 3 || A.rows != B.rows)
        {
            System.out.println("Error. Nonmatching datasets");
            return null;
        }
        
        /*for (int k = 0; k < A.cols; k++)
        {
            for (int i = 0; i < A.rows; i++)
            {
                d[k][i] = (A.data[i][k] - midA[i])*(B.data[i][k] - midB[i]);
            }
        }*/
        System.out.println(Matrix.subtract(A, centroidA));
        System.out.println(Matrix.subtract(B, centroidB));
        H = Matrix.multiply((Matrix.subtract(A, centroidA)).transpose(), Matrix.subtract(B, centroidB));
        
        //H.data = d;
        System.out.println("H: "+H);
        return H;
    }
    
    /*public static Matrix calculateCovariance(Vec3[] A, Vec3[] B, double[] midA, double[] midB)
    {
        Matrix H = new Matrix(3, 3);
        double[][] d = new double[3][3];
        
        for (int j = 0; j < A.length; j++)
        {
            double[] vecA = A[j].toArray();
            double [] vecB = B[j].toArray();
            for (int k = 0; k < 3; k++)
            {
                for (int i = 0; i < 3; i++)
                {
                    d[k][i] += (vecA[i] - midA[i])*(vecB[k] - midB[k]);
                }
            }
        }
        
        H.data = d;
        return H;
    }*/
    
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
