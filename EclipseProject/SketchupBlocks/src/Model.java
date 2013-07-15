import java.util.ArrayList;


class Model
{
	private String id;
	private ArrayList<ModelBlock> blocks;
	public static int classIdCounter = 0 ;
	  
	Model()
	{
		id = new Integer(classIdCounter++).toString();
	}
	  
	void addModelBlock(ModelBlock modelBlock)
	{
		blocks.add(modelBlock);
	}
	  
	public String getId()
	{
		return id; 
	}
}