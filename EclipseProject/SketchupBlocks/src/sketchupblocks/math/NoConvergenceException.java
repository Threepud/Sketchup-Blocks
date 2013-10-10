package sketchupblocks.math;

public class NoConvergenceException extends Exception
{
	private static final long serialVersionUID = 1L;

	public NoConvergenceException()
	{
		super("No convergence occurred");
	}
}
