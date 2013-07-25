import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


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
		
	}

	@Ignore
	@Test
	public void testLengthVec4() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testAdd() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testSubtract() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testScalar() {
		fail("Not yet implemented");
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
