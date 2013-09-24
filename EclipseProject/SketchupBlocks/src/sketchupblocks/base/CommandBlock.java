package sketchupblocks.base;

import sketchupblocks.database.Block;

public class CommandBlock extends Block
{
	private static final long serialVersionUID = 1L;

	public enum CommandType
    {
    	LOAD("LOAD"),
    	NEW("NEW"),
    	SAVE("SAVE"),
    	EXPORT("EXPORT"),
    	ROTATE("ROTATE"),
    	OK("OK"),
    	CANCEL("CANCEL"),
    	SPECTATE("SPECTATE"),
    	CALIBRATE("CALIBRATE");
    	
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