package sketchupblocks.menu;

import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.CommandBlock;
import sketchupblocks.base.SessionManager;

public abstract class MenuState 
{
	protected SessionManager sessMan;
	
	abstract boolean handleInput(CommandBlock cBlock, CameraEvent cEvent);
	
	public MenuState(SessionManager _sessMan)
	{
		sessMan = _sessMan;
	}
}
