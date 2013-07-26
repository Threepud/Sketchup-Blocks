
public class LoadingState extends MenuState
{

	protected SlotChooserState childState;
	
	public LoadingState(SessionManager _sessMan)
	{
		super(_sessMan);
		childState = new SlotChooserState(_sessMan);
		System.out.println("Please select a slot to load a project from");
	}
	
	public boolean handleInput(CommandBlock cBlock, CameraEvent cEvent)
	{
		if (Settings.verbose >= 3)
			System.out.println("--Load state is handling input--");
		boolean childDone = childState.handleInput(cBlock, cEvent);
		if (childDone && childState.slotNumber != -1) 
		{
			sessMan.loadProject(childState.slotNumber);
			if (Settings.verbose >= 1)
				System.out.println("Loading of project was confirmed to slot: "+childState.slotNumber);
		}
		
		if (childState.slotNumber == -1 && Settings.verbose >= 1)
		{
			System.out.println("Loading of project was cancelled");
		}
		
		return childDone;
	}
}
