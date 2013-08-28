package sketchupblocks.math;
import sketchupblocks.exception.UnexpectedNonSquareMatrixException;
import sketchupblocks.exception.UnexpectedVectorConversionException;
import sketchupblocks.exception.UnexpectedArrayConversionException;

public class Matrix 
{
    public double[][] data;
    public int rows;
    public int cols;

    //Constructor 1
    public Matrix(int _rows, int _cols)
    {
        rows = _rows;
        cols = _cols;
        data = new double[rows][cols];
    }

    //Constructor 2
    public Matrix(double[][] d)
    {
        rows = d.length;
        cols = d[0].length;
        data = d;
    }

    //Constructor 3
    public Matrix(Vec3[] vecs, boolean colVecs)
    {
        if (colVecs)
        {
            rows = 3;
            cols = vecs.length;
            data = new double[rows][cols];

            for (int k = 0; k < cols; k++)
            {
                double[] vdata = vecs[k].toArray();
                for (int i = 0; i < rows; i++)
                {
                    data[i][k] = vdata[i];
                }
            }
        }
        else
        {
            rows = vecs.length;
            cols = 3;
            data = new double[rows][cols];

            for (int k = 0; k < cols; k++)
            {
                for (int i = 0; i < rows; i++)
                {
                    data[i][k] = vecs[i].toArray()[k];
                }
            }
        }
    }

    //Constructor 4
    public Matrix(Vec3 v)
    {
        rows = 3;
        cols = 1;

        data = new double[3][];
        data[0] = new double[]{v.x};
        data[1] = new double[]{v.y};
        data[2] = new double[]{v.z};

    }

    public Matrix(Vec4 v)
    {
        rows = 4;
        cols = 1;

        data = new double[4][];
        data[0] = new double[]{v.x};
        data[1] = new double[]{v.y};
        data[2] = new double[]{v.z};
        data[3] = new double[]{v.w};
    }
	
    
    public Matrix(double[] d)
    {
        cols = 1;
        rows = d.length;
        data = new double[rows][cols];
        for (int k = 0; k < rows; k++)
        {
            data[k][0] = d[k];
        }
    }
    
    public void repeatAsRows(Vec3 v)
    {
        if (cols != 3 || rows != 3)
        {
            System.out.println("Cannot set as repeat vector");
            throw new RuntimeException();
        }
        for (int k = 0; k < 3; k++)
        {
            data[k][0] = v.x;
            data[k][1] = v.y;
            data[k][2] = v.z;
        }
    }
    
    
    
    public void repeatAsCols(Vec3 v)
    {
        if (cols != 3 || rows != 3)
        {
            System.out.println("Cannot set as repeat vector");
            throw new RuntimeException();
        }
        for (int k = 0; k < 3; k++)
        {
            data[0][k] = v.x;
            data[1][k] = v.y;
            data[2][k] = v.z;
        }
    }
    
    public static double determinant(Matrix m) throws UnexpectedNonSquareMatrixException
    {
    	if (!m.isSquare())
    		throw new UnexpectedNonSquareMatrixException("Cannot calculate determinant of nonsquare matrix");
    	if (m.rows == 1 && m.cols == 1)
    	{
    		return m.data[0][0];
    	}
    	
    	double sum = 0;
    	for (int j = 0; j < m.cols; j++)
    	{
    		sum += m.data[0][j]*Math.pow(-1, j)*Matrix.determinant(getMinor(m, 0, j));
    	}
    	
    	return sum;
    }
    
    private static Matrix getMinor(Matrix m, int row, int col)
    {
    	
    	double[][] d = new double[m.rows - 1][m.cols-1];
    	
    	int mi = 0; 
    	int mj = 0;
    	for (int i = 0; i < m.rows; i++)
    	{
    		if (i != row)
    		{
	    		for (int j = 0; j < m.cols; j++)
	    		{
	    			if (j != col)
	    			{
	    				d[mi][mj] = m.data[i][j];
	    				mj++;
	    			}
	    		}
    			mi++;
    			mj = 0;
    		}
    	}
    	return new Matrix(d);
    }
    
    public Matrix padMatrix() throws UnexpectedNonSquareMatrixException
    {
    	if(!isSquare())
    		throw new UnexpectedNonSquareMatrixException("I'm really not that square.");
    	
    	Matrix result = new Matrix(rows + 1, cols + 1);
    	for(int x = 0; x < cols; ++x)
    		for(int i = 0; i < rows; ++i)
    			result.data[x][i] = data[x][i];
    	
    	result.data[rows][cols] = 1;
    	
    	return result;
    }
    
    public boolean isSquare()
    {
        if (rows == cols)
                return true;
        else return false;
    }
    
    
    public Matrix getInverse() throws UnexpectedNonSquareMatrixException, UnexpectedArrayConversionException
    {
    	if (!isSquare())
    		throw new UnexpectedNonSquareMatrixException("Cannot invert a nonsquare matrix");
    	
    	double[][] d = new double[rows][rows];
    	Matrix[] lup = LUDecomposer.decompose(this);
    	for (int i = 0; i < rows; i++)
    	{
    		double[] col = new double[rows];
    		col[i] = 1;
    		double[] x = LUDecomposer.solve(col, lup);
    		for (int j = 0; j < rows; j++)
    		{
    			d[j][i] = x[j];
    		}
    	}
    	
    	return new Matrix(d);
    }
    
    public static Matrix add(Matrix A, Matrix B)
    {
        if (A.cols != B.cols || A.rows != B.rows)
        {
            System.out.println("Cannot add these matrices");
            throw new RuntimeException();
        }
        
        double[][] data = new double[A.rows][A.cols];
        
        for (int k = 0; k < A.rows; k++)
        {
            for (int i = 0; i < A.cols; i++)
            {
                data[k][i] = A.data[k][i] + B.data[k][i];
            }
        }
        return new Matrix(data);
    }
    
