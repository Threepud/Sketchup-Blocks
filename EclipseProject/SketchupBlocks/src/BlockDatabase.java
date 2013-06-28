import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
		smartBlockDatabaseFound = loadBlockData(smartBlockPath);
		commandBlockDatabaseFound = loadBlockData(commandBlockPath);
		userBlockDatabaseFound = loadBlockData(userBlockPath);
		
		//throw exception if no database is found
		if(!smartBlockDatabaseFound)
			throw new DataBaseNotFoundException("Smart block database not found.");
		if(!commandBlockDatabaseFound)
			throw new DataBaseNotFoundException("");
		if(!userBlockDatabaseFound)
			throw new DataBaseNotFoundException("");
	}
	
	private boolean loadBlockData(String fileName)
	{
		BufferedReader bufferedReader;
		String line;
		try 
		{
			bufferedReader = new BufferedReader(new FileReader(fileName));
			
			while((line = bufferedReader.readLine()) != null)
			{
				if(fileName.equals(smartBlockPath))
					loadSmartBlockLine(line);
				else if(fileName.equals(commandBlockPath))
					loadCommandBlockLine(line);
				else if(fileName.equals(userBlockPath))
					loadUserBlockLine(line);
			}
			
			bufferedReader.close();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return false;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private void loadSmartBlockLine(String line) throws RuntimeException
	{
		String[] elements = line.split("\t");
		
		if(elements.length != 4)
			throw new RecordFormatException("Smart block database record length exception.");
		
		SmartBlock tempBlock = new SmartBlock();
		
		//block ID
		try
		{
			tempBlock.blockId = Integer.parseInt(elements[0]);
		}
		catch(NumberFormatException e)
		{
			throw new RecordFormatException("Smart block database record block ID format exception.");
		}
		
		//Associated Fiducial IDs
		String[] stringAssociatedFiducials = elements[1].split(",");
		int[] associatedFiducials = new int[stringAssociatedFiducials.length];
		for(int x = 0; x < associatedFiducials.length; ++x)
		{
			try
			{
				associatedFiducials[x] = Integer.parseInt(stringAssociatedFiducials[x]);
			}
			catch(NumberFormatException e)
			{
				throw new RecordFormatException("Smart block database record associated fiducials format exception.");
			}
		}
		tempBlock.associatedFiducials = associatedFiducials;
		
		//Block Vertices
		String[] stringVertices = elements[2].split(",");
		float[] vertices = new float[stringVertices.length];
		for(int x = 0; x < vertices.length; ++x)
		{
			try
			{
				vertices[x] = Float.parseFloat(stringVertices[x]);
			}
			catch(NumberFormatException e)
			{
				throw new RecordFormatException("Smart block database record block vertices format exception.");
			}
		}
		tempBlock.vertices = vertices;
		
		//Block indices
		String[] stringIndices = elements[3].split(",");
		int[] indices = new int[stringIndices.length];
		for(int x = 0; x < indices.length; ++x)
		{
			try
			{
				indices[x] = Integer.parseInt(stringIndices[x]);
			}
			catch(NumberFormatException e)
			{
				throw new RecordFormatException("Smart block database record block indices format exception.");
			}
		}
		tempBlock.indices = indices;
		
		//add block to hash for all associated fiducials
		for(int x = 0; x < associatedFiducials.length; ++x)
		{
			blocks.put(new Integer(associatedFiducials[x]), tempBlock);
		}
	}
	
	private void loadCommandBlockLine(String line) throws RuntimeException
	{
		String[] elements = line.split("\t");
		
		if(elements.length != 3)
			throw new RecordFormatException("Command block database record length exception.");
		
		CommandBlock tempBlock = new CommandBlock();
		
		//block ID
		try
		{
			tempBlock.blockId = Integer.parseInt(elements[0]);
		}
		catch(NumberFormatException e)
		{
			throw new RecordFormatException("Command block database record block ID format exception.");
		}
		
		//Associated Fiducial IDs
		String[] stringAssociatedFiducials = elements[1].split(",");
		int[] associatedFiducials = new int[stringAssociatedFiducials.length];
		for(int x = 0; x < associatedFiducials.length; ++x)
		{
			try
			{
				associatedFiducials[x] = Integer.parseInt(stringAssociatedFiducials[x]);
			}
			catch(NumberFormatException e)
			{
				throw new RecordFormatException("Command block database record associated fiducials format exception.");
			}
		}
		tempBlock.associatedFiducials = associatedFiducials;
		
		//command block type
		CommandBlock.CommandType commandType;
		switch(elements[2])
		{
			case "NEW":
				commandType = CommandBlock.CommandType.NEW;
				break;
			case "SAVE":
				commandType = CommandBlock.CommandType.SAVE;
				break;
			case "LOAD":
				commandType = CommandBlock.CommandType.LOAD;
				break;
			case "EXPORT":
				commandType = CommandBlock.CommandType.EXPORT;
				break;
			case "SPECTATE":
				commandType = CommandBlock.CommandType.SPECTATE;
				break;
			default:
				throw new RecordFormatException("Command block database record command type exception.");
		}
		tempBlock.type = commandType;
		
		//add block to hash for all associated fiducials
		for(int x = 0; x < associatedFiducials.length; ++x)
		{
			blocks.put(new Integer(associatedFiducials[x]), tempBlock);
		}
	}
	
	//TODO: validate user information
	private void loadUserBlockLine(String line) throws RuntimeException
	{
		String[] elements = line.split("\t");
		
		if(elements.length != 5)
			throw new RecordFormatException("User block database record length exception.");
		
		UserBlock tempBlock = new UserBlock();
		
		//block ID
		try
		{
			tempBlock.blockId = Integer.parseInt(elements[0]);
		}
		catch(NumberFormatException e)
		{
			throw new RecordFormatException("User block database record block ID format exception.");
		}
		
		//Associated Fiducial IDs
		String[] stringAssociatedFiducials = elements[1].split(",");
		int[] associatedFiducials = new int[stringAssociatedFiducials.length];
		for(int x = 0; x < associatedFiducials.length; ++x)
		{
			try
			{
				associatedFiducials[x] = Integer.parseInt(stringAssociatedFiducials[x]);
			}
			catch(NumberFormatException e)
			{
				throw new RecordFormatException("User block database record associated fiducials format exception.");
			}
		}
		tempBlock.associatedFiducials = associatedFiducials;
		
		//user name
		tempBlock.name = elements[2];
		
		//user address
		tempBlock.address = elements[3];
		
		//user picture path
		tempBlock.picturePath = elements[4];
		
		//add block to hash for all associated fiducials
		for(int x = 0; x < associatedFiducials.length; ++x)
		{
			blocks.put(new Integer(associatedFiducials[x]), tempBlock);
		}
	}
	
	/*
	private boolean saveSmartBlockData(String fileName)
	{
		return true;
	}
	
	private boolean saveCommandBlockData(String fileName)
	{
		return true;
	}
	
	private boolean saveUserBlockData(String fileName)
	{
		return true;
	}
	*/
	
	public void insertBlock(Block block)
	{
	   
	}
	   
	public Block findBlock(int fiducialID)
	{
		return null;
	} 
	
	public boolean saveDatabase()
	{
		return false;
	}
}