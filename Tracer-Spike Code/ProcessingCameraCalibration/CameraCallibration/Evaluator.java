import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import java.net.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

class EvalResults
{
	EvalResults()
	{
	score =0;
	}
	public double score;
}

class mypoint
{
	public mypoint(double _x,double _y,double _z)
	{
		x = _x;
		y = _y;
		z = _z;
	}
	

	
	public double distance(mypoint other)
	{
		return Math.sqrt(Math.pow(x-other.x,2)+Math.pow(y-other.y,2)+Math.pow(z-other.z,2));
	}
	
	double x;
	double y;
	double z;
}

public interface Evaluator
	{
		public EvalResults [] evaluate(Particle [] p);
		public EvalResults evaluate(Particle  p);
		
		public EvalResults [] evaluate(double [] [] p);
		public EvalResults evaluate(double [] pa);
	}
	
class TriangleEval implements Evaluator
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
			double bad1 = (lengths[0]*lengths[0]) - (pa[0]*pa[0] + pa[1]*pa[1] - 2*pa[0]*pa[1]*Math.cos(Math.toRadians(angles[0])));
			bad1 *= bad1;
			double bad2 = (lengths[1]*lengths[1]) - (pa[1]*pa[1] + pa[2]*pa[2] - 2*pa[1]*pa[2]*Math.cos(Math.toRadians(angles[1])));
			bad2 *= bad2;		
			double bad3 = (lengths[2]*lengths[2]) - (pa[2]*pa[2] + pa[3]*pa[3] - 2*pa[2]*pa[3]*Math.cos(Math.toRadians(angles[2])));
			bad3 *= bad3;
			double bad4 = (lengths[3]*lengths[3]) - (pa[3]*pa[3] + pa[0]*pa[0] - 2*pa[3]*pa[0]*Math.cos(Math.toRadians(angles[3])));
			bad4 *= bad4;
			//diagonals
			double bad5 = (lengths[4]*lengths[4]) - (pa[0]*pa[0] + pa[2]*pa[2] - 2*pa[0]*pa[2]*Math.cos(Math.toRadians(angles[4])));
			bad5 *= bad5;
			double bad6 = (lengths[5]*lengths[5]) - (pa[1]*pa[1] + pa[3]*pa[3] - 2*pa[1]*pa[3]*Math.cos(Math.toRadians(angles[5])));
			bad6 *= bad6;
			
			result.score /= (bad1+bad2+bad3+bad4+bad5+bad6);
			return result;
		}
}

class SphereEval implements Evaluator
{

		double a ;
		double b ;
		double c ;
		double d;
		mypoint l1;
		mypoint l2;
		mypoint l3;
		mypoint l4;
		
		public SphereEval(double _a , double _b , double _c , double _d  , mypoint _l1, mypoint _l2, mypoint _l3, mypoint _l4)
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
			System.out.println("NULL");
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
				System.out.println("NULL");
			
			double bad1 = (a*a)-(Math.pow(l1.x-pa[0],2) + Math.pow(l1.y-pa[1],2) +Math.pow(l1.z-pa[2],2));
			bad1 *= bad1;
			double bad2 = (b*b)-(Math.pow(l2.x-pa[0],2) + Math.pow(l2.y-pa[1],2) +Math.pow(l2.z-pa[2],2));
			bad2 *= bad2;
			double bad3 = (c*c)-(Math.pow(l3.x-pa[0],2) + Math.pow(l3.y-pa[1],2) +Math.pow(l3.z-pa[2],2));
			bad3 *= bad3;
			double bad4 = (d*d)-(Math.pow(l4.x-pa[0],2) + Math.pow(l4.y-pa[1],2) +Math.pow(l4.z-pa[2],2));
			bad4 *= bad4;		
		
			
			result.score /= (bad1+bad2+bad3+bad4);
			return result;
		}
}

class BlockEval implements Evaluator
{
		mypoint [] vecs;
		double [] angles;
		
		public BlockEval(mypoint [] _vecs, double [] _angles)
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
			double bad = 0; 
			
			for(int k = 0 ; k < vecs.length ; k++)
				{
				//double temp = dot(pa,vecs[k]) - (length(pa)*length(vecs[k])*Math.cos(angles[k]));
                                double temp = dot(pa,vecs[k]) - (1.0*length(vecs[k])*Math.cos(Math.toRadians(angles[k]))); // we want a length of 1
				temp*=temp;
				bad += temp;
				}
			
			result.score /= (bad+Math.abs(length(pa)-1)*1000);
			return result;
		}
		
		double dot(mypoint one, mypoint two)
		{
		return one.x*two.x + one.y*two.y + one.z*two.z;
		}
		
		double dot(double [] one , mypoint two)
		{
		return one[0]*two.x + one[1]*two.y + one[2]*two.z;
		}
		
		double length(double [] arr)
		{
			double res = 0;
			for(int k = 0 ; k < arr.length ; k++)
				res+= arr[k]*arr[k];
				
			return Math.sqrt(res);
		}
		
		double length(mypoint arr)
		{
				return Math.sqrt(arr.x*arr.x + arr.y*arr.y + arr.z*arr.z);
		}
		
}

