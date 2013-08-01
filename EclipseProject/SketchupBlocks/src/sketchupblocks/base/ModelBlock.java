package sketchupblocks.base;

import sketchupblocks.database.SmartBlock;

public class ModelBlock
{
	public enum ChangeType
	{
		ADD,
		UPDATE,
		DELETE
	}
	
	public SmartBlock smartBlock;
	public float[][] world;
	public ChangeType type;
}