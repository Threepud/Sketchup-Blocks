package sketchupblocks.calibrator;
import java.util.Random;
import java.io.*;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ParticleCreator
{
	private double  lower;
	private double  upper;
	private int dimension;
	private Random rand;
	private Date createdAt;
	
	public ParticleCreator(int dim,double low, double  up)
		{
			dimension= dim;
			lower = low;
			upper = up;
			rand = new Random();
			createdAt = new Date();
		}

	public Particle getParticle(double sC,double cC,double mC,double Cmax)
	{
		Particle result = new Particle(sC,cC,mC,Cmax,dimension);
		for(int k = 0 ; k < dimension ; k++)
			result.attributes[k] = rand.nextDouble()*(upper-lower)+lower;
			
		result.bestPosition = result.attributes;
		return result;
	}
	
	ParticleSaver getParticleSaver()
	{
		return new ParticleSaver();
	}
	
	private class ParticleSaver
	{
		private File directory;
		private ParticleSaver()
		{
			GregorianCalendar cal = new GregorianCalendar();
			String directoryName = cal.get(1)+"_" + cal.get(2) +"_"+ cal.get(3) +"_"+ cal.get(4) +"_"+cal.get(5)+"_"+cal.get(6)+"_"+cal.get(12)+"_"+cal.get(13)  ;
			directory = new File(directoryName);
			directory.mkdir();
				
		}
		
		void saveParticles(Particle [] par,int iteration)
		{
		try
		{
			String fileName = directory.getPath()+"/"+iteration;
			System.out.println(fileName);
			File file = new File(fileName);
			file.createNewFile();
			FileOutputStream fs = new FileOutputStream(file);
			
			ObjectOutputStream oo = new ObjectOutputStream(fs);
			oo.writeInt(par.length);
			oo.flush();
			for(int k = 0 ; k < par.length ; k++)
				{
				oo= new ObjectOutputStream(fs);
				oo.writeObject(par[k]);
				oo.flush();
				}
			fs.flush();
			fs.close();
		}
		catch(Exception e)
			{
			e.printStackTrace();
			}
		}
	
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
			if(LoadedParticles[k].scoreNow > bestScore)
			{ 
				bestScore = LoadedParticles[0].scoreNow;
				returnValue  = LoadedParticles[k].attributes;
			}
		return returnValue;
	}
	
	

}