package sketchupblocks.exception;
public class UnknownBlockTypeException extends Exception 
{
	private static final long serialVersionUID = 8341776867784991342L;

	public UnknownBlockTypeException(String message)
	{
		super(message);
	}
}
