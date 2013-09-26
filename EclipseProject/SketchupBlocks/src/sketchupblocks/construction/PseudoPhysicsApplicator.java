package sketchupblocks.construction;

import sketchupblocks.base.Logger;
import sketchupblocks.base.RuntimeData;
import sketchupblocks.math.Face;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.RotationMatrix3D;
import sketchupblocks.math.Vec3;

public class PseudoPhysicsApplicator 
{
	private static double maxErrorMargin = 0.99999;
	private static double minErrorMargin = 0.5;
	private static double stepSize = 0.01;
	private static int MaxIter = 2000; 
	private static double minDot = 0;
	
	public static ModelBlock applyPseudoPhysics(ModelBlock m)
	{
		//Get the top face of the block just below the block to be placed (m)
		ModelBlock mBelow = EnvironmentAnalyzer.getModelBlockBelow(m);
		
		Face topFace;
		if (mBelow == null)
		{
			mBelow = new ModelBlock();
			mBelow.transformationMatrix = Matrix.identity(4);
			topFace = new Face(new Vec3(100, -100, 0), new Vec3(-100, 100, 0), new Vec3(100, 100, 0));
		}
		else 
		{
			topFace = EnvironmentAnalyzer.getFacingFace(mBelow, new Vec3(0, 0, 1)); //This face has already been rotated.
		}
		
		Vec3 surfaceNormal = topFace.normal();
		Vec3 invertedSurfaceNormal = Vec3.scalar(-1, surfaceNormal);
		Face bottomFace = EnvironmentAnalyzer.getFacingFace(m, invertedSurfaceNormal); //Get the face of m that is towards the block below it.
		
		/*System.out.println("*******************************************************************");
		System.out.println("Bottom face normal: "+bottomFace.normal());
		System.out.println("Top face norma: "+surfaceNormal);
		System.out.println("Inverted top face: "+invertedSurfaceNormal);
		System.out.println("*******************************************************************");*/
		
		RuntimeData.topFace = topFace;
		RuntimeData.bottomFace = bottomFace;
		
		
		Matrix calculatedRotationMatrix = EnvironmentAnalyzer.extractRotationMatrix(m.transformationMatrix); //Get rotation already applied to bottomFace.
		double largest = Vec3.dot(invertedSurfaceNormal, bottomFace.normal());
		
		//If the bottom face of m is "too slanted" or approximately straight, then we do nothing.
		if (largest < maxErrorMargin && largest > minErrorMargin)
		{
			if (minDot > largest)
			{
				minDot = largest;
			}
			
			int count = 0;
			//Fix minor rotations;
			while(Vec3.dot(invertedSurfaceNormal, bottomFace.normal()) < 0.999 && count < MaxIter)
			{
				RotationMatrix3D minXRot = findMinimalRot(bottomFace, Matrix.Axis.X_AXIS, invertedSurfaceNormal);
				bottomFace = new Face(Matrix.multiply(minXRot, new Matrix(bottomFace.corners, true)).toVec3Array());
				RotationMatrix3D minYRot = findMinimalRot(bottomFace, Matrix.Axis.Y_AXIS, invertedSurfaceNormal);
				bottomFace = new Face(Matrix.multiply(minYRot, new Matrix(bottomFace.corners, true)).toVec3Array());
				calculatedRotationMatrix = Matrix.multiply(minYRot, Matrix.multiply(minXRot, calculatedRotationMatrix));
				count++;
			}
			Logger.log("Old match: "+largest, 1);
			Logger.log(("New match: "+Vec3.dot(invertedSurfaceNormal, bottomFace.normal())), 1);
			
			if (count == MaxIter)
				Logger.log("WARNING: Major changes to block orientation", 2);
			
			//Translate the model down to the height of the face just below:
			Vec3 origTrans = m.transformationMatrix.colToVec3(3);
			Vec3 surfaceTrans = mBelow.transformationMatrix.colToVec3(3);
			for (int k = 0; k < bottomFace.corners.length; k++)
			{
				bottomFace.corners[k] = Vec3.add(bottomFace.corners[k], origTrans);
				topFace.corners[k] = Vec3.add(topFace.corners[k], surfaceTrans);
			}
			
			RuntimeData.bottomFace = bottomFace;
			RuntimeData.topFace = topFace;
			
			//We find a point on the bottom face of the modelblock to be placed. 
			Vec3 pointOnBottomFace = Vec3.scalar(1.0/3.0, Vec3.add(Vec3.add(bottomFace.corners[0], bottomFace.corners[1]), bottomFace.corners[2]));
			Vec3 pointOnSurface = Vec3.scalar(1.0/3.0, Vec3.add(Vec3.add(topFace.corners[0], topFace.corners[1]), topFace.corners[2]));
			/*System.out.println("--------------------------------------------");
			System.out.println("Bottom face: ");
			for (int k = 0; k < bottomFace.corners.length; k++)
			{
				System.out.println(bottomFace.corners[k]);
			}
			System.out.println("Point: "+pointOnBottomFace);
			System.out.println("--------------------------------------------");
			System.out.println("Top face: ");
			for (int k = 0; k < topFace.corners.length; k++)
			{
				System.out.println(topFace.corners[k]);
			}
			System.out.println("Point: "+pointOnSurface);
			System.out.println("--------------------------------------------");*/
			//We then project that point onto the top face of the modelblock below it.
			//Final z co-ordinate is:
			double z = (-surfaceNormal.x*pointOnBottomFace.x - surfaceNormal.y*pointOnBottomFace.y+Vec3.dot(surfaceNormal, pointOnSurface))/surfaceNormal.z;
			double diffZ = -(pointOnBottomFace.z - z);
			/System.out.println("Diff z "+diffZ);
			//The difference in z co-ordinates is then the additional translation to apply.*/
			
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
	        data[2][3] += diffZ;
	        m.transformationMatrix = new Matrix(data);
		}
		else
		{
			//System.out.println("Igoring "+largest);
		}
		//System.out.println("Adding "+m.smartBlock.blockId);
		return m;
	}
	
