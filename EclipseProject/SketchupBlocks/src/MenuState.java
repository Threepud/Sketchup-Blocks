
public abstract class MenuState 
{
	protected SessionManager sessMan;
	
	abstract boolean handleInput(CommandBlock cBlock);
	
	public MenuState(SessionManager _sessMan)
	{
		sessMan = _sessMan;
	}
}