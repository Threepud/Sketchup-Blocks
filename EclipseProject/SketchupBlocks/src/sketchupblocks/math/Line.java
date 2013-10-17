package sketchupblocks.math;

import java.io.Serializable;

/**
 * The Line class wraps the information contained in a 3D line:
 * - A point on the line
 * -The direction of the line
 * 
 * @author cravingoxygen
 *
 */
public class Line implements Serializable
{
	private static final long serialVersionUID = -1894790789654200614L;
	
	/**
	 * A point on the line.
	 */
	public Vec3 point;
	
	/**
	 * The direction of the line.
	 */
	public Vec3 direction;
	
	/**
	 * Constructor that accepts a point and direction, thereby defining a line. 
	 * The line is defined in 3D space.
	 * 
	 * point_on_line = lambda*direction + point
	 * 
	 * @param _point - The point in the line equation
	 * @param _direction - The direction vector of the line
	 */
	public Line(Vec3 _point, Vec3 _direction)
	{
		point = _point;
		direction = _direction;
	}
}
