package sketchupblocks.calibrator;
import java.util.Random;
import java.io.Serializable;

public class Particle implements Serializable 
{
	public double [] attributes;
	
	public double scoreNow;
	public double bestScore;
	public double [] bestPosition;
	

	
	public double socialComponent;
	public double cognitiveComponent;
	public double momentumComponent;
	public double ComponentMax;
	
	private double [] lastDirection;
	
	private int dimesion;
	
	Random rand;
	
	public Particle(double sC,double cC,double mC,double Cmax,int pDim)
	{
		socialComponent = sC;
		cognitiveComponent = cC;
		momentumComponent = mC;
		ComponentMax = Cmax;
		dimesion = pDim;
		rand = new Random();
		bestScore = -Double.MAX_VALUE;
		
		lastDirection = new double[pDim];
		attributes = new double[pDim];
			for(int k = 0 ; k < pDim ; k++)
				lastDirection[k] = 0.0;
	}
	
	public void updatePosition(double [] globalBestPosition)
	{
		if(bestPosition == null) // first call
			{
			bestPosition = cloneDA(attributes);
			}
			
		double [] socialDirection = scaleRandomDA(socialComponent,0.5,1,subtractDA(globalBestPosition,attributes));
		double [] cognitiveDirection = scaleRandomDA(cognitiveComponent,0.5,1,subtractDA(bestPosition,attributes));
		double [] momentiveDirection = scaleDA(momentumComponent,lastDirection);
		
		double [] currentDirection = addDA(socialDirection,cognitiveDirection,momentiveDirection);
	
		clipDA(ComponentMax,currentDirection);
		
		attributes = addDA(attributes,currentDirection);
		
		lastDirection = currentDirection;
	}

	public void currentScore(double score)
	{
		scoreNow = score;
		if(score >  bestScore)
			{
				bestScore = score;
				bestPosition = cloneDA(attributes);
			}
	}
	
	public static double [] subtractDA(double [] left, double [] right)
	{
		if(left == null || right == null) 
			{
				throw new RuntimeException("array null "+left+ "||" +right);
			}
		if(left.length != right.length) throw new RuntimeException("Cannot subtract arrays of unequal length "+left.length+ "!=" +right.length);
		
		double  [] result = new double[left.length];
		for(int k = 0 ; k < result.length ; k++)
			result[k] = left[k] - right[k];
			
		return result;	
	}
	
	public static double [] addDA(double [] left, double [] right)
	{
		if(left.length != right.length) throw new RuntimeException("Cannot subtract arrays of unequal length");
		double  [] result = new double[left.length];
		for(int k = 0 ; k < result.length ; k++)
			result[k] = left[k] + right[k];	
		return result;	
	}

	public static double [] addDA(double [] left, double [] middel, double [] right)
	{
		if(left.length != right.length) throw new RuntimeException("Cannot subtract arrays of unequal length");
		double  [] result = new double[left.length];
		for(int k = 0 ; k < result.length ; k++)
			result[k] = left[k] + middel[k] + right[k];	
		return result;	
	}	
	
	public static double [] cloneDA(double [] array)
	{
		double  [] result = new double[array.length];
		for(int k = 0 ; k < result.length ; k++)
			result[k] = array[k];		
		return result;	
	}
	
	public static double [] scaleDA(double scalar,double [] array)
	{
		for(int k = 0 ; k < array.length ; k++)
			array[k]*= scalar;		
		return array;
	}
	
	public static double [] scaleRandomDA(double scalar,double lower,double upper,double [] array)
	{
		Random rand = new Random();
			for(int k = 0 ; k < array.length ; k++)
				array[k]*= (scalar * (rand.nextDouble()*(upper-lower)+lower));		
		return array;
	}
	
	static double sizeofDA(double [] array)
	{
		double result= 0;
		for(int k = 0 ; k < array.length ; k++)
			result += array[k]*array[k];
		return Math.sqrt(result);
	}
	
	public static void clipDA(double max,double [] array)
		{			
			double size = sizeofDA(array);
			if(size == 0 ) return;
			
			if(size >max || size < -max)
			{
				if(size >max)
					array = scaleDA(max/size,array);
					
				if(size < -max) 
					array = scaleDA((-max)/size,array);
						
				size = sizeofDA(array);
				
				if(!( size - max > 0.0001 || size + max > 0.0001))
					System.out.println("Clipping error:"+ size);
			}
		}
	


}
