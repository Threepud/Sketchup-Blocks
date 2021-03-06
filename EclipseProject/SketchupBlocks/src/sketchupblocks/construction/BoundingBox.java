package sketchupblocks.construction;

import java.util.ArrayList;

import sketchupblocks.math.Face;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;

/**
 * The bounding box for geometry And functions to generate them
 * @author Elre
 *
 */
public class BoundingBox 
{
	public Vec3 min;
	public Vec3 max;
	public Vec3[] worldVertices;
	public ModelBlock modelBlock;
	
	protected BoundingBox()
	{
	}
	
	private BoundingBox(Vec3 _max, Vec3 _min, Vec3[] _worldVertices, ModelBlock _modelBlock)
	{
		min = _min;
		max = _max;
		worldVertices = _worldVertices;
		modelBlock = _modelBlock;
	}
	
	public static BoundingBox generateBoundingBox(ModelBlock mb)
	{
		double[] max = new double[]{-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
		double[] min = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
		double[][] data = new double[4][mb.smartBlock.vertices.length];
		for (int k = 0; k < mb.smartBlock.vertices.length; k++)
		{
			double[] vecData = mb.smartBlock.vertices[k].toArray();
			for (int i = 0; i < 3; i++)
				data[i][k] = vecData[i];
			data[3][k] = 1;
		}
		//Matrix hybridTransform = getHybridMatrix(mb);
		//Vec3[] vertices = Matrix.multiply(hybridTransform, new Matrix(data)).toVec3Array();
		Vec3[] vertices = Matrix.multiply(mb.transformationMatrix, new Matrix(data)).toVec3Array();
		for (int k = 0; k < vertices.length; k++)
		{
			double[] vert = vertices[k].toArray();
			for (int i = 0; i < vert.length; i++)
			{
				if (vert[i] < min[i])
					min[i] = vert[i];
				if (vert[i] > max[i])
					max[i] = vert[i];
			}
			
		}
		return new BoundingBox(new Vec3(max), new Vec3(min), vertices, mb);
	}
	
	public Vec3[] generate2DWorldVertices()
	{
		Vec3[] result = new Vec3[worldVertices.length];
		for (int k = 0; k < result.length; k++)
		{
			result[k] = new Vec3(worldVertices[k]);
			result[k].z = 0;
		}
		return result;
	}
	
	public ArrayList<Vec3> generate2DSeparationAxes(ArrayList<Vec3> normals)
	{
		for (int k = 0; k < worldVertices.length; k += 3)
		{
			Vec3 normal = (new Face (worldVertices[k], worldVertices[k+1], worldVertices[k+2])).normal();
			normal.z = 0;
			normal.normalize();
			if (!normals.contains(normal))
				normals.add(normal);
		}
		return normals;
	}
	
		public ArrayList<Vec3> generate3DSeparationAxes(ArrayList<Vec3> normals)
		{
			for (int k = 0; k < worldVertices.length; k += 3)
			{
				Vec3 normal = (new Face (worldVertices[k], worldVertices[k+1], worldVertices[k+2])).normal();
				if (!normals.contains(normal))
					normals.add(normal);
			}
			return normals;
		}
		

		public static Matrix getHybridMatrix(ModelBlock mb)
		{
			Matrix hybridTransform = mb.transformationMatrix;
			for (int k = 0; k < 4; k++)
				hybridTransform.data[k][3] = mb.rawMatrix.data[k][3];
			return hybridTransform;
		}
}
