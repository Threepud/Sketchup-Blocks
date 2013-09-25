package sketchupblocks.construction;

import java.util.Collection;

import sketchupblocks.base.Logger;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.Face;
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
			BoundingBox newBB = BoundingBox.generateBoundingBox(newBlock);
			Collection<ModelBlock> blocks = eddy.getModel().getBlocks();
			
			ModelBlock below = null;
			BoundingBox belowBB = null;
			
			for (ModelBlock modelBlock : blocks)
			{
				BoundingBox modelBB = BoundingBox.generateBoundingBox(newBlock);

				boolean overlap = checkXYOverlap(newBB, modelBB);
				
				if (overlap)
				{
					if (below != null && higherThan(modelBB, belowBB))
					{
						below = modelBlock;
						belowBB = modelBB;
					}
					else if (below == null)
					{
						below = modelBlock;
						belowBB = modelBB;
					}
				}
			}
			
			return below;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	public static Face getFacingFace(ModelBlock m, Vec3 surfaceNormal)
	{
		double smallestDot = Double.MAX_VALUE;
		int smallestDotIndex = -1; 
		Matrix rotationMatrix = extractRotationMatrix(m.transformationMatrix);

		Face[] worldFaces = getFaces(m.smartBlock);
		
		for (int k = 0; k < worldFaces.length; k++)
		{
			//Convert the corners of every face into a matrix (of column vectors).
			//Then multiply these matrices with the current rotation matrix.
			//These will be the corners for the new, transformed faces
			//Check if it is the bottom one by finding the one that is the closest to parallel.
			worldFaces[k] = new Face(Matrix.multiply(rotationMatrix, new Matrix(worldFaces[k].corners, true)).toVec3Array());
			double dotWithSurfNorm = Vec3.dot(worldFaces[k].normal(), surfaceNormal);
			if (dotWithSurfNorm < smallestDot)
			{
				smallestDot = dotWithSurfNorm;
				smallestDotIndex = k;
			}
		}
		
		if (smallestDotIndex == -1)
		{
			Logger.log("This shouldn't happen!!!!!!!!!!!!!!!!!! Thank Elre for this cryptic message\n"+"In any ordered set, there should be a smallest element. Especially if the set is finite", 1);
			System.exit(-1);
		}
		
		return worldFaces[smallestDotIndex];
	}
	
	public static Matrix extractRotationMatrix(Matrix mat)
	{
		if (!mat.isSquare())
			throw new RuntimeException("Cannot extract rotation matrix");
		int offset = mat.cols < 3 ? 0 : 1;
		double[][] data = new double[mat.rows- offset][mat.cols- offset];
		for (int k = 0; k < mat.rows - offset; k++)
		{
			for (int i = 0; i < mat.cols - offset; i++)
			{
				data[k][i] = mat.data[k][i];
			}
		}
		return new Matrix(data);
	}
	
	private static boolean checkXYOverlap(BoundingBox one, BoundingBox two)
	{
		if (one.max.x < two.min.x)
			return false;
		if (one.min.x > two.max.x)
			return false;
		if (one.max.y < two.min.y)
			return false;
		if (one.min.y > two.max.y)
			return false;
		return true;
	}
	
	
	/**
	 * @param one
	 * First BoundingBox
	 * @param two
	 * Second BoundingBox
	 * @return
	 * Returns true if the first bounding box is higher than the second.
	 */
	/**TODO: There was some argument here that I have forgotten.
	 */
	private static boolean higherThan(BoundingBox one, BoundingBox two)
	{
		if (one.max.z > two.max.z)
			return true;
		return false;
	}
	
	private static Face[] getFaces(SmartBlock block)
	{
		Face[] result = new Face[block.vertices.length/3];
		int c = 0;
		for (int k = 0; k < block.vertices.length; k += 3)
		{
			result[c++] = new Face(block.vertices[k+0], block.vertices[k+1], block.vertices[k+2]);
		}
		return result;
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
