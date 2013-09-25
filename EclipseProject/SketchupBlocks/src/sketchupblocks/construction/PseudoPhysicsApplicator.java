package sketchupblocks.construction;

import sketchupblocks.base.RuntimeData;
import sketchupblocks.math.Face;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.RotationMatrix3D;
import sketchupblocks.math.Vec3;

public class PseudoPhysicsApplicator 
{
	private static double maxErrorMargin = 0.999;
	private static double minErrorMargin = 0.8;
	private static double stepSize = 0.0001;
	private static int MaxIter = 2000; 
	
	public static ModelBlock applyPseudoPhysics(ModelBlock m)
	{
		//Get the top face of the block just below the block to be placed (m)
		ModelBlock mBelow = EnvironmentAnalyzer.getModelBlockBelow(m);
		
		Face topFace;
		if (mBelow == null)
		{
			
			//System.out.println("Block "+m.smartBlock.blockId+" has construction floor below");
			topFace = new Face(new Vec3(100, 100, 0), new Vec3(-100, -100, 0), new Vec3(100, -100, 0));
		}
		else 
		{
			//System.out.println("Block "+m.smartBlock.blockId+" has "+mBelow.smartBlock.blockId+" below");
			topFace = EnvironmentAnalyzer.getFacingFace(mBelow, new Vec3(0, 0, 1)); //This face has already been rotated.
		}
		
		Vec3 surfaceNormal = topFace.normal();
		Vec3 invertedSurfaceNormal = Vec3.scalar(-1, surfaceNormal);
		//System.out.println("Surface normal: "+surfaceNormal);
		Face bottomFace = EnvironmentAnalyzer.getFacingFace(m, invertedSurfaceNormal); //Get the face of m that is towards the block below it.
		System.out.println("Bottom face normal: "+bottomFace.normal());
		RuntimeData.topFace = topFace;
		RuntimeData.bottomFace = bottomFace;
		Matrix calculatedRotationMatrix = EnvironmentAnalyzer.extractRotationMatrix(m.transformationMatrix); //Get rotation already applied to bottomFace.
		double largest = Math.abs(Vec3.dot(invertedSurfaceNormal, bottomFace.normal()));
		
		//If the bottom face of m is "too slanted" or approximately straight, then we do nothing.
		if (largest < maxErrorMargin && largest > minErrorMargin)
		{
			//Fix minor rotations;
			//First we need to find the direction to rotate in for the x and then the y axes.
			
			RotationMatrix3D minXRot = findMinimalRot(bottomFace, Matrix.Axis.X_AXIS, invertedSurfaceNormal);
			System.out.println("X ROT: "+minXRot);
			bottomFace = new Face(Matrix.multiply(minXRot, new Matrix(bottomFace.corners, true)).toVec3Array());
			RotationMatrix3D minYRot = findMinimalRot(bottomFace, Matrix.Axis.Y_AXIS, invertedSurfaceNormal);
			System.out.println("Y ROT: "+minYRot);
			bottomFace = new Face(Matrix.multiply(minYRot, new Matrix(bottomFace.corners, true)).toVec3Array());
			System.out.println("This should be approximately the negative Z axis: "+bottomFace.normal());
			System.out.println("FINAL Bottom face normal: "+bottomFace.normal());
			calculatedRotationMatrix = Matrix.multiply(minYRot, Matrix.multiply(minXRot, calculatedRotationMatrix));
			
			//Translate the model down to the height of the face just below:
			Vec3 origTrans = m.transformationMatrix.colToVec3(3);
			for (int k = 0; k < bottomFace.corners.length; k++)
				Vec3.add(bottomFace.corners[k], origTrans);
			
			//We find a point on the bottom face of the modelblock to be placed. 
			Vec3 pointOnBottomFace = Vec3.scalar(0.5, Vec3.add(bottomFace.corners[0], bottomFace.corners[1]));
			//We then project that point onto the top face of the modelblock below it.
			//Final z co-ordinate is:
			double z = (surfaceNormal.x*pointOnBottomFace.x + surfaceNormal.y*pointOnBottomFace.y-Vec3.dot(surfaceNormal, topFace.corners[0]))/surfaceNormal.z;
			double z2 = (surfaceNormal.x*bottomFace.corners[2].x + surfaceNormal.y*bottomFace.corners[2].y-Vec3.dot(surfaceNormal, topFace.corners[0]))/surfaceNormal.z;
			double diffZ = (pointOnBottomFace.z - z);
			double diffZ2 = bottomFace.corners[2].z - z2;
			//The difference in z co-ordinates is then the additional translation to apply.
			System.out.println("These should be the same "+diffZ2+" and "+diffZ);
			
			double[][] data = new double[4][4];
			for (int k = 0; k < calculatedRotationMatrix.rows; k++)
			{
				for (int i = 0; i < calculatedRotationMatrix.cols; i++)
					data[k][i] = calculatedRotationMatrix.data[k][i];
			}
	        for (int k = 0; k < 3; k++)
	        {
	        	data[k][3] = m.transformationMatrix.data[k][3];
	        }
			System.out.println("z difference: "+diffZ);
	        data[2][3] += diffZ;
	        //System.out.println("Before: "+m.transformationMatrix);
	        m.transformationMatrix = new Matrix(data);
	        //System.out.println("After: "+m.transformationMatrix);
		}
		else
		{
		}
		
		return m;
	}
	
	private static RotationMatrix3D findMinimalRot(Face bottomFace, Matrix.Axis axis, Vec3 surfaceNormal)
	{
		
		RotationMatrix3D result = new RotationMatrix3D(0, axis);
		
		double localStep = stepSize;
		double prevDot;
		int count = 0;
		
		//Find direction:
		result.updateTheta(localStep);
		double positiveDot = Math.abs(Vec3.dot(Matrix.multiply(result,  bottomFace.normal()), surfaceNormal));
		result.updateTheta(-1*localStep);
		double negativeDot = Math.abs(Vec3.dot(Matrix.multiply(result, bottomFace.normal()), surfaceNormal));
		if (negativeDot > positiveDot)
		{
			localStep *= -1;
			prevDot = negativeDot;
		}
		else
		{
			prevDot =positiveDot;
		}

		double currentTheta = localStep;
		double currDot = Double.MAX_VALUE;
		
		while(prevDot < currDot && count < MaxIter)
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
	
	
	public static void setErrorMargin(double _maxErrorMargin, double _minErrorMargin)
	{
		maxErrorMargin = _maxErrorMargin;
		minErrorMargin = _minErrorMargin;
	}
	
	public static double getMaxErrorMargin()
	{
		return maxErrorMargin;
	}
	
	public static double getMinErrorMargin()
	{
		return minErrorMargin;
	}
}
