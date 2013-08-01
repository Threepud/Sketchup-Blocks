import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import sketchupblocks.math.Vec4;


public class Vec4Test 
{
	Vec4 testOne;
	Vec4 testTwo;
	
	@Before
	public void setup()
	{
		testOne = new Vec4(1, 2, 3, 4);
		testTwo = new Vec4(5, 6, 7, 8);
	}
	
	@Test
	public void testDot() 
	{
		assertTrue("Vec4: Dot product failed.", 70 == Vec4.dot(testOne, testTwo));
	}

	@Test
	public void testNormalize() 
	{
		Vec4 result = Vec4.normalize(testOne);
		assertTrue("Vec4: Normalize failed.", 1 / Math.sqrt(30) == result.x &&
											  2 / Math.sqrt(30) == result.y &&
											  3 / Math.sqrt(30) == result.z &&
											  4 / Math.sqrt(30) == result.w);
	}

	@Test
	public void testLength() 
	{
		double result = testOne.length();
		assertTrue("Vec4: Length failed.", Math.sqrt(30) == result);
	}

	@Test
	public void testLengthVec4() 
	{
		double result = Vec4.length(testOne);
		assertTrue("Vec4: Length failed.", Math.sqrt(30) == result);
	}

	@Test
	public void testAdd() 
	{
		Vec4 result = Vec4.add(testOne, testTwo);
		assertTrue("Vec4: Add failed.", 6 == result.x &&
										8 == result.y &&
										10 == result.z &&
										12 == result.w);
	}

	@Test
	public void testSubtract() 
	{
		Vec4 result = Vec4.subtract(testOne, testTwo);
		assertTrue("Vec4: Subtract failed.", -4 == result.x &&
											 -4 == result.y &&
											 -4 == result.z &&
											 -4 == result.w);
	}

	@Test
	public void testScalar() 
	{
		Vec4 result = Vec4.scalar(2, testOne);
		assertTrue("Vec4: Scalar failed.", 2 == result.x &&
											 4 == result.y &&
											 6 == result.z &&
											 8 == result.w);
	}

	@Test
	public void testToArray() 
	{
		double[] result = testOne.toArray();
		assertTrue("Vec3: To Array failed.", result[0] == testOne.x &&
											 result[1] == testOne.y &&
											 result[2] == testOne.z);
	}

}
