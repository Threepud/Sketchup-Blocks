package sketchupblocks.construction;

import sketchupblocks.base.Logger;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;

public class ModelTransformationCalculator 
{
	public static Matrix getModelTransformationMatrix(SmartBlock sBlock, Vec3[] positions, Vec3[] fidCoordsM)
	{
		if (fidCoordsM.length > 2)
		{	
			Matrix[] transformMatrices = TransformationCalculator.calculateTransformationMatrices(fidCoordsM, positions);
			Logger.log("ROTATION: "+transformMatrices[0], 60);
			Logger.log("TRANSLATION: "+transformMatrices[1], 60);
			
			return Matrix.multiply(transformMatrices[1], transformMatrices[0].padMatrix());
		}
		throw new RuntimeException("Invalid number of fiducials observed to calculate position "+(fidCoordsM.length == positions.length));
	}
}
