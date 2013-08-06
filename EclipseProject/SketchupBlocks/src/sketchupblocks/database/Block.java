package sketchupblocks.database;

import sketchupblocks.math.Vec3;

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
	public Vec3[] fiducialCoordinates;
	public BlockType blockType;
}  