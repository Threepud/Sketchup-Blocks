package sketchupblocks.math;

/**
 * @author cravingoxygen
 *
 *This class wraps the information that defines a 3D face (in terms of its corners).
 */
public class Face
{
	/**
	 * An array of vectors, where each vector is a corner of the Face.
	 */
	public Vec3[] corners;
	
	
	/**
	 * The constructor for a triangular face
	 * @param c1 - The first corner of the face
	 * @param c2 - The second corner of the face 
	 * @param c3 - The third corner of the face
	 */
	public Face(Vec3 c1, Vec3 c2, Vec3 c3)
	{
		corners = new Vec3[]{c1, c2, c3};
	}
	
	/**
	 * Constructor for a face that is not necessarily triangular.
	 * 
	 * This throws a RuntimeException if the input array isn't at least of size three.
	 * @param _corners - The array containing the corners that define a face. This must contain at least three entries.
	 */
	public Face(Vec3[] _corners)
	{
		corners = _corners;
		if (corners.length < 2)
			throw new RuntimeException("Invalid face specification");
	}
	
	/**
	 * This calculates and returns the normal of the face.
	 * A RuntimeException is throws if the face isn't defined by at least three corners.
	 * 
	 * a0 = corner1 - corner0;
	 * a1 = corner2 - corner0;
	 * normal = a1 cross a0.
	 * 
	 * @return - the normal of the face.
	 */
	public Vec3 normal()
	{
		if (corners.length < 3)
			throw new RuntimeException("Invalid face specification");
		
		Vec3 oneToTwo = Vec3.subtract(corners[1], corners[0]);
		Vec3 oneToThree = Vec3.subtract(corners[2], corners[0]);
		return Vec3.normalize(Vec3.cross(oneToThree, oneToTwo));
	}
}
