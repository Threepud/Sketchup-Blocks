
public class Camera
{
	public Vec3 up;
	public Vec3 at;
	public Vec3 eye;
	
	public Camera()
	{
		up = new Vec3(0, 1, 0);
		at = new Vec3(0, 0, 0);
		eye = new Vec3(100, -100, 100);
	}
}
