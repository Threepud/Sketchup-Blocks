package sketchupblocks.menu;

import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.CommandBlock;
import sketchupblocks.base.SessionManager;
import sketchupblocks.base.Settings;

public class CreateState extends MenuState
{
	public CreateState(SessionManager _sessMan)
	{
		super(_sessMan);
		System.out.println("Are you sure you would like to create a new project?");
	}
	
	public boolean handleInput(CommandBlock cBlock, CameraEvent cEvent)
	{
		if (Settings.verbose >= 3)
			System.out.println("--Create state is handling input--");
		if (cBlock.type == CommandBlock.CommandType.OK)
		{
			if (Settings.verbose >= 1)
				System.out.println("Creation of new project has been confirmed");
			sessMan.newProject();
			return true;
			
		}
		else if (cBlock.type == CommandBlock.CommandType.CANCEL)
		{
			if (Settings.verbose >= 1)
				System.out.println("Creation of new project haas been cancelled");
			return true;
		}
		
		if (Settings.verbose >= 3)
			System.out.println("--CreateState received some garbage input: "+cBlock.type.name()+"--");
		return false;	
	}
}
