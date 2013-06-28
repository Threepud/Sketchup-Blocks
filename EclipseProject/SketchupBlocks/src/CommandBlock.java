class CommandBlock extends Block
{
    public COMMAND_TYPE type;
    public enum COMMAND_TYPE
    {
    	LOAD, CREATE, SAVE, EXPORT, ROTATE, OK, CANCEL
    }
}