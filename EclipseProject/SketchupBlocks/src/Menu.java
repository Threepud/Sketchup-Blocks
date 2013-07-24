
public class Menu 
{
	private MenuState state;
	private SessionManager sessMan;
	
	public Menu(SessionManager _sessMan)
	{
		sessMan = _sessMan;
	}
	
	public void handleInput(CommandBlock cBlock, CameraEvent cEvent)
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
	
	public void drawMenuOverlay()
	{
		
	}
}
