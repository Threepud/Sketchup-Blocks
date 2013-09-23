package sketchupblocks.math;

public class Face
{
	public Vec3[] corners;
	
	public Face(Vec3[] _corners) throws Exception
	{
		corners = _corners;
		if (corners.length < 2)
			throw new Exception("Invalid face specification");
	}
	
	public Vec3 normal() throws Exception
	{
		if (corners.length < 3)
			throw new Exception("Invalid face specification");
		
		Vec3 oneToTwo = Vec3.subtract(corners[0], corners[1]);
		Vec3 oneToThree = Vec3.subtract(corners[0], corners[2]);
		return Vec3.normalize(Vec3.cross(oneToTwo, oneToThree));
	}
}
