/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sketchupblocks.math;

import sketchupblocks.exception.NoConvergenceException;

/**
 *
 * @author cravingoxygen
 */
public class SVDecomposer 
{
    //Return U, W, V
    public static Matrix[] decompose(Matrix A) throws NoConvergenceException
    {
        Matrix oldA = new Matrix(A.data.clone());
        A = prep(A);
        
        //Now A can be treated as a Matrix with 1-based indexing.
        int n = A.cols-1;
        int m = A.rows-1;
        
        
        int i, its, j, jj, k, l = 0, nm = 0, flag;
        double g = 0;
        double scale = 0;
        double anorm = 0;
        double c, f, h, s ,x, y, z;
        double[] rv1 = new double[n+1];
        double[] w = new double[n+1];
        double[][] v = new double[n+1][n+1];
        
        for (i = 1; i <= n;i++) 
	{
            l = i+1;
            rv1[i] = scale*g;
            g = 0.0;
            s = 0.0;
            scale = 0.0;
            if (i <= m) 
            {
                for (k = i; k <= m; k++) 
                    scale += Math.abs(A.data[k][i]);
                if (scale != 0.0) 
                {
                    for (k = i; k <= m; k++) 
                    {
                        A.data[k][i] /= scale;
                        s += A.data[k][i]*A.data[k][i];
                    }
                    f = A.data[i][i];
                    g = (-1)*(f > 0 ? Math.sqrt(s) : -1*Math.sqrt(s));//Unicorn! -SIGN(Math.sqrt(s),f);
                    h = f*g - s;
                    A.data[i][i] = f - g;
                    for (j = l; j <= n; j++) 
                    {
                        for (s = 0.0, k = i; k <= m; k++) 
                            s += A.data[k][i]*A.data[k][j];
                        f=s/h;
                        for (k = i; k <= m; k++) 
                            A.data[k][j] += f*A.data[k][i];
                    }
                    for (k = i; k <= m; k++) 
                        A.data[k][i] *= scale;
                }
            }
            w[i] = scale*g;
            g = 0.0;
            s = 0.0;
            scale =0.0;
            if (i <= m && i != n) 
            {
                for (k = l; k <= n; k++) 
                        scale += Math.abs(A.data[i][k]);
                if (scale != 0.0) 
                {
                    for (k = l; k <= n; k++) 
                    {
                        A.data[i][k] /= scale;
                        s += A.data[i][k]*A.data[i][k];
                    }
                    f = A.data[i][l];
                    g = (-1)*(f > 0 ? Math.sqrt(s) : Math.sqrt(s)*-1);
                    h = f*g - s;
                    A.data[i][l] = f - g;
                    for (k = l; k <= n; k++) 
                            rv1[k] = A.data[i][k]/h;
                    for (j = l; j <= m; j++) 
                    {
                            for (s = 0.0, k = l; k <= n; k++) 
                                    s += A.data[j][k]*A.data[i][k];
                            for (k = l; k <= n; k++) 
                                    A.data[j][k] += s*rv1[k];
                    }
                    for (k = l; k <= n; k++) 
                            A.data[i][k] *= scale;
                }
            }
            anorm = Math.max(anorm,(Math.abs(w[i])+Math.abs(rv1[i])));
	}
	for (i = n; i >= 1; i--) 
	{ 
            if (i < n) 
            {
                if (g != 0.0) 
                {
                    for (j = l; j <= n; j++) //Double division to avoid possible underflow.
                            v[j][i]=(A.data[i][j]/A.data[i][l])/g;
                    for (j = l; j<= n; j++) 
                    {
                        for (s = 0.0, k = l; k <= n; k++) 
                            s += A.data[i][k]*v[k][j];
                        for (k = l; k <= n; k++) 
                            v[k][j] += s*v[k][i];
                    }
                }
                for (j = l; j <= n; j++) 
                {
                    v[i][j] = 0.0;
                    v[j][i] = 0.0;
                }
            }
            v[i][i] = 1.0;
            g = rv1[i];
            l = i;
	}
	for (i = Math.min(m,n); i >= 1; i--) 
	{ //Accumulation of left-hand transformations.
            l = i+1;
            g = w[i];
            for (j = l; j <= n; j++) 
            A.data[i][j] = 0.0;
            if (g != 0.0) 
            {
                g = 1.0/g;
                for (j = l; j <= n; j++) 
                {
                    for (s = 0.0, k = l; k <= m; k++) 
                        s += A.data[k][i]*A.data[k][j];
                    f = (s/A.data[i][i])*g;
                    for (k = i; k <= m; k++) 
                        A.data[k][j] += f*A.data[k][i];
                }
                for (j = i; j <= m; j++) 
                    A.data[j][i] *= g;
            } 
            else 
            {
                for (j = i; j <= m; j++) 
                    A.data[j][i] = 0.0;
            }
            ++A.data[i][i];
	}
	for (k = n; k >= 1; k--) 
	{ //Diagonalization of the bidiagonal form: Loop over
	//singular values, 
            for (its = 1; its <= 30; its++) 
            { //and over allowed iterations.
                flag = 1;
                for (l = k; l >= 1; l--) 
                { //Test for splitting.
                    nm = l-1; //Note that rv1[1] is always zero.
                    if ((Math.abs(rv1[l])+anorm) == anorm) 
                    {
                        flag = 0;
                        break;
                    }
                    if ((Math.abs(w[nm])+anorm) == anorm) 
                        break;
                }
                if (flag != 0) 
                {
                    c = 0.0; //Cancellation of rv1[l], if l > 1.
                    s = 1.0;
                    for (i = l; i <= k; i++) 
                    {
                        f = s*rv1[i];
                        rv1[i] = c*rv1[i];
                        if (((Math.abs(f))+anorm) == anorm) 
                            break;
                        g = w[i];
                        h = pythag(f,g);
                        w[i] = h;
                        h = 1.0/h;
                        c = g*h;
                        s = -f*h;
                        for (j = 1; j <= m; j++) 
                        {
                            y = A.data[j][nm];
                            z = A.data[j][i];
                            A.data[j][nm] = y*c+z*s;
                            A.data[j][i] = z*c-y*s;
                        }
                    }
                }
                z = w[k];
                if (l == k) 
                { //Convergence.
                        if (z < 0.0) 
                        {// Singular value is made nonnegative.
                                w[k] = -z;
                                for (j=1;j<=n;j++) 
                                        v[j][k] = -v[j][k];
                        }
                        break;
                }
                if (its == 30) 
                {
                    System.out.println("no convergence in 30 svdcmp iterations");
                    System.out.println(oldA);
                    throw new NoConvergenceException("No Convergence in 30 svdcmp Iterations");
                }
                x = w[l];// Shift from bottom 2-by-2 minor.
                nm = k-1;
                y = w[nm];
                g = rv1[nm];
                h = rv1[k];
                f = ((y-z)*(y+z)+(g-h)*(g+h))/(2.0*h*y);
                g = pythag(f,1.0);
                f = ((x-z)*(x+z)+h*( (y/( f+ (f > 0 ? Math.abs(g) : -1*Math.abs(g)) )) -h))/x;
                c = s = 1.0;// Next QR transformation:
                for (j = l; j <= nm; j++) 
                {
                    i = j+1;
                    g = rv1[i];
                    y = w[i];
                    h = s*g;
                    g = c*g;
                    z = pythag(f,h);
                    rv1[j] = z;
                    c = f/z;
                    s = h/z;
                    f = x*c+g*s;
                    g = g*c-x*s;
                    h = y*s;
                    y *= c;
                    for (jj = 1; jj <= n; jj++) 
                    {
                        x = v[jj][j];
                        z = v[jj][i];
                        v[jj][j] = x*c+z*s;
                        v[jj][i] = z*c-x*s;
                    }
                    z = pythag(f,h);
                    w[j] = z; //Rotation can be arbitrary if z = 0.
                    if (z != 0.0) 
                    {
                        z = 1.0/z;
                        c = f*z;
                        s = h*z;
                    }
                    f = c*g+s*y;
                    x = c*y-s*g;

                    for (jj = 1; jj <= m; jj++) 
                    {
                        y = A.data[jj][j];
                        z = A.data[jj][i];
                        A.data[jj][j] = y*c+z*s;
                        A.data[jj][i] = z*c-y*s;
                    }
                }
                rv1[l] = 0.0;
                rv1[k] = f;
                w[k] = x;
            }
	}
        Matrix W = new Matrix(n, n);
        double[][] d1 = new double[n][n];
        for (k = 1; k <= n; k++)
        {
            d1[k-1][k-1] = w[k];
        }
        W.data = d1;
        
        Matrix V = new Matrix(v);
        V = unprep(V);
        A = unprep(A);
        
        Matrix[] res = new Matrix[]{A, W, V};
        
        return res;
        
    }
    
    private static double pythag(double a, double b)
    {
        return Math.sqrt(a*a + b*b);
    }
    
    private static Matrix unprep(Matrix A)
    {
        Matrix res = new Matrix(A.rows-1, A.cols-1);
        double[][] d = new double[res.rows][res.cols];
        for (int k = 1; k < A.rows; k++)
        {
            for (int i = 1; i < A.cols; i++)
                d[k-1][i-1] = A.data[k][i];
        }
        res.data = d;
        return res;
    }
    
    private static Matrix prep(Matrix A)
    {
        Matrix res = new Matrix(A.rows+1, A.cols+1);
        double[][] d = new double[res.rows][res.cols];
        for (int i = 0; i < res.cols; i++)
        {
            d[0][i] = 0;
        }
        for (int k = 0; k < res.rows; k++)
        {
            d[k][0] = 0;
        }
        
        for (int k = 1; k <= res.rows-1; k++)
        {
            for (int i = 1; i <= res.cols-1; i++)
                d[k][i] = A.data[k-1][i-1];
        }
        res.data = d;
        return res;
    }
}
