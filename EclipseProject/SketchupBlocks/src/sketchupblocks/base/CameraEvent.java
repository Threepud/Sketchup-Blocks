package sketchupblocks.base;

public class CameraEvent
{
	public enum EVENT_TYPE
	{
		ADD,
		REMOVE,
		UPDATE
	}

	public int cameraID;
	public float x;
	public float y;
	public float rotation;
	public float rotAcceleration;
	public float rotVelocity;
	public float xVelocity;
	public EVENT_TYPE type;
	public int fiducialID;
}