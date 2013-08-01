package sketchupblocks.database;
public class Block
{
	public enum BlockType
	{
		SMART,
		COMMAND,
		USER
	}
	
	public int blockId;
	public int [] associatedFiducials;
	public BlockType blockType;
}  