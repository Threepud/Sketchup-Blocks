
public class Settings 
{
	public static String versionNr = "0.5";
	public static String slotDirectory = "slots";
	public static String slotName = "Slot"; //Append a number to the end of this.
	public static int numSlots = 5;
	public static int scrollTrigger = 15;
	
	public static int numCameras = 1;
	public static CameraSettings [] cameraSettings = new CameraSettings[]{new CameraSettings(51.2, 640.0/480.0, 3333)};
	//cameraSettings[0] = ;
	//public static int[] cameraPorts = {3333};
	
	public static int verbose = 3; //0 is default. From there, higher is more verbose. 
	
	public static Vec3[] landmarks = new Vec3[]{ new Vec3(0, 0, 0), new Vec3(0, 5.6, 0), new Vec3(5.6, 5.6, 0), new Vec3(5.6, 0, 0)};
	
	public static class CameraSettings
	{
		double fov;
		double aspectRatio;
		int port;
		CameraSettings(double _fov, double _aspectRatio, int _port)
		{
			fov = _fov;
			aspectRatio = _aspectRatio;
			port = _port;
		}
	}
}
