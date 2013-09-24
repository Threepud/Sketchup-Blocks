package sketchupblocks.construction;

import sketchupblocks.exception.UnexpectedNonSquareMatrixException;
import sketchupblocks.math.Face;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.RotationMatrix3D;
import sketchupblocks.math.Vec3;
import sketchupblocks.network.Lobby;

public class PseudoPhysicsApplicator 
{
	private Lobby eddy;
	private double maxErrorMargin = 0.1;
	private double minErrorMargin = 0.01;
	private double stepSize = 0.001;
	private Vec3 NegZ = new Vec3(0, 0, -1);
	private int MaxIter = 200; 
	
	
	public PseudoPhysicsApplicator(Lobby _eddy)
	{
		eddy = _eddy;
	}
	
	public void applyPseudoPhysics(ModelBlock m)
	{
		//We will first need to know what is below a particular shape so that we know how to rotate it so that its bottom face is 'flat' on the shape below.
		//First of all, how do we find the shape below it?
		//We translate it to the appropriate spot. Then, we compare its xy-range to all the other blocks in the model, starting with the most _recent_.
		//If the xy-ranges overlap, we save how much it overlaps by.
		//Once we're done, we observe which one has the most overlap and assume that our block is on top of that one.
		//Note that the construction floor will also have to be represented somehow. Otherwise slanted objects might not be handled correctly.
		//Now we know what the final z-co-ordinates must be (the height of the top face). We also know the normal of the face below us...
		
		//Make the shape 'flat' via rotations
		
		//Assume fiducials are in same order as faces.
		Face[] worldFaces = new Face[m.smartBlock.faces.length];
		Vec3[] fidPos = new Vec3[m.smartBlock.fiducialCoordinates.length];
		
		double smallestDot = Double.MAX_VALUE;
		int smallestDotIndex = -1; 
		
		Matrix rotationMatrix = extractRotationMatrix(m.transformationMatrix);
		
		
		for (int k = 0; k < worldFaces.length; k++)
		{
			//Convert the corners of every face into a matrix (of column vectors).
			//Then multiply these matrices with the current rotation matrix.
			//These will be the corners for the new, transformed faces
			//Check if it is the bottom one by finding the one that is the closest to parallel.
			worldFaces[k] = new Face(Matrix.multiply(rotationMatrix, new Matrix(m.smartBlock.faces[k].corners, true)).toVec3Array());
			fidPos[k] = Matrix.multiply(rotationMatrix, m.smartBlock.fiducialCoordinates[k]);
			double dotWithNegZ = Vec3.dot(worldFaces[k].normal(), NegZ);
			if (dotWithNegZ < smallestDot)
			{
				smallestDot = dotWithNegZ;
				smallestDotIndex = k;
			}
		}
		
		
		if (smallestDotIndex == -1)
		{
			System.out.println("This shouldn't happen!!!!!!!!!!!!!!!!!! Thank Elre for this cryptic message");
			System.out.println("In any ordered set, there should be a smallest element. Especially if the set is finite");
			System.exit(-1);
		}
		
		Face bottomFace = worldFaces[smallestDotIndex];
		Matrix finalRotationMatrix = rotationMatrix;
		if (smallestDot < maxErrorMargin && smallestDot > minErrorMargin)
		{
			//Fix minor rotations;
			//First we need to find the direction to rotate in for the x and then the y axes.
			RotationMatrix3D minXRot = findMinimalRot(bottomFace, Matrix.Axis.X_AXIS);
			bottomFace = new Face(Matrix.multiply(minXRot, new Matrix(bottomFace.corners, true)).toVec3Array());
			RotationMatrix3D minYRot = findMinimalRot(bottomFace, Matrix.Axis.Y_AXIS);
			bottomFace = new Face(Matrix.multiply(minYRot, new Matrix(bottomFace.corners, true)).toVec3Array());
			System.out.println("This should be approximately the negative Z axis: "+bottomFace.normal());
			
			finalRotationMatrix = Matrix.multiply(minYRot, Matrix.multiply(minXRot, rotationMatrix));
		}
		
		//Translate the model down to the height of the face just below.
		
		
		
		eddy.updateModel(m);
	}
	
	private RotationMatrix3D findMinimalRot(Face bottomFace, Matrix.Axis axis)
	{
		
		RotationMatrix3D result = new RotationMatrix3D(0, axis);
		
		double localStep = stepSize;
		double prevDot;
		int count = 0;
		
		//Find direction:
		result.updateTheta(localStep);
		double positiveDot = Vec3.dot(Matrix.multiply(result,  bottomFace.normal()), NegZ);
		result.updateTheta(-1*localStep);
		double negativeDot = Vec3.dot(Matrix.multiply(result, bottomFace.normal()), NegZ);
		if (Math.abs(negativeDot) < Math.abs(positiveDot))
		{
			localStep *= -1;
			prevDot = Math.abs(negativeDot);
		}
		else
		{
			prevDot = Math.abs(positiveDot);
		}

		double currentTheta = localStep;
		double currDot = Double.MAX_VALUE;
		
		while(prevDot > currDot && count < MaxIter)
		{
			currentTheta += localStep*0.5;
			result.updateTheta(currentTheta);
			prevDot = currDot;
			currDot = Vec3.dot(Matrix.multiply(result,  bottomFace.normal()), NegZ);
			count++;
		}
		currentTheta -= localStep*2;
		result.updateTheta(currentTheta);
		return result;
	}
	
	private Matrix extractRotationMatrix(Matrix mat)
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
	
	public void setErrorMargin(double _maxErrorMargin, double _minErrorMargin)
	{
		maxErrorMargin = _maxErrorMargin;
		minErrorMargin = _minErrorMargin;
	}
	
	public double getMaxErrorMargin()
	{
		return maxErrorMargin;
	}
	
	public double getMinErrorMargin()
	{
		return minErrorMargin;
	}
}
