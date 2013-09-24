package sketchupblocks.construction;

import sketchupblocks.math.Vec3;

public class BoundingBox 
{
	public Vec3 min;
	public Vec3 max;
	
	public BoundingBox(Vec3 _max, Vec3 _min)
	{
		min = _min;
		max = _max;
	}
}
