package sketchupblocks.database;

import sketchupblocks.math.Vec3;

public class SmartBlock extends Block
{
	public String name;
	public Vec3[] vertices;
	public int[] indices;
	public Vec3[] fiducialCoordinates;

	public boolean areOppositeFidicials(int fiducialID, int fiducialID2) {
		// TODO Auto-generated method stub
		return false;
	}
}