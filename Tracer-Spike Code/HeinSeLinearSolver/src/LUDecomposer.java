

public class LUDecomposer 
 {
    public static int n;
    public static double[][] lu;
    public static double[] indx;
    public static double d = 1;

    public static void decompose(double[][] a)
    {
        n = a.length;
        lu = a;
        //aref = a.argvalue;
        indx = new double[n];
        final double TINY = 1.0e-40;
        int i;
        int imax;
        int j;
        int k;
        double big;
        double temp;
        double[] vv = new double[n];
        d = 1.0;

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
                d = -d;
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
    }
    public static double[] solve(double[] b)
    {
        double[] x = new double[b.length];
        int i;
        int ii = 0;
        int ip;
        int j;
        double sum;
        if (b.length != n)
            System.out.println("Size problem in solver");
        for (i = 0; i < n; i++)
            x[i] = b[i];
        for (i = 0; i < n; i++)
        {
            ip = (int)indx[i];
            sum = x[ip];
            x[ip]=x[i];
            if (ii != 0)
            for (j = ii-1;j<i;j++)
                sum -= lu[i][j]*x[j];
            else if (sum != 0.0)
                ii = i+1;
            x[i]=sum;
        }
        for (i = n-1; i >= 0; i--)
        {
            sum = x[i];
            for (j = i+1;j<n;j++)
                sum -= lu[i][j]*x[j];
            x[i]=sum/lu[i][i];
        }
        return x;
    }
}