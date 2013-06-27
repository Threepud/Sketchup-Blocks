class CommandBlock extends Block
{
	public enum CommandType
	{
		NEW,
		SAVE,
		LOAD,
		EXPORT,
		SPECTATE
	}
	
    public CommandType type;
}