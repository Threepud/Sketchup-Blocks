

public class LUDecomposer 
 {
	
	
    
  //  public static double d = 1;

    public static DecompositionResult decompose(double[][] a)
    {
        int n = a.length;
        double[][] lu = a;
        double[] indx = new double[n];
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
                System.out.println("Singular matrix");
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
            if (imax == -100)
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
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
        DecompositionResult res = new DecompositionResult(n, lu, indx);
        return res;
    }
    public static double[] solve(double[] b, DecompositionResult res)
    {
        double[] x = new double[b.length];
        int i;
        int ii = 0;
        int ip;
        int j;
        double sum;
        if (b.length != res.n)
            System.out.println("Size problem in solver");
        for (i = 0; i < res.n; i++)
            x[i] = b[i];
        for (i = 0; i < res.n; i++)
        {
            ip = (int)res.indx[i];
            sum = x[ip];
            x[ip]=x[i];
            if (ii != 0)
            for (j = ii-1;j<i;j++)
                sum -= res.lu[i][j]*x[j];
            else if (sum != 0.0)
                ii = i+1;
            x[i]=sum;
        }
        for (i = res.n-1; i >= 0; i--)
        {
            sum = x[i];
            for (j = i+1; j < res.n; j++)
                sum -= res.lu[i][j]*x[j];
            x[i]=sum/res.lu[i][i];
        }
        return x;
    }
}

class DecompositionResult
{
	protected int n;
	protected double[][] lu;
	protected double[] indx;
	DecompositionResult(int _n, double[][] _lu, double[] _indx)
	{
		n = _n;
		lu = _lu;
		indx = _indx;
	}
}