	private static RotationMatrix3D findMinimalRot(Face bottomFace, Matrix.Axis axis, Vec3 surfaceNormal)
	{
		RotationMatrix3D result = new RotationMatrix3D(0, axis);
		
		double localStep = stepSize;
		double prevDot = Vec3.dot(bottomFace.normal(), surfaceNormal);
		double currDot;
		int count = 0;
		
		//First we need to find the direction to rotate in for the x and then the y axes.
		result.updateTheta(localStep);
		double positiveDot = Vec3.dot(Matrix.multiply(result,  bottomFace.normal()), surfaceNormal);
		result.updateTheta(-1*localStep);
		double negativeDot = Vec3.dot(Matrix.multiply(result, bottomFace.normal()), surfaceNormal);
		if (negativeDot > positiveDot)
		{
			localStep *= -1;
			currDot = negativeDot;
		}
		else
		{
			currDot = positiveDot;
		}

		double currentTheta = localStep;
		Vec3 rotatedNormal = bottomFace.normal();
		
		while(prevDot <= currDot && count < MaxIter)
		{
			currentTheta += localStep;
			localStep = 0.9*localStep;
			result.updateTheta(currentTheta);
			prevDot = currDot;
			rotatedNormal = Vec3.normalize(Matrix.multiply(result,  rotatedNormal));
			currDot = Vec3.dot(rotatedNormal, surfaceNormal);
			//System.out.println("curr" + currDot);
			count++;
		}
		//System.out.println("With result "+prevDot+" we stopped at "+count);
		currentTheta -= localStep*10.0/9.0;
		result.updateTheta(currentTheta);
		if (10 < (currentTheta*180/Math.PI))
			Logger.log("WARNING: Huge changes to block rotation", 1);
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
