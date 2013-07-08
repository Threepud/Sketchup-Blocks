
public class Menu 
{
	MenuState state;
	SessionManager sessMan;
	
	Menu(SessionManager _sman)
	{
		sessMan = _sman;
	}
	
	void handleInput(CommandBlock cBlock, CameraEvent cEvent)
	{
		if (state == null)
		{
			//Create correct state
			
		}
		else
		{
			state.handleInput(cBlock, cEvent);
		}
	}
	
}
