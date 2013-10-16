package sketchupblocks.math;

public class Face
{
	public Vec3[] corners;
	
	public Face(Vec3 c1, Vec3 c2, Vec3 c3)
	{
		corners = new Vec3[]{c1, c2, c3};
	}
	
	public Face(Vec3[] _corners)
	{
		corners = _corners;
		if (corners.length < 2)
			throw new RuntimeException("Invalid face specification");
	}
	
	public Vec3 normal()
	{
		if (corners.length < 3)
			throw new RuntimeException("Invalid face specification");
		
		Vec3 oneToTwo = Vec3.subtract(corners[1], corners[0]);
		Vec3 oneToThree = Vec3.subtract(corners[2], corners[0]);
		return Vec3.normalize(Vec3.cross(oneToThree, oneToTwo));
	}
}
