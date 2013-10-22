package sketchupblocks.calibrator;

/**
 * Results of an Particle evaluation. The class returned by en Evaluator
 * @author Hein
 */
public class EvalResults
{
	public double score;
	
	public EvalResults()
	{
		score =0;
	}
	
	public void setScore(double newScore)
	{
		score = newScore;
	}
	
	public double getScore()
	{
		return score;
	}
}