    public static Matrix subtract(Matrix A, Matrix B)
    {
        if (A.cols != B.cols || A.rows != B.rows)
        {
            System.out.println("Cannot subtract these matrices");
            throw new RuntimeException();
        }
        
        double[][] data = new double[A.rows][A.cols];
        
        for (int k = 0; k < A.rows; k++)
        {
            for (int i = 0; i < A.cols; i++)
            {
                data[k][i] = A.data[k][i] - B.data[k][i];
            }
        }
        return new Matrix(data);
    }
    
    public double[] toArray() throws UnexpectedArrayConversionException
    {
        if (cols != 1)
        {
             throw new UnexpectedArrayConversionException("Cannot convert matrix to Array");
        }
        double[] res = new double[rows];
        for (int k = 0; k < rows; k++)
        {
            res[k] = data[k][0];
        }
        return res;
    }
    
	 public Vec3 toVec3() throws UnexpectedVectorConversionException 
	 {
	     if (cols != 1 || rows < 3)
	     {
			throw new UnexpectedVectorConversionException("Cannot convert matrix to vec3");
	    	 
	     }
	     else
	     {
	         Vec3 result = new Vec3(data[0][0], data[1][0], data[2][0]);
	         return result;
	     }
	 }
	
	 public Vec4 toVec4() throws UnexpectedVectorConversionException
	 {
	     if (cols != 1 || rows != 4)
	     {
	         throw new UnexpectedVectorConversionException("Cannot convert matrix to vec4");
	     }
	     else
	     {
	         Vec4 result = new Vec4(data[0][0], data[1][0], data[2][0], data[3][0]);
	         return result;
	     }
	 }
	
	public Vec3[] toVec3Array() throws UnexpectedVectorConversionException
	{
	    if (rows != 3)
	    {
	        throw new UnexpectedVectorConversionException("Cannot convert matrix to vec3Array");
	    }
	
	    Vec3[] res = new Vec3[cols];
	
	    for (int k = 0; k < cols; k++)
	    {
	        res[k] = new Vec3(data[0][k], data[1][k], data[2][k]);
	    }
	    return res;
	    
	}
	
	public static Vec3 multiply(Matrix m1, Vec3 v)
	{
		if (m1.cols != 3)
		{
			System.out.println("Error!! Cannot multiply with vec3 "+m1.cols);
			throw new RuntimeException();
		}
		
		double[] vdata = v.toArray();
		double[] resData = new double[3];
		
		for (int row = 0; row < m1.rows; row++)
		{
			resData[row] = 0;
			for (int mcol = 0; mcol < m1.cols; mcol++)
			{
				resData[row] += m1.data[row][mcol]*vdata[mcol];
			}
			
		}
		
		return new Vec3(resData[0], resData[1], resData[2]);
	}
	
	public static Vec4 multiply(Matrix m1, Vec4 v)
	{
		if (m1.cols != 4)
		{
			System.out.println("Error!! Cannot multiply with vec4 "+m1.cols);
			throw new RuntimeException();
		}
		
		double[] vdata = v.toArray();
		double[] resData = new double[4];
		
		for (int row = 0; row < m1.rows; row++)
		{
			resData[row] = 0;
			for (int mcol = 0; mcol < m1.cols; mcol++)
			{
				resData[row] += m1.data[row][mcol]*vdata[mcol];
			}
			
		}
		
		return new Vec4(resData[0], resData[1], resData[2], resData[3]);
	}
	
	public static Matrix multiply(Matrix m1, Matrix m2)
	    {
	        if (m1.cols != m2.rows)
	        {
	            System.out.println("Invalid multiplication\nm1.cols: "+m1.cols+"\tm2.rows: "+m2.rows);
	            try
	            {
	            	throw new RuntimeException();
	            }
	            catch(Exception e)
	            {
	            	e.printStackTrace();
	            }
		    }
		    Matrix res = new Matrix(m1.rows, m2.cols);
		
		    for (int r1 = 0; r1 < m1.rows; r1++)
		    {
		        for (int c2 = 0; c2 < m2.cols; c2++)
		        {
		            for (int c1r2 = 0; c1r2 < m1.cols; c1r2++)
		            {
		                res.data[r1][c2] += m1.data[r1][c1r2]*m2.data[c1r2][c2];
		            }
		        }
		    }
	
	    return res;
	}
	
	public static Matrix identity(int n)
	{
		double[][] d = new double[n][n];
		for (int k = 0; k < n; k++)
			d[k][k] = 1;
		return new Matrix(d);
	}
	
	public static Matrix scalar(double s, Matrix m)
	{
		double[][] d = new double[m.rows][m.cols];
		for (int k = 0; k < m.rows; k++)
		{
			for (int i = 0; i < m.cols; i++)
			{
				d[k][i] = m.data[k][i]*s;
			}
		}
		return new Matrix(d);
	}
	
	public Matrix transpose()
	{
	    Matrix res = new Matrix(cols, rows);
	    for (int row = 0; row < cols; row++)
	    {
	        for (int col = 0; col < rows; col++)
	        {
	                res.data[row][col] = data[col][row];
	        }
	    }
	    
	    return res;
	}
	
	@Override
    public String toString()
    {
        String res = "[";
        for (int r = 0; r < rows; r++)
        {
                for (int c = 0; c < cols; c++)
                        res += data[r][c]+" ";
                res += ";";
        }
        res += "]";
        return res;
    }
}
