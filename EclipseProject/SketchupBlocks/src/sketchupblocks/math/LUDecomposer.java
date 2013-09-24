package sketchupblocks.math;


public class LUDecomposer 
 {
	
    public static Matrix[] decompose(Matrix a)
    {
    	if (a == null)
    		throw new NullPointerException();
    	if (!a.isSquare())
    	{
    		throw new RuntimeException("Cannot decompose nonsquare matrix.");
    	}
        int n = a.cols;
        double[][] lu = a.data;
        int[] indx = new int[n];
        final double TINY = 1.0e-40;
        int i;
        int imax;
        int j;
        int k;
        double big;
        double temp;
        double[] vv = new double[n];

        for (i = 0; i < n; i++)
        {
            big = 0.0;
            for (j = 0; j < n; j++)
                if ((temp = Math.abs(lu[i][j])) > big)
                    big = temp;
            if (big == 0.0)
            {
                System.out.println("Singular matrix");
                System.out.println(a);
                try
                {
                	throw new Exception("Singular matrix");
                }
                catch(Exception e)
                {
                	e.printStackTrace();
                }
            }
            vv[i]=1.0/big;
        }
        for (k = 0; k < n; k++)
        {
            big = 0.0;
            imax = 0;
            for (i = k; i < n; i++)
            {
                temp = vv[i]*Math.abs(lu[i][k]);
                if (temp > big)
                {
                    big = temp;
                    imax = i;
                }
            }
            
            if (k != imax)
            {
                for (j = 0; j < n; j++)
                {
                    temp = lu[imax][j];
                    lu[imax][j]=lu[k][j];
                    lu[k][j]=temp;
                }
                vv[imax]=vv[k];
            }
            indx[k]=imax;
            if (lu[k][k] == 0.0)
                lu[k][k]=TINY;
            for (i = k+1; i < n; i++)
            {
                temp = lu[i][k] /= lu[k][k];
                for (j = k+1;j<n;j++)
                    lu[i][j] -= temp *lu[k][j];
            }
        }
        return new Matrix[]{getL(lu), getU(lu), getP(indx)};
    }
    
    private static Matrix getP(int[] indx)
    {
        double[][] d = new double[indx.length][indx.length];
        for (int k = 0; k < indx.length; k++)
        {
            d[k][k] = 1;
        }
        
        for (int k = 0; k < indx.length; k++)
        {
            double t;
            for (int j = 0; j < indx.length; j++)
            {
                t = d[indx[k]][j];
                d[indx[k]][j] = d[k][j];
                d[k][j] = t;
            }
        }
        Matrix P = new Matrix(d);
        return P;
        
    }
    
    private static Matrix getL(double[][] lu)
    {
        double[][] ldata = new double[lu.length][lu.length];
        for (int row = 1; row < lu.length; row++)
        {
            ldata[row][row] = 1;
            for (int col = 0; col < row; col++)
            {
                ldata[row][col] = lu[row][col];
            }
        }
        ldata[0][0] = 1;
        return new Matrix(ldata);
    }
    
    private static Matrix getU(double[][] lu)
    {
        double[][] udata = new double[lu.length][lu.length];
        for (int row = 0; row < lu.length; row++)
        {
            for (int col = row; col < lu.length; col++)
            {
                udata[row][col] = lu[row][col];
            }
        }
        return new Matrix(udata);
    }
    
    public static double[] solve(double[] b, Matrix[] LUP)
    {
        if (!LUP[0].isSquare() || !LUP[1].isSquare() || !LUP[2].isSquare() || b.length != LUP[0].rows)
        {
            System.out.println("Error with dimensions in LU solver");
            return null;
        }
        
        double[] y = forwardSub(b, LUP[0], LUP[2]);
        return backwardSub(y, LUP[1]);
    }
    
    
    private static double[] forwardSub(double[] b, Matrix L, Matrix P)
    {
        int n = b.length;
        
        b = Matrix.multiply(P, new Matrix(b)).toArray();
        double[] y = new double[b.length];
        
        for (int i = 0; i < n; i++)
        {
            double sum = 0;
            for (int j = 0; j < i; j++)
            {
                sum += L.data[i][j]*y[j];
            }
            y[i] = b[i] - sum;
        }
        return y;
    }
    
    private static double[] backwardSub(double[] y, Matrix U)
    {
        int n = y.length;
        
        double[] x = new double[n];
        x[n-1] = y[n-1]/U.data[n-1][n-1];
        
        for (int i = n-2; i >= 0; i--)
        {
            double sum = 0;
            for (int j = i+1; j < n; j++)
                sum += U.data[i][j]*x[j];
            x[i] = (y[i] - sum)/U.data[i][i];
        }
        return x;
    }
    
}