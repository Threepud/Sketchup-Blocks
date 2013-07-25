
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runners.*;
import org.junit.runner.RunWith;

/*
Run with:
java -cp .;junit.jar;hamcrest-core-1.3.jar org.junit.runner.JUnitCore Matrix

*/

@RunWith(JUnit4.class)
public class LinearSolverTest
{
	
	@Before
	public void setup()
	{
		
	}
	
	
	
	@Test
	public void testSolve()
	{
		double[] a = new double[]{1, 2};
		Vec3 v = LinearSystemSolver.solve(null, a);
		assertTrue("Invalid angle list", v == null);
		
		double[][] t = new double[2][2];
		t[0] = new double[]{2, 4};
		t[1] = new double[]{1, 2};
		double[] b = new double[]{10, 5};
		
		
		System.out.println(LUDecomposer.multiplesFound(b, t));
		
		/*DecompositionResult  res = LUDecomposer.decompose(t);
		double[] bla = LUDecomposer.solve(b, res);
		assertTrue("Matrix to solve has rows that aren't linearly independent", bla == null);*/
	}

} 
