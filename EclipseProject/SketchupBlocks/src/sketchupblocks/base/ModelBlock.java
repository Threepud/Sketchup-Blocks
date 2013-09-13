package sketchupblocks.base;

import sketchupblocks.database.SmartBlock;
import sketchupblocks.math.Matrix;

public class ModelBlock
{
	public enum ChangeType
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