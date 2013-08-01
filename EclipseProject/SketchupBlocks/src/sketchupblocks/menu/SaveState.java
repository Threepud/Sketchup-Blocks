package sketchupblocks.menu;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.CommandBlock;
import sketchupblocks.base.SessionManager;
import sketchupblocks.base.Settings;


public class SaveState extends MenuState
{
	protected SlotChooserState childState;
	public SaveState(SessionManager _sessMan)
	{
		super(_sessMan);
		childState = new SlotChooserState(_sessMan);
		System.out.println("Please select a slot to save the project into");
	}
	
	public boolean handleInput(CommandBlock cBlock, CameraEvent cEvent)
	{
		boolean childDone = childState.handleInput(cBlock, cEvent);
		if (childDone && childState.slotNumber != -1)
		{
			sessMan.saveProject(childState.slotNumber);
			if (Settings.verbose >= 1)
				System.out.println("Saving of project was confirmed to slot: "+childState.slotNumber);
		}
		
		if (childState.slotNumber == -1 && Settings.verbose >= 1)
		{
			System.out.println("Saving of project was cancelled");
		}
		
		return childDone;
	}
}
