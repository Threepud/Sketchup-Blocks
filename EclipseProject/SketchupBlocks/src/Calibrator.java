
public class Calibrator 
{
	boolean calibrated[] = new boolean[Settings.numCameras];
	
	double [][][] calibrationDetails = new double[Settings.numCameras][4][2];
	boolean [][] haveBlockDetails = new boolean[Settings.numCameras][4] ;
	Vec3 [] cameraPositions= new Vec3[Settings.numCameras];
	
	
	public Calibrator()
	{
		for (int k = 0; k < calibrated.length; k++)
		{
			calibrated[k] = false;
		}
	}
	
	public boolean isCalibrated()
	{
		for (int k = 0; k < calibrated.length; k++)
			if (!calibrated[k])
				return false;
		
		return true;
	}
	
	public void processBlock(InputBlock iBlock)
	{
		calibrationDetails[iBlock.cameraEvent.cameraID][iBlock.block.blockId-60][0] = iBlock.cameraEvent.x;
		calibrationDetails[iBlock.cameraEvent.cameraID][iBlock.block.blockId-60][1] = iBlock.cameraEvent.y;
		
		haveBlockDetails[iBlock.cameraEvent.cameraID][iBlock.block.blockId-60] = true;
		int numDetails = 0;
		for(int k = 0 ; k < 4 ; k++)
			if(haveBlockDetails[iBlock.cameraEvent.cameraID][iBlock.block.blockId-60]) numDetails++;
		
		if(numDetails == 4)
			calculateCameraPosition(iBlock.cameraEvent.cameraID);
			
	}
	
	private void calculateCameraPosition(int cameraID)
	{
		ParticleSystemSettings settings = new ParticleSystemSettings();
		double [] lengths = new double[6];
		
		lengths[0] = Settings.landmarks[0].distance(Settings.landmarks[1]);
		lengths[1] = Settings.landmarks[1].distance(Settings.landmarks[2]);
		lengths[2] = Settings.landmarks[2].distance(Settings.landmarks[3]);
		lengths[3] = Settings.landmarks[4].distance(Settings.landmarks[0]);
		lengths[4] = Settings.landmarks[0].distance(Settings.landmarks[2]);
		lengths[5] = Settings.landmarks[1].distance(Settings.landmarks[3]);
		
		double [] angles = new double[6];
	    angles[0] = getAngle(cameraID, 0, 1);//Math.sqrt(sqr((calibrationDetails[cameraID][0][0]- calibrationDetails[cameraID][1][0])*fov)+sqr((calibrationDetails[cameraID][0][1]- calibrationDetails[cameraID][1][1])*(fov/aspect))); 
	    angles[1] = getAngle(cameraID, 1, 2);//Math.sqrt(sqr((calibrationDetails[cameraID][1][0]- calibrationDetails[cameraID][2][0])*fov)+sqr((calibrationDetails[cameraID][1][1]- calibrationDetails[cameraID][2][1])*(fov/aspect)));
	    angles[2] = getAngle(cameraID, 2, 3);//Math.sqrt(sqr((calibrationDetails[cameraID][2][0]- calibrationDetails[cameraID][3][0])*fov)+sqr((calibrationDetails[cameraID][2][1]- calibrationDetails[cameraID][3][1])*(fov/aspect)));
	    angles[3] = getAngle(cameraID, 3, 0);//Math.sqrt(sqr((calibrationDetails[cameraID][3][0]- calibrationDetails[cameraID][0][0])*fov)+sqr((calibrationDetails[cameraID][3][1]- calibrationDetails[cameraID][4][1])*(fov/aspect)));
	    angles[4] = getAngle(cameraID, 0, 2);//Math.sqrt(sqr((calibrationDetails[cameraID][0][0]- calibrationDetails[cameraID][2][0])*fov)+sqr((calibrationDetails[cameraID][0][1]- calibrationDetails[cameraID][2][1])*(fov/aspect)));
	    angles[5] = getAngle(cameraID, 1, 3);//Math.sqrt(sqr((calibrationDetails[cameraID][1][0]- calibrationDetails[cameraID][3][0])*fov)+sqr((calibrationDetails[cameraID][1][1]- calibrationDetails[cameraID][3][1])*(fov/aspect)));
		
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
			
		settings.eval = new SphereEval(bestabc.bestPosition[0],bestabc.bestPosition[1],
				bestabc.bestPosition[2],bestabc.bestPosition[3],Settings.landmarks[0],
					Settings.landmarks[1],Settings.landmarks[2],Settings.landmarks[3]);
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
		
		cameraPositions[cameraID] = new Vec3(best.bestPosition[0],best.bestPosition[1],best.bestPosition[2]);	
		calibrated[cameraID] = true;
	}
	
	private double getAngle(int camID, int one, int two)
	{
		double fov = Settings.cameraSettings[camID].fov;
		double aspect = Settings.cameraSettings[camID].aspectRatio;
		return Math.sqrt(sqr((calibrationDetails[camID][one][0]- calibrationDetails[camID][two][0])*fov)+sqr((calibrationDetails[camID][one][1]- calibrationDetails[camID][two][1])*(fov/aspect))); 
	}
	
	private double sqr(double val)
	{
		return val*val;	
	}
}
