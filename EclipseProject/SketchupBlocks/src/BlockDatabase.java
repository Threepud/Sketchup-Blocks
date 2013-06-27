import java.util.HashMap;

class BlockDatabase
{
	private HashMap<Integer, Block> blocks;
	private String smartBlockPath;
	private String commandBlockPath;
	private String userBlockPath;
	  
	BlockDatabase(String _smartBlockPath, String _commandBlockPath, String _userBlockPath)
	{
		smartBlockPath = _smartBlockPath;
		commandBlockPath = _commandBlockPath;
		userBlockPath = _userBlockPath;
		
		blocks = new HashMap<>();
		loadBlockDatabases();
	}
	  
	private void loadBlockDatabases() throws RuntimeException
	{
		boolean smartBlockDatabaseFound = false;
		boolean commandBlockDatabaseFound = false;
		boolean userBlockDatabaseFound = false;
		
		//load all block data into hash map
		smartBlockDatabaseFound = loadSmartBlockData(smartBlockPath);
		commandBlockDatabaseFound = loadCommandBlockData(commandBlockPath);
		userBlockDatabaseFound = loadUserBlockData(userBlockPath);
		
		//throw exception if no database is found
		if(!smartBlockDatabaseFound)
			throw new DataBaseNotFoundException("Smart block database not found.");
		if(!commandBlockDatabaseFound)
			throw new DataBaseNotFoundException("");
		if(!userBlockDatabaseFound)
			throw new DataBaseNotFoundException("");
	}
	
	private boolean loadSmartBlockData(String fileName)
	{
		
		
		return false;
	}
	
	private boolean loadCommandBlockData(String fileName)
	{
		return false;
	}
	
	private boolean loadUserBlockData(String fileName)
	{
		return false;
	}
	
	private boolean saveSmartBlockData(String fileName)
	{
		return false;
	}
	
	private boolean saveCommandBlockData(String fileName)
	{
		return false;
	}
	
	private boolean saveUserBlockData(String fileName)
	{
		return false;
	}
	
	public void insertBlock(Block block)
	{
	   
	}
	   
	public Block findBlock(int fiducialID)
	{
		return null;
	} 
	
	public boolean saveDatabase()
	{
		saveSmartBlockData(smartBlockPath);
		saveCommandBlockData(commandBlockPath);
		saveUserBlockData(userBlockPath);
		
		return false;
	}
}