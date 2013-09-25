package sketchupblocks.base;

import java.util.Map;
import java.util.TreeMap;

import sketchupblocks.math.Face;
import sketchupblocks.math.Vec3;

public class RuntimeData 
{
	private static Map<Integer, Vec3> cameraPositions = new TreeMap<Integer, Vec3>();
	private static Map<Integer, Boolean> cameraCalibrated = new TreeMap<Integer, Boolean>();
	
	public static double[][][] cameraCalibrationDetails;
	public static boolean[][] haveCalibrationDetails;
	
	public static Face topFace;
	public static Face bottomFace;
	
	public static void init()
	{
		cameraCalibrationDetails = new double[Settings.numCameras][Settings.landmarks.length][2];
		haveCalibrationDetails = new boolean[Settings.numCameras][Settings.landmarks.length];
		for(int l = 0 ; l < haveCalibrationDetails.length ; l++)
		{
			for(int k = 0 ; k < haveCalibrationDetails[l].length ; k++)
			{
				haveCalibrationDetails[l][k] = false;
			}
		}
	}
	
	public static void clearCalibrationDetails(int camID)
	{
		for (int k = 0; k < haveCalibrationDetails[camID].length; k++)
		{
			haveCalibrationDetails[camID][k] = false;
		}
	}
	
	public static boolean haveAllCalibrationDetails(int camID)
	{
		for (int k = 0; k < haveCalibrationDetails[camID].length; k++)
		{
			if (!haveCalibrationDetails[camID][k])
				return false;
		}
		return true;
	}
	
	public static void setCameraPosition(int camID, Vec3 position)
	{
		cameraPositions.put(camID, position);
		cameraCalibrated.put(camID, true);
	}
	
	public static Vec3 getCameraPosition(int camID)
	{
		return cameraPositions.get(camID);
	}
	
	public static boolean isCameraCalibrated(int camID)
	{
		if (cameraCalibrated.get(camID) != null)
			return true;
		return false;
	}
	
	public static boolean isSystemCalibrated()
	{
		if (cameraCalibrated.size() == Settings.numCameras)
			return true;
		return false;
	}
	

	public static double getAngle(int camID, int lm, double x, double y)
	{
		double fov = Settings.cameraSettings[camID].fov;
		double aspect = Settings.cameraSettings[camID].aspectRatio;
		return Math.toRadians( Math.sqrt(Math.pow(((cameraCalibrationDetails[camID][lm][0]- x)*fov), 2)+Math.pow((cameraCalibrationDetails[camID][lm][1]- y)*(fov/aspect), 2))); 
	}
	
	public static double getAngle(int camID, int one, int two)
	{
		double fov = Settings.cameraSettings[camID].fov;
		double aspect = Settings.cameraSettings[camID].aspectRatio;
		return Math.toRadians(Math.sqrt(Math.pow((cameraCalibrationDetails[camID][one][0]- cameraCalibrationDetails[camID][two][0])*fov, 2)+
				Math.pow((cameraCalibrationDetails[camID][one][1]- cameraCalibrationDetails[camID][two][1])*(fov/aspect), 2))); 
	}
	
}
