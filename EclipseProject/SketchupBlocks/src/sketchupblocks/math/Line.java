package sketchupblocks.math;

import java.io.Serializable;

public class Line implements Serializable
{
	private static final long serialVersionUID = -1894790789654200614L;
	
	public Vec3 point;
	public Vec3 direction;
	
	public Line(Vec3 _point, Vec3 _direction)
	{
		point = _point;
		direction = _direction;
	}
}
