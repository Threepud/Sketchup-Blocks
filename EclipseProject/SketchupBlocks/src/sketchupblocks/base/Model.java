package sketchupblocks.base;

import java.util.HashMap;
import java.util.Collection;

import sketchupblocks.construction.ModelBlock;

public class Model
{
	private String id;
	private HashMap<Integer,ModelBlock> blockMap;
	public static int classIdCounter = 0 ;
	  
	public Model()
	{
		id = new Integer(classIdCounter++).toString();
		blockMap = new HashMap<Integer,ModelBlock>();
	}
	
	public Model(Model oldModel)
	{
		id = new String(oldModel.id);
		
		blockMap = new HashMap<>();
		blockMap.putAll(oldModel.blockMap);
	}
	  
	public void addModelBlock(ModelBlock modelBlock)
	{
		blockMap.put(new Integer(modelBlock.smartBlock.blockId),modelBlock);
	}
	
	public void removeModelBlock(ModelBlock modelBlock)
	{
		blockMap.remove(new Integer(modelBlock.smartBlock.blockId));
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