package sketchupblocks.math;

public class SingularMatrixException extends Exception
{
	public SingularMatrixException()
	{
		super("Singular matrix found");
	}
}
