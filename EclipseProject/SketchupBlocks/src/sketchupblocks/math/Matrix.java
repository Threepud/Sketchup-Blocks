package sketchupblocks.math;
import sketchupblocks.exception.UnexpectedVectorConversionException;

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
    public Matrix(int _rows, int _cols, double[][] d)
    {
        rows = _rows;
        cols = _cols;
        data = d;
        if (d.length != _rows || d[0].length != _cols)
        {
            System.out.println("Mismatch netween specified row x col numbers and actual data");
            rows = -1;
            cols = -1;
            data= null;
        }
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
	
	public static Vec3 multiply(Matrix m1, Vec3 v)
	{
		if (m1.cols != 3)
		{
			System.out.println("Error!! Cannot multiply with vec3");
			return null;
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
			System.out.println("Error!! Cannot multiply with vec3");
			return null;
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
	        return null;
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
        String res = "";
        for (int r = 0; r < rows; r++)
        {
                for (int c = 0; c < cols; c++)
                        res += data[r][c]+"\t";
                res += "\n";
        }
        return res;
    }
}
