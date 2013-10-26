package sketchupblocks.base;

import java.util.ArrayList;

import sketchupblocks.construction.EnvironmentAnalyzer;
import sketchupblocks.construction.ModelBlock;
import sketchupblocks.construction.PseudoPhysics;
import sketchupblocks.exception.ModelNotSetException;
import sketchupblocks.network.LocalLobby;

public class PseudoPhysicsApplicator implements ModelChangeListener
{
	private LocalLobby eddy;
	public PseudoPhysicsApplicator(LocalLobby _eddy)
	{
		eddy = _eddy;
		eddy.registerChangeListener(this);
	}
	
	@Override
	public void fireModelChangeEvent(ModelBlock change) 
	{
		try
		{
			ArrayList<ModelBlock> blocks = new ArrayList<ModelBlock>(eddy.getModel().getBlocks());
			
			//First we sort the blocks from the lowest to the highest.
			EnvironmentAnalyzer.sortBottomUp(blocks);
			
			//We then apply physics to each of the blocks.
			for (int k = 0; k < blocks.size(); k++)
			{
				blocks.set(k, PseudoPhysics.applyPseudoPhysics(blocks.get(k)));
			}
			
			eddy.updateModel(blocks);
		}
		catch(ModelNotSetException mns)
		{
			mns.printStackTrace();
		}
	}

}
