
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runners.*;
import org.junit.runner.RunWith;

import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;
import sketchupblocks.math.Vec4;

/*
Run with:
java -cp .;junit.jar;hamcrest-core-1.3.jar org.junit.runner.JUnitCore Matrix

*/

@RunWith(JUnit4.class)
public class MatrixTest
{
	Matrix square;
	Matrix rectangle;
	
	@Before
	public void setup()
	{
		double[][] data = new double[3][3];
		data[0] = new double[]{1, 2, 3};
		data[1] = new double[]{-1, 5, -2};
		data[2] = new double[]{4, 2, -1};
		square = new Matrix(data);
		
		data = new double[3][2];
		data[0] = new double[]{1, 2};
		data[1] = new double[]{-2, 2};
		data[2] = new double[]{1, 5};
		rectangle = new Matrix(data);
	}
	
	public boolean matchMatrices(Matrix m1, Matrix m2)
	{
		if (m1.rows != m2.rows || m1.cols != m2.cols)
			return false;
		for (int i = 0; i < m1.rows; i++)
			for (int k = 0; k < m1.cols; k++)
				if (m1.data[i][k] != m2.data[i][k])
				{
					System.out.println(i+","+k+" = "+m1.data[i][k]+" <> "+m2.data[i][k]);
					return false;
				}
		return true;
	}
	
	@Test
	public void testConstructor1()
	{
		Matrix m = new Matrix(4, 5);
		assertTrue("Column and row numbers incorrectly initialized (Constructor 1)",m.cols == 5 && m.rows == 4);
	}
	
	@Test
	public void testConstructor2()
	{
		double[][] data = new double[1][2];
		data[0] = new double[]{1, 2};
		
		Matrix m = new Matrix(data);
		assertTrue("Column and row numbers incorrectly initialized (Constructor 2)",m.cols == 2 && m.rows == 1);
		assertTrue("Data incorrectly initialized", m.data[0][0] == 1 && m.data[0][1] == 2);
		
		m = new Matrix(data);
		assertTrue("Matrix size error not noticed", m.data == null);
	}
	
	@Test
	public void testConstructor3()
	{
		Vec3[] colVecs = new Vec3[2]; 
		colVecs[0] = new Vec3(1, 2, 3);
		colVecs[1] = new Vec3(-1, -2, -3);
		
		Matrix m = new Matrix(colVecs, true);
		assertTrue("Column and row numbers incorrectly initialized (Constructor 3)",m.cols == 2 && m.rows == 3);
		assertTrue("Column vectors incorrectly augmented", m.data[0][0] == 1 && m.data[0][1] == -1);
		
		m = new Matrix(colVecs, false);
		assertTrue("Column and row numbers incorrectly initialized (Constructor 3)",m.cols == 3 && m.rows == 2);
		assertTrue("Row vectors incorrectly augmented", m.data[0][0] == 1 && m.data[0][1] == 2 && m.data[0][2] == 3);
	}
	
	@Test
	public void testConstructor4()
	{
		Vec3 colVec = new Vec3(1, 2, 3);
		
		Matrix m = new Matrix(colVec);
		assertTrue("Column and row numbers incorrectly initialized (Constructor 4)",m.cols == 1 && m.rows == 3);
		assertTrue("Column vec3 incorrectly augmented", m.data[0][0] == 1 && m.data[1][0] == 2);
		
	}
	
	@Test
	public void testConstructor5()
	{
		Vec4 colVec = new Vec4(1, 2, 3, 4);
		
		Matrix m = new Matrix(colVec);
		assertTrue("Column and row numbers incorrectly initialized (Constructor 5)",m.cols == 1 && m.rows == 4);
		assertTrue("Column vec4 incorrectly augmented", m.data[0][0] == 1 && m.data[1][0] == 2);
	
	}
	
