package sketchupblocks.base;

import java.util.ArrayList;

import sketchupblocks.construction.ModelBlock;
import sketchupblocks.exception.ModelNotSetException;
import sketchupblocks.network.Lobby;

/**
 * Exporter manages the exporting of the Model currently being built.
 * It maintains a reference to the Lobby (allowing it to access the model).
 * 
 * @author cravingoxygen
 *
 */
class Exporter
{
	private Lobby eddy;
	
	/**
	 * Sets the lobby reference maintained by the Exporter to be the lobby object passed as parameter.
	 * 
	 * @param _lobby The lobby object to which the Exporter's reference must be set.
	 */
	public void setLobby(Lobby _lobby)
	{
		eddy = _lobby;
	}
	
	/**
	 * Checks whether the current Model contains any blocks.
	 * 
	 * @return true if there are blocks in the Model object and false if the Model is empty.
	 */
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
			e.printStackTrace();
			return false;
		}
    }
	
	/**
	 * Makes use of ColladaLoader to export the Model to a file of type Collada.
	 * If the Model is empty, it simply returns without creating an export file.
	 * The exported Collada file is created in a directory specified in Settings.xml
	 * @throws ModelNotSetException
	 */
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