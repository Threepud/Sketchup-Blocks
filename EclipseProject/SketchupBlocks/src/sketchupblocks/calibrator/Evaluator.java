package sketchupblocks.calibrator;

public interface Evaluator
{
	public EvalResults [] evaluate(Particle [] p);
	public EvalResults evaluate(Particle  p);
	
	public EvalResults [] evaluate(double [] [] p);
	public EvalResults evaluate(double [] pa);
}

