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

class SphereEval implements Evaluator
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

class BlockPosition implements Evaluator
{
	double length;
	Line line1;
	Line line2;
		
		public BlockPosition(double _length, Line _line1, Line _line2)
		{
		length = _length;
		line1  = _line1;
		line2  = _line2;
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
			
			double distance = 0;
			distance+= Math.pow( (line1.point.x + pa[0]*line1.direction.x) - (line2.point.x + pa[1]*line2.direction.x), 2);
			
			distance+= Math.pow( (line1.point.y + pa[0]*line1.direction.y) - (line2.point.y + pa[1]*line2.direction.y), 2);
			
			distance+= Math.pow( (line1.point.z + pa[0]*line1.direction.z) - (line2.point.z + pa[1]*line2.direction.z), 2);
			
			result.score /= Math.abs(length*length - distance);
			
			return result;
		}
}

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

