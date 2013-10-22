package sketchupblocks.base;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import sketchupblocks.construction.ModelBlock;

/**
 * The Model class holds all the current
 * model blocks in the model seen on the
 * construction floor.
 */
public class Model
{
	private String id;
	private ConcurrentHashMap<Integer,ModelBlock> blockMap;
	public static int classIdCounter = 0 ;
	  
	/**
	 * The constructor of the Mode class assigns
	 * a unique ID to the model and initializes member
	 * variables.
	 */
	public Model()
	{
		id = new Integer(classIdCounter++).toString();
		blockMap = new ConcurrentHashMap<Integer,ModelBlock>();
	}
	
	/**
	 * This constructor is a copy constructor.
	 * @param oldModel Old model to copy
	 */
	public Model(Model oldModel)
	{
		id = new String(oldModel.id);
		
		blockMap = new ConcurrentHashMap<>();
		blockMap.putAll(oldModel.blockMap);
	}
	  
	/**
	 * This function adds a new model block if it does not
	 * exist in the model yet or updates the model block
	 * if the model block already exists.
	 * @param modelBlock
	 */
	public void addModelBlock(ModelBlock modelBlock)
	{
		blockMap.put(new Integer(modelBlock.smartBlock.blockId),modelBlock);
	}
	
	/**
	 * This function removes the given model block
	 * from the model.
	 * @param modelBlock Model block to be removed.
	 */
	public void removeModelBlock(ModelBlock modelBlock)
	{
		blockMap.remove(new Integer(modelBlock.smartBlock.blockId));
	}
	
	/**
	 * This function returns the model class's
	 * unique identifier.
	 * @return Unique Identifier.
	 */
	public String getId()
	{
		return id; 
	}
	
	/**
	 * This function returns all the model
	 * blocks currently in the model.
	 * @return List of model blocks.
	 */
	public Collection<ModelBlock> getBlocks()
	{
		return blockMap.values() ;
	}
	
	/**
	 * This function returns a model block
	 * with a matching block ID to the ID provided
	 * as argument.
	 * @param id Model block ID.
	 * @return	Model block.
	 */
	public ModelBlock getBlockById(Integer id)
	{
		return blockMap.get(id);
	}
}