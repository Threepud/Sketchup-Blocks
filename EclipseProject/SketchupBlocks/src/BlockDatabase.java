import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
//>EDITED
class BlockDatabase
{
  private ArrayList<Block> blocks;
  
  BlockDatabase()
  {
    //try to deserialize the block database
   /* FileInputStream fileInputSteam = new FileInputStream(new File("blockDB.dat"));
    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputSteam);
    blocks = (ArrayList)objectInputStream.readObject();
    objectInputStream.close();*/
    
    //throw exception if no database is found
  }
  
  public void insertBlock(Block block)
  {
   
  }
   
  public Block findBlock(int fiducialID)
  {
    return null;
  } 
}