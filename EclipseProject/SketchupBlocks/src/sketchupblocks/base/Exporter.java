package sketchupblocks.base;

import java.util.ArrayList;

import sketchupblocks.exception.ModelNotSetException;
import sketchupblocks.network.Lobby;

class Exporter
{
	Lobby eddy;
	
	public void setLobby(Lobby _lobby)
	{
		eddy = _lobby;
	}
	
	public void export() throws ModelNotSetException
	{
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