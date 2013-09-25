package sketchupblocks.construction;

import java.util.ArrayList;
import java.util.Collection;

import sketchupblocks.base.Logger;
import sketchupblocks.base.Model;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.exception.ModelNotSetException;
import sketchupblocks.math.Face;
import sketchupblocks.math.Line;
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
					else
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

		Face[] worldFaces = new Face[m.smartBlock.faces.length];
		
		for (int k = 0; k < worldFaces.length; k++)
		{
			//Convert the corners of every face into a matrix (of column vectors).
			//Then multiply these matrices with the current rotation matrix.
			//These will be the corners for the new, transformed faces
			//Check if it is the bottom one by finding the one that is the closest to parallel.
			worldFaces[k] = new Face(Matrix.multiply(rotationMatrix, new Matrix(m.smartBlock.faces[k].corners, true)).toVec3Array());
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
	
	/*
	 * Return an array of blocks a line passes through.
	 * 
	 */
	public static ModelBlock [] getIntersectingModels(Vec3 start, Vec3 end)
	{
		Model model;
		try 
		{
			model = eddy.getModel();
		} 
		catch (ModelNotSetException e) 
		{
			e.printStackTrace();
			return null;
		}
		
		ArrayList<ModelBlock> result = new ArrayList<ModelBlock>();
		Line theLine = new Line(start,Vec3.subtract(end, start));
		
		
		for(ModelBlock mb : model.getBlocks())
		{
			for(int k = 0 ; k < mb.smartBlock.indices.length ; k+= 3)
			{
				Vec3 v0 = mb.smartBlock.vertices[mb.smartBlock.indices[k+0]];
				Vec3 v1 = mb.smartBlock.vertices[mb.smartBlock.indices[k+1]];
				Vec3 v2 = mb.smartBlock.vertices[mb.smartBlock.indices[k+2]];
				if(rayIntersectsTriangle(theLine,v0,v1,v2))
				{
					result.add(mb);	
					break; // it goes through at least one face so we are good.
				}
			
			}
			
		}
		
		
		 ModelBlock [] res = new  ModelBlock [0];
	return result.toArray(res);	
	}
	
	static boolean rayIntersectsTriangle(Line line, Vec3 v0,Vec3 v1,Vec3 v2)
	{
		Vec3 e1 = Vec3.subtract(v1, v0);
		Vec3 e2 = Vec3.subtract(v2, v0);
		
		Vec3 h = Vec3.cross(line.direction, e2);
		double a = Vec3.dot(e1, h);
	   
		if (a > -0.00001 && a < 0.00001)
			return(false);
		
		double f = 1.0/a;
		
		Vec3 s = Vec3.subtract(line.point, v0);
		double u = f * Vec3.dot(s, h);
		
		if (u < 0.0 || u > 1.0)
			return(false);
		
		Vec3 q = Vec3.cross(s, e1);
		double v = f*Vec3.dot(line.direction, q);
		
		if (v < 0.0 || u + v > 1.0)
			return(false);
		
		double t = f * Vec3.dot(e2,q);		
		
		if (t > 0.00001) // ray intersection
			return(true);
		else // this means that there is a line intersection
			 // but not a ray intersection
			 return (false);
	}
}
