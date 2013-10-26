package sketchupblocks.calibrator;

/**
 * The settings for the PSO System
 * @author Hein
 *
 */
public class ParticleSystemSettings
{
	public Evaluator eval;
	public Evaluator tester;
	public ParticleCreator creator;
	
	public int particleCount;
	public int iterationCount;
	
	public boolean ringTopology;
	public int ringSize;
	
	public double socialStart;
	public double cognitiveStart;
	public double momentum;
	public double MaxComponentVelocity;
}