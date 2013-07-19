class CameraEvent
{
	enum EVENT_TYPE
	  {
		  ADD, REMOVE, UPDATE
	  }

	public int cameraID;
	public float x;
	public float y;
	public float rotation;
	public EVENT_TYPE type;
	public int fiducialID;
}