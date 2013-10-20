package sketchupblocks.construction;

import java.io.Serializable;
import java.util.ArrayList;

import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.Line;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;

public class ModelBlock implements Serializable
{
	private static final long serialVersionUID = -4643734394368790618L;

	public enum ChangeType implements Serializable
	{
		UPDATE,
		REMOVE
	}
	
	public SmartBlock smartBlock;
	public Matrix transformationMatrix;
	public Matrix rawMatrix;
	public Matrix mooingMatrix;
	public ChangeType type;
	public Line [] debugLines = new Line[0];
	public Vec3 [] debugPoints = new Vec3[0];
	public ArrayList<ModelBlock> blocksAbove = new ArrayList<ModelBlock>();
	
	public ModelBlock()
	{
		
	}
	
	public ModelBlock(SmartBlock _smartBlock, Matrix _transformMatrix, ChangeType _type)
	{
		smartBlock = _smartBlock;
		transformationMatrix = _transformMatrix;
		if (_transformMatrix != null)
			rawMatrix = _transformMatrix.clone();
		else 
		{
			rawMatrix = null; 
			mooingMatrix = Matrix.identity(4);
		}
		type = _type;
	}
	
	public ModelBlock clone()
	{
		ModelBlock dolly = new ModelBlock();
		dolly.smartBlock = smartBlock;
		dolly.transformationMatrix = transformationMatrix.clone();
		dolly.mooingMatrix = mooingMatrix.clone();
		dolly.rawMatrix = transformationMatrix.clone();
		dolly.type = type;
		dolly.debugLines = new Line[debugLines.length];
		for (int k = 0; k < debugLines.length; k++)
		{
			Vec3 point = new Vec3(debugLines[k].point.x ,debugLines[k].point.y, debugLines[k].point.z);
			Vec3 direction = new Vec3(debugLines[k].direction.x, debugLines[k].direction.y, debugLines[k].direction.z);
			dolly.debugLines[k] = new Line(point, direction);
		}
		
		dolly.debugPoints = new Vec3[debugPoints.length];
		for (int k = 0; k < debugPoints.length; k++)
		{
			Vec3 point = new Vec3(debugPoints[k].x ,debugPoints[k].y, debugPoints[k].z);
			dolly.debugPoints[k] = point;
		}
		return dolly;
	}
}