import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

class BlockDatabase
{
	private ArrayList<Block> blocks;
	  
	BlockDatabase()
	{
		//try to deserialize the block database
		FileInputStream fileInputSteam;
		ObjectInputStream objectInputStream;
		try 
		{
			fileInputSteam = new FileInputStream(new File("blockDB.dat"));
			objectInputStream = new ObjectInputStream(fileInputSteam);
			blocks = (ArrayList)objectInputStream.readObject();
			objectInputStream.close();
		}
		catch (FileNotFoundException e) 
		{
			System.out.println(e);
		}
		
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