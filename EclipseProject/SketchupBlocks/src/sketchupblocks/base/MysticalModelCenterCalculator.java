package sketchupblocks.base;

import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;
import sketchupblocks.math.Vec4;

public class MysticalModelCenterCalculator 
{

	private static double ERROR_MARGIN = 0.5; //Of square difference
	
	public static Vec3 calculateModelCenter(InputBlock[] fidData, Vec3[] positions, Vec3[] cameraViewVectors)
	{
		try
		{
				if (fidData.length == 3)
				{
					//Ask the mystical SVD decomposer.
					return null;
				}
				
				Vec3 m1 = Vec3.scalar(-1, ((SmartBlock)fidData[0].block).fiducialCoordinates[fidData[0].cameraEvent.fiducialID]);
				Vec3 m2 = Vec3.scalar(-1, ((SmartBlock)fidData[1].block).fiducialCoordinates[fidData[1].cameraEvent.fiducialID]);
				Vec3 m1m2Mid = Vec3.midpoint(m1, m2);
				
				//Now translate these points so that m1m2Mid is at the origin.
				Vec3 origin = new Vec3(0, 0, 0);
				Matrix mTranslation = getTranslationMatrix(m1m2Mid, origin);
				Vec3 m1o = Matrix.multiply(mTranslation, new Vec4(m1)).toVec3();
				Vec3 m2o = Matrix.multiply(mTranslation, new Vec4(m2)).toVec3();
				
				//Translate the fiducials' midpoint to the origin.
				//Multiply the fiducial positions by the same translation matrix.
				Vec3 w1w2Mid = Vec3.midpoint(positions[0], positions[1]);
				Matrix wTranslation = getTranslationMatrix(w1w2Mid, origin);
				Vec3 w1o = Matrix.multiply(wTranslation, new Vec4(positions[0])).toVec3();
				Vec3 w2o = (Matrix.multiply(wTranslation, new Vec4(positions[1]))).toVec3();
				
				
				Vec3 dm = Vec3.subtract(m1, m2);
				Vec3 d = Vec3.subtract(positions[0], positions[1]);
				
				//Choose offset in some direction perpendicular to the d.
				Vec3 offset = new Vec3(1, 1, -(d.x + d.y)/d.z);
				offset.normalize();
				Vec3 offsetPoint = Vec3.add(origin, offset);
				//Choose a similar offset,  perpendicular to dm.
				Vec3 oOffset = new Vec3(1, 1, -(dm.x + dm.y)/dm.z);
				Vec3 oOffsetPoint = Vec3.add(origin, Vec3.normalize(oOffset));
				
				//Now we have 3 points, so we can continue from there....
				//Get the rotation between the model points and fiducial points
				Matrix R = SVDDecomposer.getRotationMatrix(new Vec3[]{m1o, m2o, oOffsetPoint}, new Vec3[]{w1o, w2o, offsetPoint});
				
				//mOffset = x
				//basis2 = y
				//d = z
				Vec3 basis2 = Vec3.cross(dm, oOffset); //Right?
				//So now we can rotate about x and y
				
				Matrix toDWorld = new Matrix(new Vec3[]{oOffset, basis2, dm}, true);
				
				Vec3[] dFidNorms = new Vec3[2];
				for (int k = 0; k < 2; k++)
				{
					//Transform our fiducial normals from model space to D world.
					dFidNorms[k] = fidData[k].block.fiducialOrient[fidData[k].cameraEvent.fiducialID];
					dFidNorms[k] = Matrix.multiply(mTranslation, new Vec4(dFidNorms[k])).toVec3();
					dFidNorms[k] = Matrix.multiply(toDWorld, dFidNorms[k]);
				}
				
				RotationMatrix rTry = new RotationMatrix(0);
				Vec3[] tryNorms = new Vec3[2];
				//Now we rotate them iteratively
				for (int k = 0; k < 360; k += 2)
				{
					rTry.updateTheta(k);
					
					//Rotate the fiducial normals through the proposed angle and see whether that matches the observed norms.
					//For this, we need the observed norms.
					for (int i = 0; i < 2; i++)
						tryNorms[i] = Matrix.multiply(rTry, dFidNorms[i]);
					
					
					//Evaluate goodness of rTry.
				}
				
				
		}
		catch(Exception e)
		{
			System.out.println("Error");
		}
		return null;
	}
	
	static Matrix getTranslationMatrix(Vec3 one, Vec3 two)
	{
		Matrix res = new Matrix(4, 4);
		double[][] data = new double[4][];
		data[0] = new double[]{1, 0, 0, two.x - one.x};
		data[1] = new double[]{0, 1, 0, two.y - one.y};
		data[2] = new double[]{0, 0, 1, two.z - one.z};
		data[3] = new double[]{0, 0, 0, 1};
		res.data = data;
		return res;
	}
}
