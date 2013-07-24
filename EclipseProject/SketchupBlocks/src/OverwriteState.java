
public class OverwriteState extends MenuState
{
	boolean yesno;
	
	public OverwriteState(SessionManager _sessMan)
	{
		super(_sessMan);
		yesno = false;
		System.out.println("Are you sure you would like to overwrite this slot?");
	}
	
	public boolean handleInput(CommandBlock cBlock, CameraEvent cEvent)
	{
		if (cBlock.type == CommandBlock.CommandType.OK)
		{
			yesno = true;
			if (Settings.verbose >=1)
				System.out.println("Overwrite of slot has been confirmed");
			return true;
		}
		else if (cBlock.type == CommandBlock.CommandType.CANCEL)
		{
			yesno = false;
			if (Settings.verbose >=1)
				System.out.println("Overwrite of slot has been cancelled");
			return true;
		}
		else if (Settings.verbose >= 3)
		{
			System.out.println("--OverwriteState received garbage input: "+cBlock.type.name()+"--");
		}
		
		return false;
	}
}
