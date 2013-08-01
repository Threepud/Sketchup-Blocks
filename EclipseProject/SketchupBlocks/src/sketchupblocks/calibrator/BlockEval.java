package sketchupblocks.calibrator;

import sketchupblocks.math.Vec3;

class BlockEval implements Evaluator
{
	Vec3 [] vecs;
	double [] angles;
	
	public BlockEval(Vec3 [] _vecs, double [] _angles)
	{
	vecs = _vecs ;
	angles = _angles;
	}

	public EvalResults [] evaluate(Particle [] p)
	{
		EvalResults [] result = new EvalResults[p.length];
		for(int k = 0 ; k < p.length ; k++)
			result[k] = evaluate(p[k]);
			
		return result;
	}
	
	public EvalResults evaluate(Particle  p)
	{
		EvalResults result = evaluate(p.attributes);
		p.currentScore(result.score);
		return result;
	}
	
	
	public EvalResults [] evaluate(double [] [] p)
	{
		EvalResults [] result = new EvalResults[p.length];
		for(int k = 0 ; k < p.length ; k++)
			result[k] = evaluate(p[k]);
		return result;		
	}
	
	public EvalResults evaluate(double [] pa)
	{
		EvalResults result = new EvalResults();
		result.score = 10000;
		
		// 4 sides
		double error = 0; 
		
		for(int k = 0 ; k < vecs.length ; k++)
			{
            double temp = dot(pa,vecs[k]) - (1.0*(vecs[k].length())*Math.cos(Math.toRadians(angles[k]))); // we want a length of 1
			temp*=temp;
			error += temp;
			}
		
		result.score /= (error+Math.abs(length(pa)-1)*1000);
		return result;
	}
	
	double dot(Vec3 one, Vec3 two)
	{
	return one.x*two.x + one.y*two.y + one.z*two.z;
	}
	
	double dot(double [] one , Vec3 two)
	{
	return one[0]*two.x + one[1]*two.y + one[2]*two.z;
	}
	
	private double length(double [] arr)
	{
		double c = 0;
		for(int k = 0 ; k < arr.length ; k++)
		{
			c += arr[k]*arr[k];
		}
		return Math.sqrt(c);
			
	}
}