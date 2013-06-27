import java.util.ArrayList;

class Model
{
  private  volatile int classIdCounter = 0 ;
  private String id;
  private ArrayList<ModelBlock> blocks;
  
  Model()
  {
    id = classIdCounter++ +"";
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
