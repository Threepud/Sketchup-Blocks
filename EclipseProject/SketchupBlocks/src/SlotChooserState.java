
public class SlotChooserState extends MenuState
{
	int chosenSlot = 0;
	float x;
	protected OverwriteState childState;
	SlotChooserState(SessionManager _sessMan)
	{
		super(_sessMan);
	}
	
	public boolean handleInput(CommandBlock cBlock)
	{
		if (childState != null)
		{
			if (cBlock.type == CommandBlock.COMMAND_TYPE.ROTATE)
			{
				//Funky angle and slot stuff.
				return false;
			}
			else if (cBlock.type == CommandBlock.COMMAND_TYPE.OK)
			{
				//If slot is already full
				childState = new OverwriteState(sessMan);
				return false;
			}
			else if (cBlock.type == CommandBlock.COMMAND_TYPE.CANCEL)
			{
				return true;
			}
			else return false;
		}
		else
		{
			boolean childDone = childState.handleInput(cBlock);
			if (childDone && !childState.yesno)
			{
				childState = null;
				return false;
			}
			return childDone;
		}
	}
}
