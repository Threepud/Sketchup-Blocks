class CommandBlock extends Block
{
    public CommandType type;
    public enum CommandType
    {
    	LOAD, NEW, SAVE, EXPORT, ROTATE, OK, CANCEL,SPECTATE
    }
}