
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
public class MenuTest
{
	Menu menu;
	
	@Before
	public void setup()
	{
		menu = new Menu(null);
	}
	
	
	
	@Test
	public void testConstructor()
	{
		
	}

} 
