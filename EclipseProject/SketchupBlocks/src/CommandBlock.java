class CommandBlock extends Block
{
<<<<<<< HEAD
    public COMMAND_TYPE type;
    public enum COMMAND_TYPE
    {
    	LOAD, CREATE, SAVE, EXPORT, ROTATE, OK, CANCEL
    }
=======
	public enum CommandType
	{
		NEW,
		SAVE,
		LOAD,
		EXPORT,
		SPECTATE
	}
	
    public CommandType type;
>>>>>>> 95fcb2d6f35b63578a680b61d7b1f96790fefe4d
}