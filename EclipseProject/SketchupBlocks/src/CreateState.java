
public class CreateState extends MenuState
{
	public CreateState(SessionManager _sessMan)
	{
		super(_sessMan);
	}
	
	public boolean handleInput(CommandBlock cBlock)
	{

		if (cBlock.type == CommandBlock.COMMAND_TYPE.OK)
		{
			sessMan.newProject();
			return true;
			
		}
		else if (cBlock.type == CommandBlock.COMMAND_TYPE.CANCEL)
		{
			return true;
		}
		return false;	
	}
}
