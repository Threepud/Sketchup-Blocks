package sketchupblocks.database;

import java.io.Serializable;

import sketchupblocks.math.Vec3;

public class Block implements Serializable
{
	private static final long serialVersionUID = 6282612248719679661L;
	
	public enum BlockType
	{
		SMART,
		COMMAND,
		USER
	}
	
	public int blockId;
	public int [] associatedFiducials;
	public Vec3[] fiducialCoordinates;
	public Vec3[] fiducialOrient;
	public BlockType blockType;
}  