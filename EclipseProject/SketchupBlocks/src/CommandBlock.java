class CommandBlock extends Block
{
    public enum CommandType
    {
    	LOAD, 
    	NEW, 
    	SAVE, 
    	EXPORT, 
    	ROTATE, 
    	OK, 
    	CANCEL,
    	SPECTATE
    }
    
    public CommandType type;
}