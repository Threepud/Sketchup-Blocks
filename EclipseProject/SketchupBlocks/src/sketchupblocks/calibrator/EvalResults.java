package sketchupblocks.calibrator;

public class EvalResults
{
	public double score;
	
	EvalResults()
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