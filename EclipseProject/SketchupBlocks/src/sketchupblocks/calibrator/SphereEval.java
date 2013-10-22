package sketchupblocks.calibrator;

import sketchupblocks.math.Vec3;

/**
 * Evaluation of particles for the intersection of spheres
 * @author Hein
 *
 */
public class SphereEval implements Evaluator
{

	double a ;
	double b ;
	double c ;
	double d;
	Vec3 l1;
	Vec3 l2;
	Vec3 l3;
	Vec3 l4;
	
	public SphereEval(double _a , double _b , double _c , double _d  , Vec3 _l1, Vec3 _l2, Vec3 _l3, Vec3 _l4)
	{
		a = _a ;
		b = _b;
		c = _c;
		d = _d;
		l1 = _l1;
		l2 = _l2;
		l3 = _l3;
		l4 = _l4;
	
	if(l1 == null || l2 == null || l3 == null|| l3 == null)
		throw new NullPointerException();
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

        if(pa[2] < 0 )
			result.score = -100;
            else
                result.score = 100;
		if(l1 == null || l2 == null || l3 == null || l4 == null)
			throw new NullPointerException();
		
		double error1 = (a*a)-(Math.pow(l1.x-pa[0],2) + Math.pow(l1.y-pa[1],2) +Math.pow(l1.z-pa[2],2));
		error1 *= error1;
		double error2 = (b*b)-(Math.pow(l2.x-pa[0],2) + Math.pow(l2.y-pa[1],2) +Math.pow(l2.z-pa[2],2));
		error2 *= error2;
		double error3 = (c*c)-(Math.pow(l3.x-pa[0],2) + Math.pow(l3.y-pa[1],2) +Math.pow(l3.z-pa[2],2));
		error3 *= error3;
		double error4 = (d*d)-(Math.pow(l4.x-pa[0],2) + Math.pow(l4.y-pa[1],2) +Math.pow(l4.z-pa[2],2));
		error4 *= error4;		
	
		
		result.score /= (error1+error2+error3+error4);
		return result;
	}
}