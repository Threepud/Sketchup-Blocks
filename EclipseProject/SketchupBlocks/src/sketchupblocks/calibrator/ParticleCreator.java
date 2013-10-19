package sketchupblocks.calibrator;
import java.util.Random;
import java.io.*;

public class ParticleCreator
{
	private double  lower;
	private double  upper;
	private int dimension;
	private Random rand;
	
	public ParticleCreator(int dim,double low, double  up)
		{
			dimension= dim;
			lower = low;
			upper = up;
			rand = new Random(42);
		}

	public Particle getParticle(double sC,double cC,double mC,double Cmax)
	{
		Particle result = new Particle(sC,cC,mC,Cmax,dimension);
		for(int k = 0 ; k < dimension ; k++)
			result.attributes[k] = rand.nextDouble()*(upper-lower)+lower;
			
		result.bestPosition = result.attributes;
		return result;
	}

}

class ParticleCreatorLoader extends ParticleCreator
{
	private String filename;
	private int particleIndex;
	private Particle [] LoadedParticles;
		
	 public ParticleCreatorLoader(String FolderName,String iteration)
	 {
		super(0,0,0);
		try
		{
			filename = FolderName+"/"+iteration;
			particleIndex = 0;
			FileInputStream  input= new FileInputStream(filename);
			ObjectInputStream reader= new ObjectInputStream(input);
			int numToRead = reader.readInt();
			LoadedParticles = new Particle[numToRead];
			for(int k = 0 ; k < numToRead ; k++)
			{
				reader= new ObjectInputStream(input);
				LoadedParticles[k] = (Particle)reader.readObject();
			}
			
			reader.close();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	 
	public Particle getParticle(double sC,double cC,double mC,double Cmax)
	{		
		return LoadedParticles[particleIndex++];
	}
	
	public double [] getBestParticle()
	{
		double bestScore = LoadedParticles[0].scoreNow;
		double [] returnValue  = LoadedParticles[0].attributes;
		for(int k = 0 ; k < LoadedParticles.length ; k++)
		{
			if(LoadedParticles[k].scoreNow > bestScore)
			{ 
				bestScore = LoadedParticles[0].scoreNow;
				returnValue  = LoadedParticles[k].attributes;
			}
		}
		return returnValue;
	}
	
	

}