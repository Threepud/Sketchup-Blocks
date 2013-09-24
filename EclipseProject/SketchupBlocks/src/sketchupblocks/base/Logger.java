package sketchupblocks.base;
import sketchupblocks.base.Settings;


public class Logger 
{
	public static void log(String msg, int minVerboseLevel)
	{
		if (Settings.verbose > minVerboseLevel)
		{
			System.out.println(msg);
		}
	}
}
