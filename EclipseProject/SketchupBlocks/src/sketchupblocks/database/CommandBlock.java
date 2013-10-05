package sketchupblocks.database;


/**
 * 
 * 
 * @author cravingoxygen
 *
 */
public class CommandBlock extends Block
{
	private static final long serialVersionUID = 1L;

	public enum CommandType
    {
		EXPORT("EXPORT"),
    	ROTATE("ROTATE"),
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