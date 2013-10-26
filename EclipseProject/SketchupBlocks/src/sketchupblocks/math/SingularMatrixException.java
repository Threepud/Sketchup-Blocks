package sketchupblocks.math;

public class SingularMatrixException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SingularMatrixException()
	{
		super("Singular matrix found");
	}
}
