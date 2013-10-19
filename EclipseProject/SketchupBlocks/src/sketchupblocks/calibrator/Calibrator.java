package sketchupblocks.calibrator;
import sketchupblocks.base.InputBlock;
import sketchupblocks.base.RuntimeData;
import sketchupblocks.base.Settings;
import sketchupblocks.base.Logger;
import sketchupblocks.math.LineDirectionSolver;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.SingularMatrixException;
import sketchupblocks.math.Vec3;
import sketchupblocks.math.nonlinearmethods.CP;
import sketchupblocks.math.nonlinearmethods.ErrorFunction;
import sketchupblocks.math.nonlinearmethods.Newton;
import sketchupblocks.math.nonlinearmethods.SIS;


public class Calibrator 
{
	//public boolean[][] haveBlockDetails;
	
	public Calibrator()
	{
		//Unit tests need this check
		//TODO: Examine this closely
		if(Settings.numCameras == 0)
		{
			Settings.readSettings("Settings.xml");
		}
	}
	
	
	public boolean processBlock(InputBlock iBlock)
	{
		if(Settings.numCameras == 0)
			throw new RuntimeException("ERROR: Settings, no cameras set.");
		
		if(iBlock.cameraEvent.fiducialID >= 60 || iBlock.cameraEvent.fiducialID <= 63)
		{
			RuntimeData.cameraCalibrationDetails[iBlock.cameraEvent.cameraID][iBlock.cameraEvent.fiducialID-60][0] = iBlock.cameraEvent.x;
			RuntimeData.cameraCalibrationDetails[iBlock.cameraEvent.cameraID][iBlock.cameraEvent.fiducialID-60][1] = iBlock.cameraEvent.y;
		}
		else
		if(iBlock.cameraEvent.fiducialID >= 200 || iBlock.cameraEvent.fiducialID <= 203)
		{	
			RuntimeData.cameraCalibrationDetails[iBlock.cameraEvent.cameraID][iBlock.cameraEvent.fiducialID-200][0] = iBlock.cameraEvent.x;
			RuntimeData.cameraCalibrationDetails[iBlock.cameraEvent.cameraID][iBlock.cameraEvent.fiducialID-200][1] = iBlock.cameraEvent.y;
		}
		
			
		RuntimeData.haveCalibrationDetails[iBlock.cameraEvent.cameraID][iBlock.cameraEvent.fiducialID-60] = true;
		if (RuntimeData.haveAllCalibrationDetails(iBlock.cameraEvent.cameraID))
		{
			RuntimeData.clearCalibrationDetails(iBlock.cameraEvent.cameraID);
			calculateCameraPosition(iBlock.cameraEvent.cameraID);
			return true;
		}
		return false;	
	}
	
	private void calculateCameraPosition(int cameraID)
	{
		Logger.log("Camera busy calibrating", 98);
		
		double [] lengths = new double[6];
		
		lengths[0] = Settings.landmarks[0].distance(Settings.landmarks[1]);
		lengths[1] = Settings.landmarks[1].distance(Settings.landmarks[2]);
		lengths[2] = Settings.landmarks[2].distance(Settings.landmarks[3]);
		lengths[3] = Settings.landmarks[3].distance(Settings.landmarks[0]);
		lengths[4] = Settings.landmarks[0].distance(Settings.landmarks[2]);
		lengths[5] = Settings.landmarks[1].distance(Settings.landmarks[3]);
		
		double [] angles = new double[6];
	    angles[0] = RuntimeData.getAngle(cameraID, 0, 1); 
	    angles[1] = RuntimeData.getAngle(cameraID, 1, 2);
	    angles[2] = RuntimeData.getAngle(cameraID, 2, 3);
	    angles[3] = RuntimeData.getAngle(cameraID, 3, 0);
	    angles[4] = RuntimeData.getAngle(cameraID, 0, 2);
	    angles[5] = RuntimeData.getAngle(cameraID, 1, 3);

	    
	    SIS radiiFunction = new SIS(lengths, angles);
	    ErrorFunction radiiErrorFunction = new ErrorFunction(radiiFunction);
	    double[] radiiX0 = new double[]{60, 60, 60, 60};
	    Matrix radii;
	    try
	    {
	    	radii = Newton.go(new Matrix(radiiX0), radiiErrorFunction);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	return;
	    }
	    
	    CP camPosFunction = new CP(Settings.landmarks, radii.toArray());
	    ErrorFunction camPosErrorFunction = new ErrorFunction(camPosFunction);
	    double[] camPosX0 = new double[]{1, 1, 1};
	    Matrix camPos;
	    try
	    {
	    	camPos = Newton.go(new Matrix(camPosX0), camPosErrorFunction);
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    	return;
			//TODO: Fail gracefully
	    }
	    Logger.log("Camera position: "+camPos.toVec3(), 50);
	    
	    Vec3[] landmarkToCamera = new Vec3[4];
		double[] myAngles = new double[4];
		
		for (int k = 0; k < 4; k++)
		{
			landmarkToCamera[k] = Vec3.subtract(Settings.landmarks[k], camPos.toVec3());
			myAngles[k] = RuntimeData.getAngle(cameraID, k, 0.5, 0.5);
		}
		
		// Do calculation 
		try
		{
			Vec3 camViewVector = LineDirectionSolver.solve(landmarkToCamera, myAngles);
		    RuntimeData.setCameraPosition(cameraID, camPos.toVec3(),camViewVector);
		}
		catch(SingularMatrixException s)
		{
			s.printStackTrace();
		}
	}
	
}
