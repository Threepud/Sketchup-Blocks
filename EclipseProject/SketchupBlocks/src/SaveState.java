
public class SaveState extends MenuState
{
	protected SlotChooserState childState;
	public SaveState(SessionManager _sessMan)
	{
		super(_sessMan);
		childState = new SlotChooserState(_sessMan);
	}
	
	public boolean handleInput(CommandBlock cBlock)
	{
		boolean childDone = childState.handleInput(cBlock);
		if (childDone)
		{
			sessMan.saveProject();
		}
		return childDone;
	}
}
