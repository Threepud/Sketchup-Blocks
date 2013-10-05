package sketchupblocks.construction;

import sketchupblocks.base.Logger;
import sketchupblocks.calibrator.EvalResults;
import sketchupblocks.calibrator.Evaluator;
import sketchupblocks.calibrator.*;
import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.*;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.RotationMatrix4D;
import sketchupblocks.math.Matrix.Axis;

public class PSOPosition {
	
	static class PSORot implements Evaluator
	{
		Matrix translation;
		Vec4 [] modelPositions;
		Vec3 [] worldPostitions;
		
		public PSORot(Matrix _translation, Vec3 [] _modelPositions, Vec3 [] _worldPostitions)
		{
			if(_modelPositions.length != _worldPostitions.length)
				throw new RuntimeException("Array lengths do not match");
			translation = _translation;
			modelPositions = new Vec4[_modelPositions.length];
			for(int k = 0 ; k < modelPositions.length ;k++)
				modelPositions[k] = _modelPositions[k].padVec3();
			worldPostitions = _worldPostitions;
		}

		public EvalResults [] evaluate(Particle [] p)
		{
			EvalResults [] result = new EvalResults[p.length];
			for(int k = 0 ; k < p.length ; k++)
				result[k] = evaluate(p[k]);
				
			return result;
		}
		
		public EvalResults evaluate(Particle  p)
		{
			EvalResults result = evaluate(p.attributes);
			p.currentScore(result.score);
			return result;
		}
		
		
		public EvalResults [] evaluate(double [] [] p)
		{
			EvalResults [] result = new EvalResults[p.length];
			for(int k = 0 ; k < p.length ; k++)
				result[k] = evaluate(p[k]);
				
			return result;		
		}
		
		public EvalResults evaluate(double [] pa)
		{
			if(pa.length != 3)
				throw new RuntimeException("Particle length should be 3, one for the rotation in each axis");
			EvalResults result = new EvalResults();

	       	Matrix mRot = getRotationMatrix(pa);
	       	Matrix total = getTotalMatrix(mRot,translation) ;
	       	
	       	Vec3 [] PSOworldPostitions = new Vec3 [modelPositions.length];
	       	for(int k = 0 ; k < modelPositions.length ;k++)
	       	{
	       		PSOworldPostitions[k] = Matrix.multiply(total,  modelPositions[k]).toVec3();
	       	}
	       	
	       	double error = 1;
	       	for(int k = 0 ; k < worldPostitions.length ;k++)
	       	{
	       		double temp = worldPostitions[k].distance(PSOworldPostitions[k]);
	       		error += temp*temp;
	       	}
	       	
			result.score  = 1000/(error);
			return result;
		}
		
	
	}

	
	public static Matrix getRotationMatrix(double [] pa)
	{
		return new RotationMatrix4D(pa[0], pa[1], pa[2]);
	}
	
	public static Matrix getTotalMatrix(Matrix rotation , Matrix translation)
	{
		return Matrix.multiply(translation, rotation);	
	}
	
	public static Matrix getModelTransformationMatrix(Vec3 [] rotation, SmartBlock sBlock, Vec3[] positions, Integer[] fidIDs)
	{
		if( positions.length < 3 )
			throw new RuntimeException("Too few positions to calculate");
		
		Vec3[] modelFidCoords = new Vec3[positions.length];
		for (int k = 0; k < modelFidCoords.length; k++)
		{
			modelFidCoords[k] = sBlock.fiducialCoordinates[fidIDs[k]];
		}
				
		Vec3 modelCenter = calculateCentroid(modelFidCoords);
		Vec3 worlCenter = calculateCentroid(positions);
		Matrix trantsMat = getTranslationMatrix(modelCenter,worlCenter);
		
		ParticleSystemSettings settings = new ParticleSystemSettings();
		settings.eval = new PSORot(trantsMat,modelFidCoords,positions);
		settings.tester = null;
		settings.creator = new ParticleCreator(3, -10, 10);
		
		settings.iterationCount = 5000;
		settings.particleCount = 50;
		settings.ringTopology = true;
		settings.ringSize = 1;
		
		settings.socialStart = 0.72;
		settings.cognitiveStart = 0.72;
		settings.momentum = 1.4;
		settings.MaxComponentVelocity = 0.1;
		
		
		ParticleSystem ps = new ParticleSystem(settings);
		Particle best = ps.go();
		
		Matrix rot = getRotationMatrix(best.bestPosition);
		trantsMat = getTranslationMatrix(Matrix.multiply(rot, modelCenter.padVec3()).toVec3(),worlCenter);
		
		
		return getTotalMatrix( rot, trantsMat);//getRotationMatrix(best.bestPosition)
	}
	
	protected static Matrix getTranslationMatrix(Vec3 one, Vec3 two)
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
	
	private static Vec3 calculateCentroid(Vec3[] points)
    {
		
        double[] center = new double[3];
        for (int k = 0; k < points.length; k++)
        {
            center[0] += points[k].x;
            center[1] += points[k].y;
            center[2] += points[k].z;
        }
        
        
        Vec3 res = new Vec3(center);
        res = Vec3.scalar(1.0/points.length, res);
        return res;
    }
	
}
