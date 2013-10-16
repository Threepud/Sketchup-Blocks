package sketchupblocks.construction;

import java.util.ArrayList;

import sketchupblocks.base.Logger;
import sketchupblocks.base.RuntimeData;
import sketchupblocks.base.Settings;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.LineDirectionSolver;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Line;
import sketchupblocks.math.RotationMatrix3D;
import sketchupblocks.math.SingularMatrixException;
import sketchupblocks.math.Vec3;

public class ModelTransformationCalculator 
{
	public static Matrix getModelTransformationMatrix(BlockInfo.Fiducial[] fids, Vec3[] positions, Vec3[] fidCoordsM, Vec3[] fidUpM, int numUniqueFids)
	{
		
		if (fidCoordsM.length > 2)
		{	
			Matrix[] transformMatrices = TransformationCalculator.calculateTransformationMatrices(fidCoordsM, positions);
			Logger.log("ROTATION: "+transformMatrices[0], 60);
			Logger.log("TRANSLATION: "+transformMatrices[1], 60);
			
			Matrix transform = Matrix.multiply(transformMatrices[1], transformMatrices[0].padMatrix());
			
			if (numUniqueFids == 2)
			{
				//Find unique points:
				ArrayList<Integer> uniqueIndices =  new ArrayList<Integer>();
				ArrayList<Integer> ids = new ArrayList<Integer>();
				for (int k = 0; k < fids.length; k++)
				{
					if (!ids.contains(fids[k].fiducialsID))
					{
						uniqueIndices.add(k);
						ids.add(fids[k].fiducialsID);
						
					}
				}
				
				if(uniqueIndices.size() != 2)
					throw new RuntimeException("Too many unique fiducials!");
				
				Vec3 axis = Vec3.subtract(positions[uniqueIndices.get(0)], positions[uniqueIndices.get(1)]);
				axis.normalize();
				
				System.out.println("Code has been invoked!");
				Matrix rot = Matrix.identity(3);
				for (int k = 0; k < positions.length; k++)
				{ 
					Vec3 up = getUpVector(fids[k].camID);
					Vec3 camPos = fids[k].getLine().point;
					RotationMatrix3D rotUp = new RotationMatrix3D(RuntimeData.getCameraViewVector(fids[k].camID), fids[k].rotation);
					Vec3 r = Matrix.multiply(rotUp, up);
					//Project r onto basis of plane perpendicular to line b/w fiducial & camera.
					Line[] camFidBasis = getPlaneBasis(fids[k].getLine());
					Vec3 rf = r.project(camFidBasis[0].direction, camFidBasis[1].direction);
					Vec3 wfn = Matrix.multiply(rot, Matrix.multiply(transformMatrices[0], Vec3.normalize(fidCoordsM[k]))); //World fiducial Normal
					wfn.normalize();
					
					Line[] fidNormBasis = getPlaneBasis(new Line(wfn, positions[k]));
					Vec3 rn = rf.project(fidNormBasis[0].direction, fidNormBasis[1].direction);
					Vec3 fidUpWPre = Matrix.multiply(rot, Matrix.multiply(transformMatrices[0], fidUpM[k]));
					rn.normalize();
					fidUpWPre.normalize();
					
					Line[] axisBasis = getPlaneBasis(new Line(axis, positions[k]));
					Vec3 ra = rn.project(axisBasis[0].direction, axisBasis[1].direction);
					Vec3 fidUpAxis = fidUpWPre.project(axisBasis[0].direction, axisBasis[1].direction);
					
					double angle = Math.acos(Vec3.dot(ra, fidUpAxis));
					
					rot = Matrix.multiply(new RotationMatrix3D(axis, angle), rot);
					
					RuntimeData.outputLines.add(new Line(camPos, up));
					
					//transform = Matrix.multiply(transformMatrices[1], Matrix.multiply(transformMatrices[0], rot).padMatrix());
					
				}
			}
			
			return transform;
		}
		throw new RuntimeException("Invalid number of fiducials observed to calculate position "+(fidCoordsM.length == positions.length));
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
