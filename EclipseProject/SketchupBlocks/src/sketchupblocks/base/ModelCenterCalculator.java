package sketchupblocks.base;

import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.Line;
import sketchupblocks.math.LinearSystemSolver;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.RotationMatrix3D;
import sketchupblocks.math.Vec3;
import sketchupblocks.math.Vec4;

public class ModelCenterCalculator 
{

	private static double ERROR_MARGIN = 0.5; //Of square difference
	
	public static Vec3 calculateModelCenter(Vec3 [] rotation, SmartBlock sBlock, Vec3[] positions, Integer[] fidIDs)
	{
		try
		{
				if (fidIDs.length == 3)
				{
					//Ask the mystical SVD decomposer.
					return null;
				}
				
				Vec3 m1 = Vec3.scalar(-1, sBlock.fiducialCoordinates[fidIDs[0]]); //((SmartBlock)fidData[0].block).fiducialCoordinates[fidData[0].cameraEvent.fiducialID]);
				Vec3 m2 = Vec3.scalar(-1, sBlock.fiducialCoordinates[fidIDs[1]]);//((SmartBlock)fidData[1].block).fiducialCoordinates[fidData[1].cameraEvent.fiducialID]);
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
				//Rotates world fiducials positions to fit model positions
				Matrix R = RotationMatrixCalculator.calculateRotationMatrix(new Vec3[]{w1o, w2o, offsetPoint},new Vec3[]{m1o, m2o, oOffsetPoint});
				
				//oOffset = x
				//basis2 = y
				//d = z
				Vec3 basis2 = Vec3.cross(dm, oOffset);
				//So now we can rotate about x and y
				Matrix cobDSpace = new Matrix(new Vec3[]{oOffset, basis2, dm}, true);
				
				Matrix toDWorld = Matrix.multiply(Matrix.multiply(cobDSpace, R), wTranslation);
				
				Vec3[] dFidUp = new Vec3[2];
				for (int k = 0; k < 2; k++)
				{
					//Transform our fiducial normals from model space to D world.
					dFidUp[k] = sBlock.fiducialOrient[fidIDs[k]];
					dFidUp[k] = Matrix.multiply(mTranslation, new Vec4(dFidUp[k])).toVec3();
					dFidUp[k] = Matrix.multiply(toDWorld, dFidUp[k]);
				}
				
				
				for (int i = 0; i < 2; i++)
					rotation[i] = Matrix.multiply(toDWorld, new Vec4(rotation[i])).toVec3();
				
				RotationMatrix3D rTry = new RotationMatrix3D(0);
				Vec3[] tryUps = new Vec3[2];
				double [] scores = new double[360];
				//Now we rotate them iteratively
				for (int k = 0; k < 360; k += 2)
				{
					rTry.updateTheta(k);
					
					//Rotate the fiducial normals through the proposed angle and see whether that matches the observed norms.
					//For this, we need the observed norms.
					double error = 0;
					for (int i = 0; i < 2; i++)
					{
						tryUps[i] = Matrix.multiply(rTry, dFidUp[i]);
						
						Vec3 uhm1 = getProjection(oOffset,basis2,tryUps[i]);
						Vec3 uhm2 = getProjection(oOffset,basis2,rotation[i]);
						
						error += Math.acos(Vec3.dot(uhm1, uhm2));
						
					}
					scores[k] = error;
				}
				
				int highest = 0;
				for (int k = 0; k < 360; k += 2)
					if(scores[k] > scores[highest])
						highest = k;
				
				//so highest is the best rotation
				rTry.updateTheta(highest);
				Matrix modelToDSpace = Matrix.multiply(cobDSpace,mTranslation);
				Matrix DSpaceToWorld = Matrix.multiply(toDWorld.inverse(),rTry);
				Matrix modelToWorld = Matrix.multiply(DSpaceToWorld,modelToDSpace);
				
				
				
		}
		catch(Exception e)
		{
			System.out.println("Error:"+e);
		}
		return null;
	}
	
	static Vec3 getProjection(Vec3 k1,Vec3 k2, Vec3 point)
	{
		double scale1 =Vec3.dot(Vec3.normalize(k1),Vec3.normalize(point));
		double scale2 =Vec3.dot(Vec3.normalize(k2),Vec3.normalize(point));
		return Vec3.add(Vec3.scalar(scale1,k1), Vec3.scalar(scale2,k2));		
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
