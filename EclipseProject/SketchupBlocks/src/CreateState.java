
public class CreateState extends MenuState
{
	public CreateState(SessionManager _sessMan)
	{
		super(_sessMan);
	}
	
	public boolean handleInput(CommandBlock cBlock)
	{

		if (cBlock.type == CommandBlock.CommandType.OK)
		{
			sessMan.newProject();
			return true;
			
		}
		else if (cBlock.type == CommandBlock.CommandType.CANCEL)
		{
			return true;
		}
		return false;	
	}
}