	@Test
	public void testInverse()
	{
		try
		{
			Matrix inv = square.getInverse();
			assert(approximatelyIdentity(Matrix.multiply(inv, square)));
		}
		catch(Exception e)
		{
			
		}
		
	}
	
	private boolean approximatelyIdentity(Matrix one)
	{
		for (int k = 0; k < one.rows; k++)
		{
			for (int i = 0; i < one.cols; i++)
			{
				if (k != i)
				{
					if(Math.abs(one.data[k][i]) > 0.000001)
						return false;
				}
				else
				{
					if (Math.abs(one.data[k][i] - 1) > 0.000001)
						return false;
				}
			}
		}
		return true;
	}
	
	@Test
	public void testToVec3()
	{
		double[][] data = new double[1][1];
		data[0][0] = 1;
		Matrix m = new Matrix(data);
		try
		{
			Vec3 v = square.toVec3();
		    fail("Didn't throw vec3 conversion exception: ("+v.x+", "+v.y+", "+v.z+")");
		}
		catch(Exception e)
		{
		    assertEquals("Cannot convert matrix to vec3", e.getMessage());
		}
		
		try
		{
			m.toVec3();
		    fail("Didn't throw vec3 conversion exception");
		}
		catch(Exception e)
		{
		    assertEquals("Cannot convert matrix to vec3", e.getMessage());
		}
		
		m = new Matrix(new Vec3(1, 2, 3));
		try
		{
			Vec3 vec = m.toVec3();
			assertTrue("Incorrect conversion to vec3", vec.x == 1 && vec.y == 2 && vec.z == 3);
		}
		catch(Exception e)
		{
		   fail("Unexpected exception");
		}
	}
	
	@Test
	public void testToVec4()
	{
		double[][] data = new double[1][1];
		data[0][0] = 1;
		Matrix m = new Matrix(data);
		try
		{
			square.toVec4();
		    fail("Didn't throw vec4 conversion exception");
		}
		catch(Exception e)
		{
		    assertEquals("Cannot convert matrix to vec4", e.getMessage());
		}
		
		try
		{
			m.toVec4();
		    fail("Didn't throw vec4 conversion exception");
		}
		catch(Exception e)
		{
		    assertEquals("Cannot convert matrix to vec4", e.getMessage());
		}
		
		m = new Matrix(new Vec4(1, 2, 3, 4));
		try
		{
			Vec4 vec = m.toVec4();
			assertTrue("Incorrect conversion to vec4", vec.x == 1 && vec.y == 2 && vec.z == 3 && vec.w == 4);
		}
		catch(Exception e)
		{
		   fail("Unexpected exception");
		}
	}

	@Test
	public void testMatrixMultiplication()
	{
		//Square-square
		Matrix ss = Matrix.multiply(square, square);
		double[][] d = new double[3][3];
		d[0] = new double[]{11, 18, -4};
		d[1] = new double[]{-14, 19, -11};
		d[2] = new double[]{-2, 16, 9};
		Matrix ssC = new Matrix(d);
		assertTrue("Incorrect square-square multiplication", matchMatrices(ss, ssC));
		//Square-nonsquare
		ss = Matrix.multiply(square,  rectangle);
		
		d = new double[3][2];
		d[0] = new double[]{0, 21};
		d[1] = new double[]{-13, -2};
		d[2] = new double[]{-1, 7};
		ssC = new Matrix(d);
		assertTrue("Incorrect square-rectangular multiplication", matchMatrices(ss, ssC));
		
		//Invalid multiplication
		assertTrue("Muliplication should be invalid", Matrix.multiply(rectangle, square) == null);
	}
	
	@Test
	public void testTranspose()
	{
		double[][] d = new double[3][3];
		d[0] = new double[]{1, -1, 4};
		d[1] = new double[]{2, 5, 2};
		d[2] = new double[]{3, -2, -1};
		
		assertTrue("Incorrect transpose", matchMatrices(square.transpose(), new Matrix(d)));
	}

}