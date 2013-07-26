class BlockDatabase
{
  private ArrayList<Block> blocks;
  
  BlockDatabase()
  {
    //try to deserialize the block database
    FileInputStream fileInputSteam = new FileInputStream(dataPath("blockDB.dat"));
    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
    blocks = (ArrayList)objectInputStream.readObject();
    objectInputStream.close();
    
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
