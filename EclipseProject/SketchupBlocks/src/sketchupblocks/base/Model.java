package sketchupblocks.base;

import java.util.HashMap;
import java.util.Collection;

public class Model
{
	private String id;
	private HashMap<Integer,ModelBlock> blockMap;
	public static int classIdCounter = 0 ;
	  
	Model()
	{
		id = new Integer(classIdCounter++).toString();
		blockMap = new HashMap<Integer,ModelBlock>();
	}
	  
	public void addModelBlock(ModelBlock modelBlock)
	{
		blockMap.put(new Integer(modelBlock.smartBlock.blockId),modelBlock);
	}
	  
	public String getId()
	{
		return id; 
	}
	
	public Collection<ModelBlock> getBlocks()
	{
		return blockMap.values() ;
	}
	
	public ModelBlock getBlockById(Integer id)
	{
		return blockMap.get(id);
	}
}