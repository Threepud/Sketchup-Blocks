package sketchupblocks.gui;
import sketchupblocks.math.Vec3;


/**
 * @author Jacques Coetzee
 * This class wraps three vectors in order
 * to represent a camera in OpenGL.
 */
public class Camera
{
	public Vec3 up;
	public Vec3 at;
	public Vec3 eye;
	
	/**
	 * This constructor initializes the three vectors
	 * with some predefined camera position.
	 */
	public Camera()
	{
		up = new Vec3(0, 1, 0);
		at = new Vec3(0, 0, 0);
		eye = new Vec3(100, -100, 100);
	}
	
	/**
	 * This constructor creates a new camera with the 
	 * given vectors as properties.
	 * @param _up Up vector of camera.
	 * @param _at At vector of camera.
	 * @param _eye Eye vector of camera.
	 */
	public Camera(Vec3 _up, Vec3 _at, Vec3 _eye)
	{
		up = _up;
		at = _at;
		eye = _eye;
	}
}
