package sketchupblocks.construction;

import java.util.ArrayList;

import sketchupblocks.base.Logger;
import sketchupblocks.base.RuntimeData;
import sketchupblocks.base.Settings;
import sketchupblocks.math.LineDirectionSolver;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Line;
import sketchupblocks.math.RotationMatrix3D;
import sketchupblocks.math.SingularMatrixException;
import sketchupblocks.math.Vec3;

/**
 * Calculates the transformation from Model space to World space
 * @author Neoin
 * @author Elre
 */
public class ModelTransformationCalculator 
{
	public static Matrix[] getModelTransformationMatrix(BlockInfo.Fiducial[] fids, Vec3[] positions, Vec3[] fidCoordsM, Vec3[] fidUpM)
	{
		
		if (fidCoordsM.length > 2)
		{	
			Matrix[] transformMatrices = TransformationCalculator.calculateTransformationMatrices(fidCoordsM, positions);
			Logger.log("ROTATION: "+transformMatrices[0], 60);
			Logger.log("TRANSLATION: "+transformMatrices[1], 60);
			
			Matrix transform = Matrix.multiply(transformMatrices[1], transformMatrices[0].padMatrix());
			
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for (int k = 0; k < fids.length; k++)
			{
				if (ids.indexOf(fids[k].fiducialsID) == -1)
				{
					ids.add(fids[k].fiducialsID);
				}
				
			}

			if (ids.size() == 2)
			{
				Vec3[] axisEnds = new Vec3[]{new Vec3(), new Vec3()};
				int[] counts = new int[2];
				int firstID = ids.get(0);
				
				for (int k = 0; k < fids.length; k++)
				{
					if (fids[k].fiducialsID == firstID)
					{
						axisEnds[0] = Vec3.add(axisEnds[0], fids[k].worldPosition);
						counts[0] += 1;
					}
					else
					{
						axisEnds[1] = Vec3.add(axisEnds[1], fids[k].worldPosition);
						counts[1] += 1;
					}
				}
				axisEnds[0] = Vec3.scalar(1.0/counts[0], axisEnds[0]);
				axisEnds[1] = Vec3.scalar(1.0/counts[1], axisEnds[1]);
				Vec3 axis = Vec3.subtract(axisEnds[0], axisEnds[1]);
				axis.normalize();
				
				Matrix rot = Matrix.identity(3);
				Matrix rotTest = Matrix.identity(3);
				
				for (int k = 0; k < positions.length; k++)
				{ 
					
					Vec3 up = getUpVector(fids[k].camID);
					
					//double angle = Math.acos(Vec3.dot(ra, fidUpAxis));
					double angle = GetError(transformMatrices[0],rot,fidUpM[k],fidCoordsM[k],positions[k],axis,up,fids[k].rotation);
					rot = Matrix.multiply(new RotationMatrix3D(axis, angle), rot);
					
					double newAngle = GetError(transformMatrices[0],rot,fidUpM[k],fidCoordsM[k],positions[k],axis,up,fids[k].rotation);
					rotTest = Matrix.multiply(new RotationMatrix3D(axis, -angle), rotTest);
					
					double newTestAngle = GetError(transformMatrices[0],rotTest,fidUpM[k],fidCoordsM[k],positions[k],axis,up,fids[k].rotation);
					
					Vec3 fidNormal = Matrix.multiply(rot, Matrix.multiply(transformMatrices[0], Vec3.normalize(fidCoordsM[k])));
					RuntimeData.outputLines.add(new Line(positions[k], Vec3.normalize(Matrix.multiply(new RotationMatrix3D(fidNormal, fids[k].rotation), new Vec3(0,0,1)))));
	
					if (newTestAngle < newAngle)
					{
						rot = rotTest;
					}
					
					transform = Matrix.multiply(transformMatrices[1], Matrix.multiply(rot, transformMatrices[0]).padMatrix());
					
				}
				return new Matrix[]{transform, Matrix.multiply(transformMatrices[1],  transformMatrices[0].padMatrix())};
			}
			/*RuntimeData.outputLines.clear();
			for (int k = 0; k < positions.length; k++)
			{
				Vec3 up = getUpVector(fids[k].camID);
				Vec3 fidNormal = Matrix.multiply(transformMatrices[0], Vec3.normalize(fidCoordsM[k]));
				RuntimeData.outputLines.add(new Line(positions[k], Vec3.normalize(Matrix.multiply(new RotationMatrix3D(fidNormal, fids[k].rotation), /*new Vec3(0,0,1)up))));
			
			}*/
			
			return new Matrix[]{transform, Matrix.multiply(transformMatrices[1], transformMatrices[0].padMatrix())};
		}
		throw new RuntimeException("Invalid number of fiducials observed to calculate position "+(fidCoordsM.length == positions.length));
	}
	
