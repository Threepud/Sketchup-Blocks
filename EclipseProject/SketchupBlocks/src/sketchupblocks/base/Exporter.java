package sketchupblocks.base;

import java.util.ArrayList;

import sketchupblocks.construction.ModelBlock;
import sketchupblocks.exception.ModelNotSetException;
import sketchupblocks.network.Lobby;

class Exporter
{
	Lobby eddy;
	
	public void setLobby(Lobby _lobby)
	{
		eddy = _lobby;
	}
	
	public boolean checkModelExists()
    {
    	try 
		{
			Model tempModel;
			tempModel = eddy.getModel();
			ArrayList<ModelBlock> tempArr = new ArrayList<>(tempModel.getBlocks());
			
			return !tempArr.isEmpty();
		}
		catch (ModelNotSetException e) 
		{
			System.out.println(e);
			return false;
		}
    }
	
	public void export() throws ModelNotSetException
	{
		if(!checkModelExists())
			return;
		
		Model model = null;
		try
		{
			model = eddy.getModel();
		}
		catch(ModelNotSetException e)
		{
			throw e;
		}
		ArrayList<ModelBlock> blocks = new ArrayList<>(model.getBlocks());
		ColladaLoader.export(blocks);
	}
}