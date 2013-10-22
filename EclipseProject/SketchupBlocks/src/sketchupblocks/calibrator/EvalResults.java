package sketchupblocks.calibrator;

/**
 * Results of an Particle evaluation
 * @author Neoin
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