	static private double GetError(Matrix transform, Matrix rot ,Vec3 fidMUp,Vec3 fidM , Vec3 fidW, Vec3 axis,Vec3 cameraUp, double rotation)
	{
		Vec3 fidUpW = Matrix.multiply(rot, Matrix.multiply(transform, fidMUp));
		Vec3 fidNormal = Matrix.multiply(rot, Matrix.multiply(transform, Vec3.normalize(fidM)));
		fidNormal.normalize();
		
		Line[] fidNormBasis = getPlaneBasis(new Line(fidW, fidNormal));
		cameraUp = cameraUp.project(fidNormBasis[0].direction,fidNormBasis[1].direction);
		
		Vec3 rn = Vec3.normalize(Matrix.multiply(new RotationMatrix3D(fidNormal, rotation), cameraUp));
		Line[] axisBasis = getPlaneBasis(new Line(fidW, axis));
		Vec3 ra = rn.project(axisBasis[0].direction, axisBasis[1].direction);
		ra.normalize();
		
		Vec3 fidUpAxis = (fidUpW).project(axisBasis[0].direction, axisBasis[1].direction);
		fidUpAxis.normalize();
		
		double dotProd = Vec3.dot(ra, fidUpAxis);
		if (dotProd > 1)
			dotProd = 1;
		else if (dotProd < -1)
			dotProd = -1;
		return Math.acos(dotProd);	
		
	}
	
	private static Line[] getPlaneBasis(Line line)
	{
		double xop = 1;
		double yop = 1;
		double zop = 1;
		if (line.direction.z != 0)
		{
			zop = (line.direction.dot(line.point) - xop*line.direction.x - yop*line.direction.y)/line.direction.z;
		}
		else if (line.direction.y != 0)
		{
			yop = (line.direction.dot(line.point) - xop*line.direction.x - zop*line.direction.z)/line.direction.y;
		}
		else if (line.direction.x != 0)
		{
			xop = (line.direction.dot(line.point) - zop*line.direction.z - yop*line.direction.y)/line.direction.x;
		}
		Vec3 pop = new Vec3(xop, yop , zop);
		Vec3 vop = Vec3.subtract(pop, line.point);
		Vec3 vop2 = Vec3.cross(vop, line.direction);
		return new Line[]{new Line(line.point, vop), new Line(line.point, vop2)};
	}
	
	private static Vec3 getUpVector(int camID)
    {
            Vec3[] landmarkToCamera = new Vec3[4];
            for (int k = 0; k < 4; k++)
            {
                    landmarkToCamera[k] = Vec3.subtract(RuntimeData.getCameraPosition(camID), Settings.landmarks[k]);
            }
            
            double[] angles = new double[4];
            for (int k = 0; k < 4; k++)
            {
                    angles[k] = RuntimeData.getAngle(camID, k, 0.5, 0.5+0.01);
            }
            // Do calculation 
            Vec3 lineDirection;
			try {
				lineDirection = LineDirectionSolver.solve(landmarkToCamera, angles);
			} catch (SingularMatrixException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new Vec3(0,0,1);
			}
            Line top = new Line(RuntimeData.getCameraPosition(camID), lineDirection);
            
            angles = new double[4];
            for (int k = 0; k < 4; k++)
            {
                    angles[k] = RuntimeData.getAngle(camID, k, 0.5, 0.5-0.01);
            }
            // Do calculation 
            try {
				lineDirection = LineDirectionSolver.solve(landmarkToCamera, angles);
			} catch (SingularMatrixException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new Vec3(0,0,1);
			}
            Line bottom = new Line(RuntimeData.getCameraPosition(camID), lineDirection);
            
            top.direction.normalize();
            bottom.direction.normalize();
            
            return Vec3.subtract(top.direction, bottom.direction);
    }
}


/*
					Vec3 fidUpW = Matrix.multiply(rot, Matrix.multiply(transformMatrices[0], fidUpM[k]));
					Vec3 fidNormal = Matrix.multiply(rot, Matrix.multiply(transformMatrices[0], Vec3.normalize(fidCoordsM[k])));
					
					Vec3 rn = Vec3.normalize(Matrix.multiply(new RotationMatrix3D(fidNormal, fids[k].rotation), new Vec3(0,0,1)));
					Line[] axisBasis = getPlaneBasis(new Line(positions[k], axis));
					Vec3 ra = rn.project(axisBasis[0].direction, axisBasis[1].direction);
					Vec3 fidUpAxis = (fidUpW).project(axisBasis[0].direction, axisBasis[1].direction);
 */

//Vec3 up = getUpVector(fids[k].camID);
//Vec3 camPos = fids[k].getLine().point;
//RotationMatrix3D rotUp = new RotationMatrix3D(RuntimeData.getCameraViewVector(fids[k].camID), fids[k].rotation);
//Vec3 r = Matrix.multiply(rotUp, up);
//Project r onto basis of plane perpendicular to line b/w fiducial & camera.
/*Line[] camFidBasis = getPlaneBasis(fids[k].getLine());
Vec3 rf = r.project(camFidBasis[0].direction, camFidBasis[1].direction);
Vec3 wfn = Matrix.multiply(rot, fidNormal); //World fiducial Normal
wfn.normalize();

Line[] fidNormBasis = getPlaneBasis(new Line(positions[k], wfn));
Vec3 rn = rf.project(fidNormBasis[0].direction, fidNormBasis[1].direction);
rn.normalize();
fidUpWPre.normalize();*/