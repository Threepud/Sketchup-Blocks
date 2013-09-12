package sketchupblocks.calibrator;
import sketchupblocks.base.InputBlock;
import sketchupblocks.base.Settings;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;
import sketchupblocks.math.nonlinearmethods.CP;
import sketchupblocks.math.nonlinearmethods.ErrorFunction;
import sketchupblocks.math.nonlinearmethods.Newton;
import sketchupblocks.math.nonlinearmethods.SIS;


public class Calibrator 
{
	boolean calibrated[] = new boolean[Settings.numCameras];
	
	public double [][][] calibrationDetails = new double[Settings.numCameras][4][2];
	boolean [][] haveBlockDetails = new boolean[Settings.numCameras][4] ;
	public Vec3 [] cameraPositions= new Vec3[Settings.numCameras];
	
	
	public Calibrator()
	{
		for (int k = 0; k < calibrated.length; k++)
		{
			calibrated[k] = false;
		}
		for(int l = 0 ; l < haveBlockDetails.length ; l++)
			for(int k = 0 ; k < haveBlockDetails[l].length ; k++)
			{
				haveBlockDetails[l][k] = false;
			}
	}
	
	public boolean[] getCalibrated()
	{
		return calibrated;
	}
	
	public boolean isCalibrated()
	{
		for (int k = 0; k < calibrated.length; k++)
			if (!calibrated[k])
				return false;
		
		return true;
	}
	
	public boolean processBlock(InputBlock iBlock) throws Exception
	{
		calibrationDetails[iBlock.cameraEvent.cameraID][iBlock.cameraEvent.fiducialID-60][0] = iBlock.cameraEvent.x;
		calibrationDetails[iBlock.cameraEvent.cameraID][iBlock.cameraEvent.fiducialID-60][1] = iBlock.cameraEvent.y;
		
		haveBlockDetails[iBlock.cameraEvent.cameraID][iBlock.cameraEvent.fiducialID-60] = true;
		int numDetails = 0;
		for(int k = 0 ; k < 4 ; k++)
			if(haveBlockDetails[iBlock.cameraEvent.cameraID][k])
				numDetails++;
		
		if(numDetails == 4)
		{
			//Only calibrate once all 4 blocks change
			for(int k = 0 ; k < 4 ; k++)
				haveBlockDetails[iBlock.cameraEvent.cameraID][k] = false;
			calculateCameraPosition(iBlock.cameraEvent.cameraID);
			return true;
		}
		return false;	
	}
	
	private void calculateCameraPosition(int cameraID) throws Exception
	{
		if(Settings.verbose >= 3)
			System.out.println("Camera busy calibrating");
		
		double [] lengths = new double[6];
		
		lengths[0] = Settings.landmarks[0].distance(Settings.landmarks[1]);
		lengths[1] = Settings.landmarks[1].distance(Settings.landmarks[2]);
		lengths[2] = Settings.landmarks[2].distance(Settings.landmarks[3]);
		lengths[3] = Settings.landmarks[3].distance(Settings.landmarks[0]);
		lengths[4] = Settings.landmarks[0].distance(Settings.landmarks[2]);
		lengths[5] = Settings.landmarks[1].distance(Settings.landmarks[3]);
		
		double [] angles = new double[6];
	    angles[0] = getAngle(cameraID, 0, 1); 
	    angles[1] = getAngle(cameraID, 1, 2);
	    angles[2] = getAngle(cameraID, 2, 3);
	    angles[3] = getAngle(cameraID, 3, 0);
	    angles[4] = getAngle(cameraID, 0, 2);
	    angles[5] = getAngle(cameraID, 1, 3);

	    
	    SIS radiiFunction = new SIS(lengths, angles);
	    ErrorFunction radiiErrorFunction = new ErrorFunction(radiiFunction);
	    double[] radiiX0 = new double[]{60, 60, 60, 60};
	    Matrix radii = Newton.go(new Matrix(radiiX0), radiiErrorFunction);
	    
	    CP camPosFunction = new CP(Settings.landmarks, radii.toArray());
	    ErrorFunction camPosErrorFunction = new ErrorFunction(camPosFunction);
	    double[] camPosX0 = new double[]{1, 1, 1};
	    Matrix camPos = Newton.go(new Matrix(camPosX0), camPosErrorFunction);
			
	    if (Settings.verbose > 3)
    	{
	    	System.out.println(camPos.toVec3());
    	}
	    cameraPositions[cameraID] = camPos.toVec3();
		calibrated[cameraID] = true;
	}
	
	private double getAngle(int camID, int one, int two)
	{
		double fov = Settings.cameraSettings[camID].fov;
		double aspect = Settings.cameraSettings[camID].aspectRatio;
		return Math.toRadians(Math.sqrt(sqr((calibrationDetails[camID][one][0]- calibrationDetails[camID][two][0])*fov)+sqr((calibrationDetails[camID][one][1]- calibrationDetails[camID][two][1])*(fov/aspect)))); 
	}
	
	private double sqr(double val)
	{
		return val*val;	
	}
}
