package sketchupblocks.database;

import java.io.Serializable;

import sketchupblocks.math.Vec3;

public class SmartBlock extends Block implements Serializable
{
	public String name;
	public Vec3[] vertices;
	public int[] indices;

	public boolean areOppositeFidicials(int fiducialID, int fiducialID2) 
	{
		return false;
	}
}