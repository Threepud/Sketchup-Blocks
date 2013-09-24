package sketchupblocks.construction;

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
	private int MaxIter = 200; 
	
	
	public PseudoPhysicsApplicator(Lobby _eddy)
	{
		eddy = _eddy;
	}
	
	public void applyPseudoPhysics(ModelBlock m)
	{
		ModelBlock mBelow = EnvironmentAnalyzer.getModelBlockBelow(m);
		Face topFace = EnvironmentAnalyzer.getFacingFace(mBelow, new Vec3(0, 0, 1)); //This face has already been rotated.
		Vec3 surfaceNormal = topFace.normal();
		Face bottomFace = EnvironmentAnalyzer.getFacingFace(m, surfaceNormal);
		
		Matrix calculatedRotationMatrix = EnvironmentAnalyzer.extractRotationMatrix(m.transformationMatrix); //Get rotation already applied to bottomFace.
		
		double smallestDot = Vec3.dot(surfaceNormal, bottomFace.normal());
		
		if (smallestDot < maxErrorMargin && smallestDot > minErrorMargin)
		{
			//Fix minor rotations;
			//First we need to find the direction to rotate in for the x and then the y axes.
			RotationMatrix3D minXRot = findMinimalRot(bottomFace, Matrix.Axis.X_AXIS, surfaceNormal);
			bottomFace = new Face(Matrix.multiply(minXRot, new Matrix(bottomFace.corners, true)).toVec3Array());
			RotationMatrix3D minYRot = findMinimalRot(bottomFace, Matrix.Axis.Y_AXIS, surfaceNormal);
			bottomFace = new Face(Matrix.multiply(minYRot, new Matrix(bottomFace.corners, true)).toVec3Array());
			System.out.println("This should be approximately the negative Z axis: "+bottomFace.normal());
			
			calculatedRotationMatrix = Matrix.multiply(minYRot, Matrix.multiply(minXRot, calculatedRotationMatrix));
		}
		
		//Translate the model down to the height of the face just below.
		
		
		
		eddy.updateModel(m);
	}
	
	private RotationMatrix3D findMinimalRot(Face bottomFace, Matrix.Axis axis, Vec3 surfaceNormal)
	{
		
		RotationMatrix3D result = new RotationMatrix3D(0, axis);
		
		double localStep = stepSize;
		double prevDot;
		int count = 0;
		
		//Find direction:
		result.updateTheta(localStep);
		double positiveDot = Vec3.dot(Matrix.multiply(result,  bottomFace.normal()), surfaceNormal);
		result.updateTheta(-1*localStep);
		double negativeDot = Vec3.dot(Matrix.multiply(result, bottomFace.normal()), surfaceNormal);
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
			currDot = Vec3.dot(Matrix.multiply(result,  bottomFace.normal()), surfaceNormal);
			count++;
		}
		currentTheta -= localStep*2;
		result.updateTheta(currentTheta);
		return result;
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
