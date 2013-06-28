
public class OverwriteState extends MenuState
{
	boolean yesno;
	
	OverwriteState(SessionManager _sessMan)
	{
		super(_sessMan);
		yesno = false;
	}
	
	public boolean handleInput(CommandBlock cBlock)
	{
		if (cBlock.type == CommandBlock.COMMAND_TYPE.OK)
		{
			yesno = true;
			return true;
		}
		else if (cBlock.type == CommandBlock.COMMAND_TYPE.CANCEL)
		{
			yesno = false;
			return true;
		}
		return false;
	}
}
