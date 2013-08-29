
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import sketchupblocks.gui.Menu;

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
		menu = new Menu(null, null);
	}
	
	
	
	@Test
	public void testConstructor()
	{
		
	}

} 
