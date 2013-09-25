package sketchupblocks.construction;

import java.io.Serializable;
import java.util.HashMap;

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
	public ChangeType type;
	public Line [] debugLines = new Line[0];
	public Vec3 [] debugPoints = new Vec3[0];
	
	public ModelBlock()
	{
		
	}
	
	public ModelBlock(SmartBlock _smartBlock, Matrix _transformMatrix, ChangeType _type)
	{
		smartBlock = _smartBlock;
		transformationMatrix = _transformMatrix;
		type = _type;
	}
}