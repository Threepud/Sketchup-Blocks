package sketchupblocks.calibrator;

public class TriangleEval implements Evaluator
{
	double [] lengths;
	double [] angles;
	
	public TriangleEval(double [] _lengths, double [] _angles)
	{
		lengths = _lengths;
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
		

		if(pa[0] < 0 || pa[1] < 0 || pa[2] < 0 || pa[3] < 0)
		  result.score = -1000;
		  else
		  result.score = 1000;
		
		// 4 sides
		double error1 = (lengths[0]*lengths[0]) - (pa[0]*pa[0] + pa[1]*pa[1] - 2*pa[0]*pa[1]*Math.cos(Math.toRadians(angles[0])));
		error1 *= error1;
		double error2 = (lengths[1]*lengths[1]) - (pa[1]*pa[1] + pa[2]*pa[2] - 2*pa[1]*pa[2]*Math.cos(Math.toRadians(angles[1])));
		error2 *= error2;		
		double error3 = (lengths[2]*lengths[2]) - (pa[2]*pa[2] + pa[3]*pa[3] - 2*pa[2]*pa[3]*Math.cos(Math.toRadians(angles[2])));
		error3 *= error3;
		double error4 = (lengths[3]*lengths[3]) - (pa[3]*pa[3] + pa[0]*pa[0] - 2*pa[3]*pa[0]*Math.cos(Math.toRadians(angles[3])));
		error4 *= error4;
		//diagonals
		double error5 = (lengths[4]*lengths[4]) - (pa[0]*pa[0] + pa[2]*pa[2] - 2*pa[0]*pa[2]*Math.cos(Math.toRadians(angles[4])));
		error5 *= error5;
		double error6 = (lengths[5]*lengths[5]) - (pa[1]*pa[1] + pa[3]*pa[3] - 2*pa[1]*pa[3]*Math.cos(Math.toRadians(angles[5])));
		error6 *= error6;
		
		result.score /= (error1+error2+error3+error4+error5+error6);
		return result;
	}
}
