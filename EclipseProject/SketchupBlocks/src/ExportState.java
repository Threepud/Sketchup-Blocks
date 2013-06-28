
public class ExportState extends MenuState
{
	public ExportState(SessionManager _sessMan)
	{
		super(_sessMan);
	}
	
	public boolean handleInput(CommandBlock cBlock)
	{
		if (cBlock.type == CommandBlock.COMMAND_TYPE.OK)
		{
			sessMan.exportToFile();
			return true;
			
		}
		else if (cBlock.type == CommandBlock.COMMAND_TYPE.CANCEL)
		{
			return true;
		}
		return false;	
	}
}
