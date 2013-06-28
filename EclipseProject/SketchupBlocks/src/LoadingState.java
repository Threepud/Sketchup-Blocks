
public class LoadingState extends MenuState
{

	protected SlotChooserState childState;
	
	public LoadingState(SessionManager _sessMan)
	{
		super(_sessMan);
		childState = new SlotChooserState(_sessMan);
	}
	
	public boolean handleInput(CommandBlock cBlock)
	{
		boolean childDone = childState.handleInput(cBlock);
		if (childDone)
		{
			sessMan.loadProject();
		}
		return childDone;
	}
}
