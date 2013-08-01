package sketchupblocks.calibrator;

import sketchupblocks.math.Line;

class BlockPosition implements Evaluator
{
	Vec3 [] positions;
	Line [] lines;
		
	public BlockPosition(Vec3 [] pos,Line [] lin )
	{
		positions = pos;
		lines = lin;
		if(positions.length != lines.length)
			throw new RuntimeException("Lengths do not match");
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
		if(positions.length != pa.length)
			throw new RuntimeException("Lengths do not match");
		
		int arrayLengths = pa.length;
		
		EvalResults result = new EvalResults();
		result.score = 10000;
		
		for(int k = 0 ; k < arrayLengths ; k++ )
			if(pa[k] < 0)
				result.score = 0;
		
		double errors = 0 ;
		
		//On lines
		for(int k = 0 ; k < arrayLengths-1 ; k++ )
			for(int l = k+1 ; l < arrayLengths ; l++ )
				{
					errors += error(positions[k],positions[l],lines[k],lines[l],pa[k],pa[l]);;
				}
		
		//Closest to camera
		for(int k = 0 ; k < arrayLengths ; k++ )
			errors += pa[k]*pa[k];
		
		
		result.score /= errors;
		
		return result;
	}
	
	public double error(Vec3 position1, Vec3 position2, Line lin1, Line lin2,double s1, double s2)
	{
		double distance = 0;
		
		distance+= Math.pow( (lin1.point.x + s1*lin1.direction.x) - (lin2.point.x + s2*lin2.direction.x), 2);
		distance+= Math.pow( (lin1.point.y + s1*lin1.direction.y) - (lin2.point.y + s2*lin2.direction.y), 2);
		distance+= Math.pow( (lin1.point.z + s1*lin1.direction.z) - (lin2.point.z + s2*lin2.direction.z), 2);	
	
		double length = position1.distance(position2);
		
		return Math.abs(length*length - distance);
	}
}