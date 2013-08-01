package sketchupblocks.calibrator;
import java.util.Date;

public class ParticleSystem
{
	private ParticleSystemSettings settings;
	
	public ParticleSystem(ParticleSystemSettings sett)
	{
		settings = sett;
	}
	
	public Particle go()
	{
		Particle [] particles = new Particle[settings.particleCount];
		
		for(int k = 0 ; k  < particles.length ; k++)
			particles[k] = settings.creator.getParticle(settings.socialStart,settings.cognitiveStart,settings.momentum,settings.MaxComponentVelocity);
		EvalResults [] scores;
		
		double [] bestScore = new double[particles.length] ; //Store the best for every neighborhood
			for(int k = 0 ; k < bestScore.length ; k++)
				bestScore[k] = -Double.MAX_VALUE;		
		double [][] globalBestPosition = new double[particles.length][];
			
			for(int  iteration = 0 ; iteration < settings.iterationCount ; iteration++)
			{
				scores = settings.eval.evaluate(particles); // Will evaluate the particles, and update best scores

					if(settings.ringTopology)
					{ // Ring
					//get the best local particle	
						for(int k = 0 ; k < bestScore.length ; k++)
							bestScore[k] = -Double.MAX_VALUE;
					
						for(int k = 0 ; k < particles.length; k++) // for every particle find its local best
							for(int l = -settings.ringSize;l <= settings.ringSize ; l++)
							{
								int index = ((k+l) < 0) ? (particles.length - (k+l)) % particles.length : (k+l) % particles.length	;
								
								if(particles[index].bestScore > bestScore[k])
									{
										bestScore[k] =particles[index].bestScore;
										globalBestPosition[k] = Particle.cloneDA(particles[index].bestPosition);
									}
									
							}
					}
					else
					{ // Star
						double [] bestNow = Particle.cloneDA(particles[0].bestPosition);
						double bestScoreNow  = bestScoreNow = particles[0].bestScore;
						
						for(int k = 0 ; k < particles.length; k++) // for every particle find its local best
							if(particles[k].bestScore > bestScoreNow)
									{
										bestScoreNow =particles[k].bestScore;
										bestNow = Particle.cloneDA(particles[k].bestPosition);
									}
						
						for(int k = 0 ; k < particles.length; k++) // for every particle find its local best
							globalBestPosition[k] = bestNow;
						bestScore[0] = bestScoreNow;
					}
				
				int bestIndex = -1;
				double bestScoreSearch = -Double.MAX_VALUE;
				for(int k = 0 ; k < particles.length; k++)
					if(scores[k].score >= bestScoreSearch)
					{
						bestScoreSearch = scores[k].score;
						bestIndex = k;
					}
				
				
				//update positions
				for(int p = 0 ; p < particles.length; p++)
					particles[p].updatePosition(globalBestPosition[p]);
				
			}
	
		//Return best remembered
	    Particle result = particles[0];
	    double score =particles[0].bestScore;
	    
	    for(int k = 0 ; k < particles.length; k++)
		    if(particles[k].bestScore > score)
			    {
				    score = particles[k].bestScore;
				    result = particles[k];
			    }
	
	return result;
	}
	
	private double highest(double [] arr)
	{
		double h = arr[0];
		for(int k = 0 ; k < arr.length ; k++)
			h = h > arr[k] ? h : arr[k] ;
		return h;
	}

	private double highest(EvalResults [] arr)
	{
		double h = arr[0].score;
		for(int k = 0 ; k < arr.length ; k++)
			h = h > arr[k].score ? h : arr[k].score ;
		return h;
	}
}