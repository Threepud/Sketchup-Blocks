package sketchupblocks.base;

import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;

public class MysticalModelCenterCalculator 
{
	private static double ERROR_MARGIN = 0.5; //Of square difference
	
	public static Vec3 calculateModelCenter(InputBlock[] fidData, Vec3[] positions, Vec3[] cameraViewVectors)
	{
		//Magically craft array of fiducial id's
		//Find max z, because it will be the top face.
		if (fidData.length == 3)
		{
			//Ask the mystical SVD decomposer.
			return null;
		}
		int topFid = 0;
		if ((positions[1].z - positions[topFid].z)*(positions[1].z - positions[topFid].z) > ERROR_MARGIN)
		{
			//Whoop! Simplest case. Take x,y, of top fiducial and z of any other fiducial as the center.
			return new Vec3(positions[topFid].x, positions[topFid].y, positions[1].z);
		}
		
		//If we arrive here, no fiducial was substantially higher than the other. So the observed positions are in the same x-y plane.
		//Now we need to determine whether we have a case of two opposite fiducials or adjacent fiducials.
		
		//Preferably, we want 2 opposite sides:
		if (((SmartBlock)fidData[0].block).areOppositeFidicials(fidData[0].cameraEvent.fiducialID, fidData[1].cameraEvent.fiducialID))
		{
			return Vec3.midpoint(positions[0], positions[1]);
		}
		
		//We now have the most difficult case, with two adjacent fiducials.
		//First we need to know which direction (w.r.t. the line between the two fiducials) is towards the inside of the block.
		
		Vec3 f1 = ((SmartBlock)fidData[0].block).fiducialCoordinates[fidData[0].cameraEvent.fiducialID];
		Vec3 f2 = ((SmartBlock)fidData[1].block).fiducialCoordinates[fidData[1].cameraEvent.fiducialID];
		Vec3 d = Vec3.subtract(f1,  f2);
		
		double theta = Math.acos((Vec3.dot(f1, f1) + Vec3.dot(d, d) - Vec3.dot(f2, f2))/(2*f1.length()*d.length()));
		
		Matrix wiggle = new Matrix(Vec3.normalize(d));
		RotationMatrix ninety = new RotationMatrix(Math.PI/2.0);
		if (Vec3.dot(cameraViewVectors[0], (Matrix.multiply(ninety, wiggle)).toVec3()) < 0)
		{
			if (Settings.verbose > 2)
				System.out.println("Clockwise failed. Trying anticlockwise");
			ninety.updateTheta(-1*Math.PI/2.0);
			if (Vec3.dot(cameraViewVectors[0], (Matrix.multiply(ninety, wiggle)).toVec3()) < 0)
			{
				System.out.println("Eek! Neither direction is plausible. What to do!?\nReturning null like a pansy.");
				return null;
			}
			else
			{
				theta = Math.PI*2 - theta;
			}
		}
		
		RotationMatrix R = new RotationMatrix(theta);
		//d.normalize();
		try 
		{
			d = (Matrix.multiply(R, wiggle)).toVec3();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return Vec3.scalar(f1.length(), d);
	}
}
