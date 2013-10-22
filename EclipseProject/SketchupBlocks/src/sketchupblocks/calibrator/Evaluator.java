package sketchupblocks.calibrator;

/**
 * An interface for evaluating a particle
 * @author Hein
 */
public interface Evaluator
{
	/**
	 * Evaluation of a set of particles
	 */
	public EvalResults [] evaluate(Particle [] p);
	/**
	 * Evaluation of a single particle
	 */
	public EvalResults evaluate(Particle  p);
	
	/**
	 * Evaluation of a the contents of a set of particles
	 */
	public EvalResults [] evaluate(double [] [] p);
	
	/**
	 * The evaluation of a single particle's contents
	 */
	public EvalResults evaluate(double [] pa);
}

