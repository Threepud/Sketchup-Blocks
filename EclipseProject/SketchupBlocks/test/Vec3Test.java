import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class Vec3Test 
{
	Vec3 testOne;
	Vec3 testTwo;
	
	@Before
	public void setup() 
	{
		testOne = new Vec3(1, 2, 3);
		testTwo = new Vec3(4, 5, 6);
	}

	@Test
	public void testDot()
	{
		assertTrue("Vec3: Dot product failed.", 32 == Vec3.dot(testOne, testTwo));
	}
	
	@Test
	public void testCross()
	{
		Vec3 result = Vec3.cross(testOne, testTwo);
		assertTrue("Vec3: Cross product failed.", -3 == result.x &&
												  6 == result.y &&
												  -3 == result.z);
	}
	
	@Test
	public void testNormalizeVec3()
	{
		Vec3 result = Vec3.normalize(testOne);
		assertTrue("Vec3: Normalize failed.", 1 / Math.sqrt(14) == result.x &&
											  2 / Math.sqrt(14) == result.y &&
											  3 / Math.sqrt(14) == result.z);
	}
	
	@Test
	public void testNormalize()
	{
		Vec3 temp = new Vec3(testOne.x, testOne.y, testOne.z);
		temp.normalize();
		assertTrue("Vec3: Normalize failed.", 1 / Math.sqrt(14) == temp.x &&
				  2 / Math.sqrt(14) == temp.y &&
				  3 / Math.sqrt(14) == temp.z);
	}
	
	@Test
	public void testLengthDouble()
	{
		double result = Vec3.length(testOne);
		assertTrue("Vec3: Length failed.", Math.sqrt(14) == result);
	}
	
	@Test
	public void testLength()
	{
		double result = testOne.length();
		assertTrue("Vec3: Length failed.", Math.sqrt(14) == result);
	}
	
	@Test
	public void testAdd()
	{
		Vec3 result = Vec3.add(testOne, testTwo);
		assertTrue("Vec3: Add failed.", 5 == result.x &&
										7 == result.y &&
										9 == result.z);
	}
	
	@Test
	public void testSubtract()
	{
		Vec3 result = Vec3.subtract(testOne, testTwo);
		assertTrue("Vec3: Subtract failed.", -3 == result.x &&
											 -3 == result.y &&
											 -3 == result.z);
	}
	
	@Test
	public void testScalar()
	{
		Vec3 result = Vec3.scalar(2, testOne);
		assertTrue("Vec3: Scalar failed.", 2 == result.x &&
									   	   4 == result.y &&
										   6 == result.z);
	}
	
	@Test
	public void testToArray()
	{
		double[] result = testOne.toArray();
		assertTrue("Vec3: To Array failed.", result[0] == testOne.x &&
											 result[1] == testOne.y &&
											 result[2] == testOne.z);
	}
	
	@Test
	public void testDistance()
	{
		double result = testOne.distance(testTwo);
		assertTrue("Vec3: Distance failed.", Math.sqrt(27) == result);
	}
}
