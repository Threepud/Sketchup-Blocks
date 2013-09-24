package sketchupblocks.construction;

import java.util.Collection;

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
			BoundingBox newBB = BoundingBox.generateBoundingBox(newBlock);
			Collection<ModelBlock> blocks = eddy.getModel().getBlocks();
			for (ModelBlock modelBlock : blocks)
			{
				BoundingBox modelBB = BoundingBox.generateBoundingBox(newBlock);
				
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
	
	/*
	 * Return an array of blocks a line passes through.
	 * 
	 */
	public static ModelBlock [] getIntersectingModels(Vec3 start, Vec3 end)
	{
	return null;	
	
	}
}
