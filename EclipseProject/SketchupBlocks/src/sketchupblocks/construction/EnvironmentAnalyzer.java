package sketchupblocks.construction;

import java.util.Collection;

import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;
import sketchupblocks.network.Lobby;

public class EnvironmentAnalyzer 
{
	private static Lobby eddy;
	
	public static void setLobby(Lobby _eddy)
	{
		eddy = _eddy;
	}
	
	public static ModelBlock getModelBlockBelow(ModelBlock newBlock)
	{
		try
		{
			BoundingBox newBB = generateBoundingBox(newBlock);
			Collection<ModelBlock> blocks = eddy.getModel().getBlocks();
			for (ModelBlock modelBlock : blocks)
			{
				BoundingBox modelBB = generateBoundingBox(newBlock);
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	private static double checkXYOverlap(BoundingBox one, BoundingBox two)
	{

		return -1;
	}
	
	private static BoundingBox compareHeights(BoundingBox one, BoundingBox two)
	{

		return null;
	}
	
	private static BoundingBox generateBoundingBox(ModelBlock mb)
	{
		double[] max = new double[]{Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};
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
