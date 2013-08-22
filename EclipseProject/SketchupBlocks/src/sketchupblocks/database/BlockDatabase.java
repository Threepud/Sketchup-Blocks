package sketchupblocks.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import sketchupblocks.base.ColladaLoader;
import sketchupblocks.base.CommandBlock;
import sketchupblocks.exception.DataBaseNotFoundException;
import sketchupblocks.exception.RecordFormatException;
import sketchupblocks.exception.UnknownBlockTypeException;
import sketchupblocks.math.Vec3;

public class BlockDatabase
{
	private HashMap<Integer, Block> blocks;
	private String smartBlockPath;
	private String commandBlockPath;
	private String userBlockPath;
	
	public BlockDatabase(String _smartBlockPath, String _commandBlockPath, String _userBlockPath) throws Exception
	{
		smartBlockPath = _smartBlockPath;
		commandBlockPath = _commandBlockPath;
		userBlockPath = _userBlockPath;
		
		blocks = new HashMap<>();
		loadBlockDatabases();
	}
	  
	private void loadBlockDatabases() throws Exception
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
			throw new DataBaseNotFoundException("Command block database not found.");
		if(!userBlockDatabaseFound)
			throw new DataBaseNotFoundException("User block database not found.");
	}
	
	private boolean loadBlockData(String fileName) throws Exception
	{
		BufferedReader bufferedReader;
		String line;
		try 
		{
			bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
			
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
	
	private void loadSmartBlockLine(String line) throws Exception
	{
		int index = 0;
		String[] elements = line.split("\t");
		
		if(elements.length != 5)
			throw new RecordFormatException("DB Smart Block: record length exception.");
		
		SmartBlock tempBlock = new SmartBlock();
		tempBlock.blockType = Block.BlockType.SMART;
		
		//block ID
		try
		{
			tempBlock.blockId = Integer.parseInt(elements[index++]);
		}
		catch(NumberFormatException e)
		{
			throw new RecordFormatException("DB Smart Block: block ID format exception.");
		}
		
		//Associated Fiducial IDs
		String[] stringAssociatedFiducials = elements[index++].split(",");
		int[] associatedFiducials = new int[stringAssociatedFiducials.length];
		for(int x = 0; x < associatedFiducials.length; ++x)
		{
			try
			{
				associatedFiducials[x] = Integer.parseInt(stringAssociatedFiducials[x]);
			}
			catch(NumberFormatException e)
			{
				throw new RecordFormatException("DB Smart Block: associated fiducials format exception.");
			}
		}
		tempBlock.associatedFiducials = associatedFiducials;
	
		//add fiducial center coordinates in 3D space
		String[] stringFiducialCoords = elements[index++].split(",");
		Vec3[] fiducialCoordinates = new Vec3[stringFiducialCoords.length / 3];
		for(int x = 0; x < fiducialCoordinates.length; ++x)
		{
			int i = 3 * x;
			try
			{
				fiducialCoordinates[x] = new Vec3();
				fiducialCoordinates[x].x = Double.parseDouble(stringFiducialCoords[i]);
				fiducialCoordinates[x].y = Double.parseDouble(stringFiducialCoords[i + 1]);
				fiducialCoordinates[x].z = Double.parseDouble(stringFiducialCoords[i + 2]);
			}
			catch(NumberFormatException e)
			{
				throw new RecordFormatException("DB Smart Block: fiducial coordinates format exception.");
			}
		}
		tempBlock.fiducialCoordinates = fiducialCoordinates;
		
		//add fiducial center coordinates in 3D space
		String[] stringFiducialUp = elements[index++].split(",");
		Vec3[] fiducialUp = new Vec3[stringFiducialUp.length / 3];
		for(int x = 0; x < fiducialUp.length; ++x)
		{
			int i = 3 * x;
			try
			{
				fiducialUp[x] = new Vec3();
				fiducialUp[x].x = Double.parseDouble(stringFiducialUp[i]);
				fiducialUp[x].y = Double.parseDouble(stringFiducialUp[i + 1]);
				fiducialUp[x].z = Double.parseDouble(stringFiducialUp[i + 2]);
			}
			catch(NumberFormatException e)
			{
				throw new RecordFormatException("DB Smart Block: fiducial coordinates format exception.");
			}
		}
		tempBlock.fiducialOrient = fiducialUp;
		
		//get model data from collada file
		String fileName = elements[index++];
		SmartBlock colladaBlock = ColladaLoader.getSmartBlock(fileName);
		tempBlock.vertices = colladaBlock.vertices;
		tempBlock.indices = colladaBlock.indices;
		tempBlock.name = fileName;
		
		//add block to hash for all associated fiducials
		for(int x = 0; x < associatedFiducials.length; ++x)
		{
			blocks.put(new Integer(associatedFiducials[x]), tempBlock);
		}
	}
	
	private void loadCommandBlockLine(String line) throws Exception
	{
		int index = 0;
		String[] elements = line.split("\t");
		
		if(elements.length != 3)
			throw new RecordFormatException("DB Command Block: record length exception.");
		
		CommandBlock tempBlock = new CommandBlock();
		tempBlock.blockType = Block.BlockType.COMMAND;
		
		//block ID
		try
		{
			tempBlock.blockId = Integer.parseInt(elements[index++]);
		}
		catch(NumberFormatException e)
		{
			throw new RecordFormatException("DB Command Block: block ID format exception.");
		}
		
		//Associated Fiducial IDs
		String[] stringAssociatedFiducials = elements[index++].split(",");
		int[] associatedFiducials = new int[stringAssociatedFiducials.length];
		for(int x = 0; x < associatedFiducials.length; ++x)
		{
			try
			{
				associatedFiducials[x] = Integer.parseInt(stringAssociatedFiducials[x]);
			}
			catch(NumberFormatException e)
			{
				throw new RecordFormatException("DB Command Block: associated fiducials format exception.");
			}
		}
		tempBlock.associatedFiducials = associatedFiducials;
		
		//fiducial coordinates
		tempBlock.fiducialCoordinates = new Vec3[]{new Vec3(0, 0, 0)};
		
		//command block type
		tempBlock.type = CommandBlock.CommandType.valueOf(elements[index++]);
		
		//add block to hash for all associated fiducials
		for(int x = 0; x < associatedFiducials.length; ++x)
		{
			blocks.put(new Integer(associatedFiducials[x]), tempBlock);
		}
	}
	
	private void loadUserBlockLine(String line) throws Exception
	{
		int index = 0;
		String[] elements = line.split("\t");
		
		if(elements.length != 5)
			throw new RecordFormatException("DB User Block: record length exception.");
		
		UserBlock tempBlock = new UserBlock();
		tempBlock.blockType = Block.BlockType.USER;
		
		//block ID
		try
		{
			tempBlock.blockId = Integer.parseInt(elements[index++]);
		}
		catch(NumberFormatException e)
		{
			throw new RecordFormatException("DB User Block: block ID format exception.");
		}
		
		//Associated Fiducial IDs
		String[] stringAssociatedFiducials = elements[index++].split(",");
		int[] associatedFiducials = new int[stringAssociatedFiducials.length];
		for(int x = 0; x < associatedFiducials.length; ++x)
		{
			try
			{
				associatedFiducials[x] = Integer.parseInt(stringAssociatedFiducials[x]);
			}
			catch(NumberFormatException e)
			{
				throw new RecordFormatException("DB User Block: associated fiducials format exception.");
			}
		}
		tempBlock.associatedFiducials = associatedFiducials;
		
		//user name
		tempBlock.name = elements[index++];
		
		//user address
		tempBlock.address = elements[index++];
		
		//user picture path
		tempBlock.picturePath = elements[index++];
		
		//add block to hash for all associated fiducials
		for(int x = 0; x < associatedFiducials.length; ++x)
		{
			blocks.put(new Integer(associatedFiducials[x]), tempBlock);
		}
	}
	
	private void saveBlockData() throws Exception
	{
		ArrayList<Block> blockList = new ArrayList<>(blocks.values());
		ArrayList<SmartBlock> smartList = new ArrayList<>();
		ArrayList<CommandBlock> commandList = new ArrayList<>();
		ArrayList<UserBlock> userList = new ArrayList<>();
		
		//Create dummy object for initial comparison
		Block prevBlock = new Block();
		prevBlock.blockId = -1;
		
		for(Block blockItem: blockList)
		{
			
			if(prevBlock.blockId != blockItem.blockId)
			{
				switch(blockItem.blockType)
				{
					case SMART:
						smartList.add((SmartBlock)blockItem);
						break;
					case COMMAND:
						commandList.add((CommandBlock)blockItem);
						break;
					case USER:
						userList.add((UserBlock)blockItem);
						break;
					default:
						throw new UnknownBlockTypeException("Found unknown block type in database save.");
				}
			}
			
			prevBlock = blockItem;
		}
		
		saveSmartBlockData(smartBlockPath, smartList);
		saveCommandBlockData(commandBlockPath, commandList);
		saveUserBlockData(userBlockPath, userList);
	}
	
	private void saveSmartBlockData(String fileName, ArrayList<SmartBlock> list)
	{
		try 
		{
			PrintWriter printWriter = new PrintWriter(smartBlockPath, "UTF-8");
			String line;
			
			//generate line
			for(SmartBlock smartBlockItem: list)
			{
				//block ID
				line = Integer.toString(smartBlockItem.blockId);
				
				//associated fiducials
				int[] associatedFiducials = smartBlockItem.associatedFiducials;
				line += "\t" + Integer.toString(associatedFiducials[0]);
				for(int i = 1; i < associatedFiducials.length; ++i)
				{
					line += "," + Integer.toString(associatedFiducials[i]);
				}
				
				//block fiducial coordinates
				Vec3[] vertices = smartBlockItem.fiducialCoordinates;
				line += "\t" + Double.toString(vertices[0].x);
				line += "," + Double.toString(vertices[0].y);
				line += "," + Double.toString(vertices[0].z);
				for(int i = 1; i < vertices.length; ++i)
				{
					line += "," + Double.toString(vertices[i].x);
					line += "," + Double.toString(vertices[i].y);
					line += "," + Double.toString(vertices[i].z);
				}
				
				//block collada file name
				line += "\t" + smartBlockItem.name;
				
				printWriter.println(line);
				line = "";
			}
			
			printWriter.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void saveCommandBlockData(String fileName, ArrayList<CommandBlock> list)
	{
		try 
		{
			PrintWriter printWriter = new PrintWriter(commandBlockPath, "UTF-8");
			String line;
			
			//generate line
			for(CommandBlock commandBlockItem: list)
			{
				//block ID
				line = Integer.toString(commandBlockItem.blockId);
				
				//associated fiducials
				int[] associatedFiducials = commandBlockItem.associatedFiducials;
				line += "\t" + Integer.toString(associatedFiducials[0]);
				for(int i = 1; i < associatedFiducials.length; ++i)
				{
					line += "," + Integer.toString(associatedFiducials[i]);
				}
				
				//block command type
				line += "\t" + commandBlockItem.type.toString();
				
				printWriter.println(line);
				line = "";
			}
			
			printWriter.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void saveUserBlockData(String fileName, ArrayList<UserBlock> list)
	{
		try 
		{
			PrintWriter printWriter = new PrintWriter(userBlockPath, "UTF-8");
			String line;
			
			//generate line
			for(UserBlock userBlockItem: list)
			{
				//block ID
				line = Integer.toString(userBlockItem.blockId);
				
				//associated fiducials
				int[] associatedFiducials = userBlockItem.associatedFiducials;
				line += "\t" + Integer.toString(associatedFiducials[0]);
				for(int i = 1; i < associatedFiducials.length; ++i)
				{
					line += "," + Integer.toString(associatedFiducials[i]);
				}
				
				//name
				line += "\t" + userBlockItem.name;
				
				//address
				line += "\t" + userBlockItem.address;
				
				//picture path
				line += "\t" + userBlockItem.picturePath;
				
				printWriter.println(line);
				line = "";
			}
			
			printWriter.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void insertBlock(Block block)
	{
	   int[] associatedFiducials = block.associatedFiducials;
	   
	   for(int fiducial: associatedFiducials)
	   {
		   blocks.put((Integer)fiducial, block);
	   }
	}
	   
	public Block findBlock(int fiducialID)
	{
		return blocks.get((Integer)fiducialID);
	} 
	
	public boolean saveDatabase() throws Exception
	{
		saveBlockData();
		
		return true;
	}
}