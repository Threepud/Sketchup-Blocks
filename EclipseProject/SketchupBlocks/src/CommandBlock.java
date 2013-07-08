class CommandBlock extends Block
{
    public enum CommandType
    {
    	LOAD("LOAD"),
    	NEW("NEW"),
    	SAVE("SAVE"),
    	EXPORT("EXPORT"),
    	ROTATE("ROTATE"),
    	OK("OK"),
    	CANCEL("CANCEL"),
    	SPECTATE("SPECTATE");
    	
    	private final String name;
    	
    	private CommandType(String _name)
    	{
    		name = _name;
    	}
    	
    	public String toString()
    	{
    		return name;
    	}
    }
    
    public CommandType type;
}