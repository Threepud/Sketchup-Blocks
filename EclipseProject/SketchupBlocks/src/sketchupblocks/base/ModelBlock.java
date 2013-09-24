package sketchupblocks.base;

import java.io.Serializable;

import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.Matrix;

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