package sketchupblocks.base;

import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.Line;
import sketchupblocks.math.LinearSystemSolver;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.RotationMatrix3D;
import sketchupblocks.math.RotationMatrix4D;
import sketchupblocks.math.Vec3;
import sketchupblocks.math.Vec4;

public class ModelTransformationCalculator 
{

	private static double ERROR_MARGIN = 0.5; //Of square difference
	
	public static Matrix getModelTransformationMatrix(Vec3 [] rotation, SmartBlock sBlock, Vec3[] positions, Integer[] fidIDs)
	{
		try
		{
				if (fidIDs.length > 2)
				{
					
					Vec3[] modelFidCoords = new Vec3[positions.length];
					for (int k = 0; k < modelFidCoords.length; k++)
					{
						modelFidCoords[k] = sBlock.fiducialCoordinates[fidIDs[k]];
						System.out.println("Getting coord for "+fidIDs[k]);
						System.out.println(modelFidCoords[k]);
						System.out.println(positions[k]);
					}
					
					Matrix[] transformMatrices = TransformationCalculator.calculateTransformationMatrices(modelFidCoords, positions);
					//System.out.println("ROTATION: "+transformMatrices[0]);
					//System.out.println("TRANSLATION: "+transformMatrices[1]);
					return Matrix.multiply(transformMatrices[1], transformMatrices[0].padMatrix());
					/*Matrix m = Matrix.multiply(transformMatrices[1],  Matrix.identity(4)); 
					if (Settings.verbose > 3)
						System.out.println("TRANSFORMATION: "+m);
					return m;*/
				}

				Vec3 m1 = Vec3.scalar(-1, sBlock.fiducialCoordinates[fidIDs[0]]); 
				Vec3 m2 = Vec3.scalar(-1, sBlock.fiducialCoordinates[fidIDs[1]]);
				Vec3 m1m2Mid = Vec3.midpoint(m1, m2);
				
				//Now translate these points so that m1m2Mid is at the origin.
				Vec3 origin = new Vec3(0, 0, 0);
				Matrix mTranslation = getTranslationMatrix(m1m2Mid, origin);
				Vec3 m1o = Matrix.multiply(mTranslation, m1.padVec3()).toVec3();
				Vec3 m2o = Matrix.multiply(mTranslation, m2.padVec3()).toVec3();
				
				//Translate the fiducials' midpoint to the origin.
				//Multiply the fiducial positions by the same translation matrix.
				Vec3 w1w2Mid = Vec3.midpoint(positions[0], positions[1]);
				Matrix wTranslation = getTranslationMatrix(w1w2Mid, origin);
				Vec3 w1o = Matrix.multiply(wTranslation, positions[0].padVec3()).toVec3();
				Vec3 w2o = (Matrix.multiply(wTranslation, positions[1].padVec3())).toVec3();
				
				
				Vec3 dm = Vec3.subtract(m1, m2);
				Vec3 d = Vec3.subtract(positions[0], positions[1]);

				//Choose offset in some direction perpendicular to the d.
				Vec3[] offsets = getOffsets(dm, d);
				Vec3 oOffset = offsets[0];
				Vec3 offset = offsets[1];
				Vec3 offsetPoint = Vec3.add(origin, offset);
				Vec3 oOffsetPoint = Vec3.add(origin, oOffset);
				
				//Now we have 3 points, so we can continue from there....
				//Get the rotation between the model points and fiducial points
				//Rotates world fiducials positions to fit model positions
				Matrix R = TransformationCalculator.calculateTransformationMatrices(new Vec3[]{w1o, w2o, offsetPoint},new Vec3[]{m1o, m2o, oOffsetPoint})[0];
				//(R*A.')'
				
				//oOffset = x
				//basis2 = y
				//d = z
				Vec3 basis2 = Vec3.cross(dm, oOffset);
				//So now we can rotate about x and y
				Matrix cobDSpace = new Matrix(new Vec3[]{oOffset, basis2, dm}, true);
				
				Matrix toDWorld = Matrix.multiply(Matrix.multiply(cobDSpace, R).padMatrix(), wTranslation);
				
				Vec4[] dFidUp = new Vec4[2];
				for (int k = 0; k < 2; k++)
				{
					//Transform our fiducial normals from model space to D world.
					dFidUp[k] = new Vec4(sBlock.fiducialOrient[fidIDs[k]]);
					dFidUp[k] = Matrix.multiply(mTranslation, dFidUp[k]);
					dFidUp[k] = Matrix.multiply(toDWorld, dFidUp[k]);
				}
				
				
				for (int i = 0; i < 2; i++)
					rotation[i] = Matrix.multiply(toDWorld, new Vec4(rotation[i])).toVec3();
				
				RotationMatrix4D rTry = new RotationMatrix4D(0, Matrix.Axis.Z_AXIS);
				Vec4[] tryUps = new Vec4[2];
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
						
						Vec3 uhm1 = getProjection(oOffset,basis2,tryUps[i].toVec3());
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
				Matrix modelToDSpace = Matrix.multiply(cobDSpace.padMatrix(),mTranslation);
				Matrix DSpaceToWorld = Matrix.multiply(toDWorld.getInverse(),rTry);
				Matrix modelToWorld = Matrix.multiply(DSpaceToWorld,modelToDSpace);
				
				return modelToWorld;
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private static Vec3[] getOffsets(Vec3 dm, Vec3 d) throws Exception
	{
		Vec3[] result = new Vec3[2];
		int index = 0;
		if (dm.x != 0 && d.x != 0)
		{
			result[index++] = Vec3.normalize(new Vec3(-(dm.y + dm.z)/dm.x, 1, 1));
			result[index++] = Vec3.normalize(new Vec3(-(d.y + d.z)/d.x, 1, 1));
		}
		else if (dm.y != 0 && d.y != 0)
		{
			result[index++] = Vec3.normalize(new Vec3(1, -(dm.x + dm.z)/dm.y, 1));
			result[index++] = Vec3.normalize(new Vec3(1, -(d.x + d.z)/d.y, 1));
		}
		else if (dm.z != 0 && d.z != 0)
		{
			result[index++] = Vec3.normalize(new Vec3(1, 1, -(dm.x + dm.y)/dm.z));
			result[index++] = Vec3.normalize(new Vec3(1, 1, -(d.x + d.y)/d.z));
		}
		else
			throw new Exception("No offset can be calculated for given d's");
		return result;
	}
	
	
	private static Vec3 getProjection(Vec3 k1,Vec3 k2, Vec3 point)
	{
		double scale1 =Vec3.dot(Vec3.normalize(k1),Vec3.normalize(point));
		double scale2 =Vec3.dot(Vec3.normalize(k2),Vec3.normalize(point));
		return Vec3.add(Vec3.scalar(scale1,k1), Vec3.scalar(scale2,k2));		
	}
	
	private static Matrix getTranslationMatrix(Vec3 one, Vec3 two)
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
