package sketchupblocks.database;

import java.io.Serializable;

import sketchupblocks.math.Vec3;

public class SmartBlock extends Block implements Serializable
{
	private static final long serialVersionUID = -9059999728154223860L;
	
	public String name;
	public Vec3[] vertices;
	public int[] indices;
}