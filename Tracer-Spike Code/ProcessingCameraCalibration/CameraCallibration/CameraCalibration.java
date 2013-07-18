import java.util.Date;
public class CameraCalibration
{

	public static void main(String [] args)
	{
		mypoint l1 = new mypoint(-1,1,0);
		mypoint l2 = new mypoint(1,1,0);
		mypoint l3 = new mypoint(1,-1,0);
		mypoint l4 = new mypoint(-1,-1,0);

		double [] angles = new double[6];
			
		angles[0] = 0;
		angles[1] = 1;
		angles[2] = 2;
		angles[3] = 3;
		angles[4] = 4;
		angles[5] = 5;
		
		mypoint res = getCameraPosition(l1,l2,l3,l4,angles);
		System.out.println(res.x+":"+res.y+":"+res.z);
	}
	
	static mypoint  getCameraPosition(mypoint l1 , mypoint l2, mypoint l3, mypoint l4,double [] angles)
	{
		ParticleSystemSettings settings = new ParticleSystemSettings();
		double [] lengths = new double[6];
		
		lengths[0] = l1.distance(l2);
		lengths[1] = l2.distance(l3);
		lengths[2] = l3.distance(l4);
		lengths[3] = l4.distance(l1);
		lengths[4] = l1.distance(l3);
		lengths[5] = l2.distance(l4);
		
		settings.eval = new TriangleEval(lengths,angles);
		settings.tester = null;
		settings.creator = new ParticleCreator(4,0,100);
		
		settings.particleCount = 100;
		settings.iterationCount= 2000;
		
		settings.ringTopology = true;
		settings.ringSize = 1;
		
		settings.socialStart = 0.72;
		settings.cognitiveStart = 0.72;
		settings.momentum = 1.4;
		settings.MaxComponentVelocity = 1;
		
		ParticleSystem system = new ParticleSystem(settings);
		
		Particle bestabc = null;
		
		bestabc = system.go(); // a b c d 
			
		settings.eval = new SphereEval(bestabc.bestPosition[0],bestabc.bestPosition[1],bestabc.bestPosition[2],bestabc.bestPosition[3],l1,l2,l3,l4);
		settings.tester = null;
		settings.creator = new ParticleCreator(3,-100,100);
		settings.particleCount = 20;
		settings.iterationCount= 2000;
		settings.socialStart = 0.72;
		settings.cognitiveStart = 0.72;
		settings.momentum = 1.4;
		settings.MaxComponentVelocity = 1;		


		system = new ParticleSystem(settings);
                
		Particle best = null;
		
		best = system.go(); // x y z
		
		mypoint result = new mypoint(best.bestPosition[0],best.bestPosition[1],best.bestPosition[2]);
		return result;
	}


	static mypoint getBlockVector(mypoint [] vecs, double [] angles)
	{
	   /* for(int k = 0 ; k < 4 ; k++)
		  {
		   System.out.println(vecs[k].x+" , "+vecs[k].y+" , "+vecs[k].z); 
		  }
		  
		 for(int k = 0 ; k < 4 ; k++)
		  {
		   System.out.println(angles[k]); 
		  }   */              
  
		ParticleSystemSettings settings = new ParticleSystemSettings();
		
		settings.eval = new BlockEval(vecs,angles);
		settings.tester = null;
		settings.creator = new ParticleCreator(3,-1,1);
		
		settings.particleCount = 50;
		settings.iterationCount= 10000;
		
		settings.ringTopology = true;
		settings.ringSize = 1;
		
		settings.socialStart = 0.72;
		settings.cognitiveStart = 0.72;
		settings.momentum = 1.4;
		settings.MaxComponentVelocity = 0.01;
		
		ParticleSystem system = new ParticleSystem(settings);
		
		Particle bestabc = null;
      
		bestabc = system.go(); // x y z

		mypoint result = new mypoint(bestabc.bestPosition[0],bestabc.bestPosition[1],bestabc.bestPosition[2]);
		double resLength = Math.sqrt(result.x*result.x+result.y*result.y+result.z*result.z);
		result.x /= resLength;
		result.y /= resLength;
		result.z /= resLength;
		return result;
	}
}
