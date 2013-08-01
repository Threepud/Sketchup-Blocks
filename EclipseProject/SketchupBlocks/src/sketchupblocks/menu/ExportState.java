package sketchupblocks.menu;

import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.CommandBlock;
import sketchupblocks.base.SessionManager;
import sketchupblocks.base.Settings;

public class ExportState extends MenuState
{
	public ExportState(SessionManager _sessMan)
	{
		super(_sessMan);
		System.out.println("Are you sure you would like to export this project to file?");
	}
	
	public boolean handleInput(CommandBlock cBlock, CameraEvent cEvent)
	{
		if (Settings.verbose >= 3)
			System.out.println("--ExportState is handling input--");
		if (cBlock.type == CommandBlock.CommandType.OK)
		{
			sessMan.exportToFile();
			if (Settings.verbose >= 1)
				System.out.println("Export of file confirmed");
			return true;
			
		}
		else if (cBlock.type == CommandBlock.CommandType.CANCEL)
		{
			if (Settings.verbose >= 1)
				System.out.println("Export of file cancelled");
			return true;
		}
		else if (Settings.verbose >= 3)
			System.out.println("--ExportState received garbage input: "+cBlock.type.name()+"--");
		return false;	
	}
}
