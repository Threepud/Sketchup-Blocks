
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runners.*;
import org.junit.runner.RunWith;
import sketchupblocks.base.Interpreter;

/*
Run with:
java -cp .;junit.jar;hamcrest-core-1.3.jar org.junit.runner.JUnitCore Matrix

*/

@RunWith(JUnit4.class)
public class InterpreterTest
{
	
	@Before
	public void setup()
	{
		
	}
	
	
	
	@Test
	public void testConstructor()
	{
		Interpreter i = new Interpreter(5, null, null, 10);
		assertTrue("CameraID incorrectly set", 10 == i.getCameraID());
	}

} 
