package sketchupblocks.construction;

import sketchupblocks.math.Matrix;
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
	
	public static BoundingBox generateBoundingBox(ModelBlock mb)
	{
		double[] max = new double[]{-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
		double[] min = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
		
		Vec3[] vertices = Matrix.multiply(mb.transformationMatrix, new Matrix(mb.smartBlock.vertices, true)).toVec3Array();
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
		return new BoundingBox(new Vec3(max), new Vec3(min));
	}
}
