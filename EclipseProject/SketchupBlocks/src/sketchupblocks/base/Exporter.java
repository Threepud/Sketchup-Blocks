package sketchupblocks.base;

import java.util.ArrayList;

import sketchupblocks.network.Lobby;

class Exporter
{
	Lobby eddy;
	
	public void setLobby(Lobby _lobby)
	{
		eddy = _lobby;
	}
	
	public void export()
	{
		Model model = eddy.getModel();
		ArrayList<ModelBlock> blocks = new ArrayList<>(model.getBlocks());
		ColladaLoader.export(blocks);
	}
}