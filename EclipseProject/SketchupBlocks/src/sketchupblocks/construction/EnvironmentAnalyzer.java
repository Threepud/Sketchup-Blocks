package sketchupblocks.construction;

import java.util.ArrayList;
import java.util.Collection;

import sketchupblocks.base.Logger;
import sketchupblocks.base.Model;
import sketchupblocks.base.RuntimeData;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.exception.ModelNotSetException;
import sketchupblocks.math.Face;
import sketchupblocks.math.Line;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;
import sketchupblocks.network.Lobby;

/**
 * A utility class for analyzing the model.
 * @author Hein
 * @author Elre
 */
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
			
			BoundingBox belowBB = null;
			for (ModelBlock modelBlock : blocks)
			{
				if (modelBlock.smartBlock.blockId != newBlock.smartBlock.blockId)
				{
					BoundingBox modelBB = BoundingBox.generateBoundingBox(modelBlock);
					boolean overlap = checkOverlap(newBB, modelBB);
					
					if (overlap)
					{
						if (belowBB != null && higherThan(modelBB, belowBB) && higherThan(newBB, modelBB))
						{
							belowBB = modelBB;
						}
						else if (belowBB == null && higherThan(newBB, modelBB))
						{
							belowBB = modelBB;
						}
					}
				}
			}
			
			if (belowBB == null)
			{
				return null;
			}
			return belowBB.modelBlock;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	public static void sortBottomUp(ArrayList<ModelBlock> blocks)
	{
		BoundingBox[] bb = new BoundingBox[blocks.size()];
		for (int k = 0; k < bb.length; k++)
		{
			bb[k] = BoundingBox.generateBoundingBox(blocks.get(k));
		}
		
		for (int i = 1; i < blocks.size(); i++)
		{
			for (int k = i; k > 0 && higherThan(bb[k-1], bb[k]); k--)
			{
				BoundingBox tempbb = bb[k];
				bb[k] = bb[k-1];
				bb[k-1] = tempbb;
				ModelBlock tempmb = blocks.get(k);
				blocks.set(k, blocks.get(k-1));
				blocks.set(k-1, tempmb);
			}
		}
	}
	
	public static Face[] getFacingFaces(Matrix transform, SmartBlock s, Vec3 surfaceNormal)
	{
		double largestDot = -Double.MAX_VALUE;
		double secondDot = -Double.MAX_VALUE;
		int largestDotIndex = -1; 
		int secondDotIndex = -1;
		Matrix rotationMatrix = extractRotationMatrix(transform);

		Face[] worldFaces = getFaces(s);
		
		for (int k = 0; k < worldFaces.length; k++)
		{
			worldFaces[k] = new Face(Matrix.multiply(rotationMatrix, new Matrix(worldFaces[k].corners, true)).toVec3Array());
			double dotWithSurfNorm = (Vec3.dot(worldFaces[k].normal(), surfaceNormal));
			
			if (dotWithSurfNorm >= largestDot)
			{
				secondDot = largestDot;
				secondDotIndex = largestDotIndex;
				largestDot = dotWithSurfNorm;
				largestDotIndex = k;
			}
			else if (dotWithSurfNorm >= secondDot)
			{
				secondDot = dotWithSurfNorm;
				secondDotIndex = k;
			}
		}
		
		if (largestDotIndex == -1)
		{
			Logger.log("This shouldn't happen!!!!!!!!!!!!!!!!!! Thank Elre for this cryptic message\n"+"In any ordered set, there should be a smallest element. Especially if the set is finite", 1);
			System.exit(-1);
		}
		
		return new Face[]{worldFaces[largestDotIndex], worldFaces[secondDotIndex]};
	}

	
	public static Face[] getFaces(SmartBlock block)
	{
		Face[] result = new Face[block.indices.length/3];
		int c = 0;
		for (int k = 0; k < block.indices.length; k += 3)
		{
			result[c++] = new Face(block.vertices[block.indices[k+0]], block.vertices[block.indices[k+1]], block.vertices[block.indices[k+2]]);
		}
		
		return result;
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
	
	protected static boolean checkOverlap(BoundingBox one, BoundingBox two)
	{
		if (checkBroad(one, two))
		{
			if (checkSAT(one, two))
				return true;
		}
		return false;
	}
	
	protected static boolean checkSAT(BoundingBox one, BoundingBox two)
	{
		double thresh = 1.5;
		ArrayList<Vec3> sepAxes = new ArrayList<Vec3>();
		sepAxes = one.generate2DSeparationAxes(sepAxes);
		sepAxes = two.generate2DSeparationAxes(sepAxes);
		
		for (int a = 0; a < sepAxes.size(); a++)
		{
		
			double min1 = Double.MAX_VALUE;
			double max1 = -Double.MAX_VALUE;
			Vec3[] worldVertices1 = one.generate2DWorldVertices();
			for (int k = 0; k < worldVertices1.length; k++)
			{
				double proj = Vec3.dot(worldVertices1[k], sepAxes.get(a));
				min1 = proj < min1 ? proj : min1;
				max1 = proj > max1 ? proj : max1;
			}
			
			double min2 = Double.MAX_VALUE;
			double max2 = -Double.MAX_VALUE;
			
			Vec3[] worldVertices2 = two.generate2DWorldVertices();
			for (int k = 0; k < two.worldVertices.length; k++)
			{
				double proj = Vec3.dot(worldVertices2[k], sepAxes.get(a));
				min2 = proj < min2 ? proj : min2;
				max2 = proj > max2 ? proj : max2;
			}
			
			if (min1 > max2 || min2 > max1)
			{
				return false;
			}
			else if ((max2 - min1 < thresh  && (max2 - min1  > 0)))
			{
				return false;
			}
			else if (max1 - min2 < thresh && max1 - min2 > 0)
			{
				return false;
			}
		}
		return true;
		
	}
	
	protected static boolean checkBroad(BoundingBox one, BoundingBox two)
	{
		double error = 3;
		if (error < two.min.x - one.max.x)
			return false;
		if (one.min.x - two.max.x > error)
			return false;
		if (error < two.min.y - one.max.y)
			return false;
		if (one.min.y - two.max.y > error)
			return false;
		return true;
	}

	protected static boolean higherThan(BoundingBox one, BoundingBox two)
	{
		if (one.max.z > two.max.z)
			return true;
		return false;
	}
	
	public static int getNumObscuredPoints(SmartBlock sm, Matrix transform, int camID, int fidID)
	{
		int count = 0;
		
		Vec3 fidPos = sm.fiducialCoordinates[fidID%6];
		double distToEdge = 3.0;
		
		int nonzeroDim = -1;
		int indexOne = -1;
		int indexTwo = -1;
		double[] fidPosData = fidPos.toArray();
		if (fidPos.x == 0 && fidPos.y == 0)
		{
			indexOne = 0;
			indexTwo = 1;
			nonzeroDim = 2;
		}
		else if (fidPos.x == 0 && fidPos.z == 0)
		{
			indexOne = 0;
			indexTwo = 2;
			nonzeroDim = 1;
		}
		else
		{
			indexOne = 1;
			indexTwo = 2;
			nonzeroDim = 0;
		}
		int numCorners = 4;
		int numPoints = numCorners+1;
		double[][] fidCornerData = new double[numCorners][3];
		Vec3[] fidPoints = new Vec3[5];
		int k = 0;
		fidCornerData[k][indexOne] = distToEdge;
		fidCornerData[k][indexTwo] = distToEdge;
		fidCornerData[k][nonzeroDim] = fidPosData[nonzeroDim];
		k++;
		fidCornerData[k][indexOne] = -distToEdge;
		fidCornerData[k][indexTwo] = distToEdge;
		fidCornerData[k][nonzeroDim] = fidPosData[nonzeroDim];
		k++;
		fidCornerData[k][indexOne] = -distToEdge;
		fidCornerData[k][indexTwo] = -distToEdge;
		fidCornerData[k][nonzeroDim] = fidPosData[nonzeroDim];
		k++;
		fidCornerData[k][indexOne] = distToEdge;
		fidCornerData[k][indexTwo] = -distToEdge;
		fidCornerData[k][nonzeroDim] = fidPosData[nonzeroDim];
		
		for (k = 0; k < numCorners; k++)
		{
			fidPoints[k] = new Vec3(fidCornerData[k]);
		}
		fidPoints[numCorners] = fidPos;
		
		for (k = 0; k < numPoints; k++)
		{
			Vec3 fidWorld = Matrix.multiply(transform, fidPoints[k].padVec3()).toVec3();
			ModelBlock[] mb = getIntersectingModels(RuntimeData.getCameraPosition(camID), fidWorld);
			if ((mb.length == 1 && mb[0].smartBlock.blockId == sm.blockId) || mb.length == 0)
			{
				count = count + 0;
			}
			else
			{
				count++;
			}
		}
		
		
		
		return count;
	}
	
	/**
	 * Return an array of blocks a line passes through.
	 * @return An array of blocks
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
			if(isIntersecting(theLine,mb))
			{
				result.add(mb);
			}
			
		}
		
		
		 ModelBlock [] res = new  ModelBlock [0];
		 return result.toArray(res);	
	}
	
	public static boolean isIntersecting(Line line , ModelBlock mb)
	{
		for(int k = 0 ; k < mb.smartBlock.indices.length ; k+= 3)
		{
			Vec3 v0 = Matrix.multiply(mb.transformationMatrix , mb.smartBlock.vertices[mb.smartBlock.indices[k+0]].padVec3()).toVec3();
			Vec3 v1 = Matrix.multiply(mb.transformationMatrix , mb.smartBlock.vertices[mb.smartBlock.indices[k+1]].padVec3()).toVec3();
			Vec3 v2 = Matrix.multiply(mb.transformationMatrix , mb.smartBlock.vertices[mb.smartBlock.indices[k+2]].padVec3()).toVec3();
			if(rayIntersectsTriangle(line,v0,v1,v2))
			{
					
				return true; // it goes through at least one face so we are good.
			}
		
		}
		return false;
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
