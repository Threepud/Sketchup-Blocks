import java.util.ArrayList;
//>EDITED
//This is a nasty work around.
//Processing cannot contains static member variables in classes that are not static themselves.
//So this class wraps the static member variable that should be in the Model class.
//What is wrong with this? Well anybody can access this and change it...
/*static class ModelIdCounter
  {
     public static int classIdCounter = 0 ;
  }*/

class Model
{
  private String id;
  private ArrayList<ModelBlock> blocks;
  
  Model()
  {
   // id = new Integer(ModelIdCounter.classIdCounter++).toString();
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