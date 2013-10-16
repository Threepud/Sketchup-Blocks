package sketchupblocks.construction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import sketchupblocks.database.*;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.InputBlock;
import sketchupblocks.base.Logger;
import sketchupblocks.base.RuntimeData;
import sketchupblocks.base.SessionManager;
import sketchupblocks.base.Settings;
import sketchupblocks.calibrator.*;
import sketchupblocks.math.Line;
import sketchupblocks.math.LineDirectionSolver;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.NoConvergenceException;
import sketchupblocks.math.RotationMatrix3D;
import sketchupblocks.math.SingularMatrixException;
import sketchupblocks.math.Vec3;
import sketchupblocks.math.nonlinearmethods.BPos;
import sketchupblocks.math.nonlinearmethods.ErrorFunction;
import sketchupblocks.math.nonlinearmethods.Newton;
import sketchupblocks.network.Lobby;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hein,Jacques,Elre
 */

public class ModelConstructor implements Runnable 
{
	private Lobby eddy;
	private Map<Integer,BlockInfo> blockMap;
	
	private Calibrator cally;
	
	private int changeWindow = 10;
	private double errorThreshold = 5; //Error threshold for calculating transformation matrix before resorting to PSO.
	
	public ModelConstructor(SessionManager _sessMan)
	{
		blockMap = new ConcurrentHashMap<Integer,BlockInfo>();
		cally = new Calibrator();
		Thread th = new Thread(this);
		th.start();
	}
	  
	public void setLobby(Lobby lobby)
	{
		eddy = lobby;
	}

	  
	/**
	 * This function is called by session manager to process all blocks
	 * @param iBlock the block seen by the camera.
	 */
	public void receiveBlock(InputBlock iBlock)
	{
		
		try
		{
			if (iBlock.block.blockType == Block.BlockType.COMMAND && ((CommandBlock)iBlock.block).type == CommandBlock.CommandType.CALIBRATE  )
			{
				callCalibrate(iBlock);
			}
			else 
			{
				
				store(iBlock);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	
	/**
	 * This function updates the block information in a Map to be processed later by a separate thread.
	 * The thread will only process the data once there is enough.
	 * @param iBlock
	 */
	private synchronized void store(InputBlock iBlock)
	{
		if (iBlock.cameraEvent.type != CameraEvent.EVENT_TYPE.REMOVE)
		{
			BlockInfo block = blockMap.get(iBlock.block.blockId);
			
			if (block == null)
			{
				block = new BlockInfo(iBlock.block);
				blockMap.put(iBlock.block.blockId,block);
			}
			
			if(!checkReAdd(block,iBlock))
			{
				BlockInfo.Fiducial fiducial =  block.new Fiducial(iBlock.cameraEvent);
				block.fiducialMap.put(block.new CamFidIdentifier(iBlock.cameraEvent.cameraID,iBlock.cameraEvent.fiducialID),fiducial);
				block.lastChange = new Date();
			}
		}
		else //When a remove call is received
		{
			BlockInfo block = blockMap.get(iBlock.block.blockId);
			if(block == null) return;
			BlockInfo.Fiducial fid = block.fiducialMap.get(block.new CamFidIdentifier(iBlock.cameraEvent.cameraID,iBlock.cameraEvent.fiducialID));
			if(fid == null) return;
			
			boolean blockWasRemoved = false;
			fid.setSeen(false);
			block.lastChange = new Date();
						
			if(!block.removed)
			{
				if(blockNotSeen(block))
				{
					if(expectedToSeeBlock(block))
					{
						block.removed = true;						
						eddy.updateModel(new ModelBlock((SmartBlock)block.smartBlock, null, ModelBlock.ChangeType.REMOVE));		
						blockWasRemoved = true;
					}
				}
			}
			
			if (blockWasRemoved)
			{
				//Check ALLLLLL the blocks
				for(BlockInfo blokkie : blockMap.values())
				{
					if(!blokkie.removed)
					{
						if(blockNotSeen(blokkie))
						{
							if(expectedToSeeBlock(blokkie))
							{
								blokkie.removed = true;						
								eddy.updateModel(new ModelBlock((SmartBlock)blokkie.smartBlock, null, ModelBlock.ChangeType.REMOVE));			
							}
						}
					}				
				}
					
			}
		}
	}
	
	/**
	 * @param block The block being assessed.
	 * @return true is the block has any visible fiducials
	 */
	private boolean blockNotSeen(BlockInfo block)
	{
		for(BlockInfo.Fiducial fid : block.fiducialMap.values())
		{
			if(fid.isSeen())
			{
				return false;
			}
		}
		return true;
	}
	
	private void reAddBlockToModel(BlockInfo bi)
	{
		bi.removed = false;
		ModelBlock mb = new ModelBlock((SmartBlock)bi.smartBlock, bi.getTransform(), ModelBlock.ChangeType.UPDATE);
		
		ArrayList<Line> dbLines = new ArrayList<Line>();
		ArrayList<Vec3>  dbPoints = new ArrayList<Vec3>();
		
		for(BlockInfo.Fiducial fid : bi.fiducialMap.values())
		{
			if(RuntimeData.getCameraPosition(fid.camID) == null)
			{
				continue;
			}
			if(fid.worldPosition == null)
			{
				continue;
			}
			Vec3 direction = Vec3.subtract(fid.worldPosition,RuntimeData.getCameraPosition(fid.camID));
			dbLines.add(new Line(RuntimeData.getCameraPosition(fid.camID),direction));
			dbPoints.add(fid.worldPosition);
		}
		
		mb.debugLines = dbLines.toArray(new Line[0]);
		mb.debugPoints = dbPoints.toArray(new Vec3[0]);
		//mb.debugPoints;
		eddy.updateModel(PseudoPhysicsApplicator.applyPseudoPhysics(mb));	
	
	}
	
	private boolean expectedToSeeBlock(BlockInfo block)
	{
		for(BlockInfo.Fiducial fid : block.fiducialMap.values())
		{
			Vec3 camPos = RuntimeData.getCameraPosition(fid.camID);
			Vec3 fidPos = fid.worldPosition;
			if(camPos == null || fidPos == null)
			{
				continue;
			}
		
			ModelBlock [] mb = EnvironmentAnalyzer.getIntersectingModels(camPos,fidPos);
					
			//The fiducial is not obscured.
			if((mb.length == 1 && mb[0].smartBlock.blockId == block.blockID) || mb.length == 0) 
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return a List of Blocks the are removed, but expected to be seen.
	 */
	private BlockInfo [] allPossibleReadditions()
	{
		ArrayList<BlockInfo> result = new ArrayList<BlockInfo>();
		for(BlockInfo bi : blockMap.values())
		{
			if(bi.removed && expectedToSeeBlock(bi) && bi.getLastSeen().getTime() < 2*changeWindow)
				result.add(bi);
		}
		BlockInfo [] res = new BlockInfo [0];
		return result.toArray(res);
	}
	
	private void doReadditions(BlockInfo [] bis)
	{
		for(BlockInfo bi : bis)
		{
			if(bi.removed && !expectedToSeeBlock(bi))
			{
				reAddBlockToModel(bi);
			}
		}
	}
	
	private boolean checkReAdd(BlockInfo block , InputBlock iBlock)
	{
		BlockInfo.CamFidIdentifier key = block.new CamFidIdentifier(iBlock.cameraEvent.cameraID,iBlock.cameraEvent.fiducialID);
		if(!block.fiducialMap.containsKey(key))
			return false; //There is no information to build on
		
		BlockInfo.Fiducial fid =  block.fiducialMap.get(key);
		if(!fid.isSeen()) //The block was removed and we are seeing it again.
		{
			if(fid.camID == iBlock.cameraEvent.cameraID && Math.abs(fid.camViewX - iBlock.cameraEvent.x) < 0.1 && Math.abs(fid.camViewY - iBlock.cameraEvent.y) < 0.1) // Seen at the same place
			{
				fid.setSeen(true);
				if(block.removed && block.getTransform() != null) // if all the fiducials are seen we add
				{
					reAddBlockToModel(block);							
				}
				return true;
			}
			
		}
		return false;
	}
	
	private void callCalibrate(InputBlock iBlock)
	{
		boolean calibrated = RuntimeData.isSystemCalibrated();
		if(!calibrated)
		{
			boolean changedPosition = cally.processBlock(iBlock);
			
			//Propagate updated camera positions to the appropriate parties.
			Logger.log("Calibrated ? "+calibrated, 100);
			Logger.log("changed: "+changedPosition, 100);
		}
	
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				Collection<BlockInfo> blocks = blockMap.values();
				Iterator<BlockInfo> iterate = blocks.iterator();
				while(iterate.hasNext())
				{
					BlockInfo b = iterate.next();
					if (b == null) 
						continue;
					
					double timePassed = Math.abs(b.lastChange.getTime() - new Date().getTime());
					if(b.ready() && RuntimeData.isSystemCalibrated() && (timePassed > changeWindow) )
					{
						Logger.log("Processing "+b.fiducialMap.size()+" number of lines after "+timePassed, 50);
						processBin(b);
					}
				}
				Thread.sleep(1);
		
			}
			catch(ConcurrentModificationException ie)
			{
				//If there is a concurrent change, then an exception will be thrown and we simply try again.
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * This calculates the block position and adds it to the model.
	 * @param bin
	 */
	private void processBin(BlockInfo bin)
	{
		BlockInfo.Fiducial [] fids = bin.getCleanFiducials();
		int numFiducials = fids.length;
		
		Line[] lines = new Line[numFiducials];
		for(int k = 0 ; k < fids.length ; k++)
		{
			lines[k] = fids[k].getLine(); 
			lines[k].direction.normalize();
		}
		
		Vec3[] fidCoordsM = new Vec3[numFiducials]; //Get from DB
		int [] cameraIds = new int[numFiducials];
		Vec3[] fidUpM = new Vec3[numFiducials];
		//Generate list of the indices (into the smartblock's associatedFiducials list) of the observed fiducials.
		SmartBlock sBlock = (SmartBlock)(bin.smartBlock);
		
		for (int k = 0 ; k < numFiducials ; k++)
		{
			cameraIds[k] = fids[k].camID;
			int fiducialIndex = -1;
			for (int i = 0 ; i < sBlock.associatedFiducials.length; i++)
			{
				if (sBlock.associatedFiducials[i] == fids[k].fiducialsID)
				{
					fiducialIndex = i;
					break;
				}
			}
			
			if(fiducialIndex == -1)
			{
				throw new RuntimeException("Smart Block fiducials don't match");
			}
			
			fidCoordsM[k] = sBlock.fiducialCoordinates[fiducialIndex];
			fidUpM[k] = sBlock.fiducialOrient[fiducialIndex];
		}
		
		Matrix lambdas = calculateLambdas(bin.getTransform(),cameraIds,fidCoordsM, lines);
		
		Vec3 [] fiducialWorld = new Vec3[numFiducials];
		for(int k = 0 ; k < numFiducials ; k++)
		{
			fids[k].worldPosition = fiducialWorld[k] = Vec3.add(lines[k].point,  Vec3.scalar(lambdas.data[k][0], lines[k].direction));
		}
		
		if(samePosition(bin,fidCoordsM,fiducialWorld))
			return;
		
		Matrix transform = ModelTransformationCalculator.getModelTransformationMatrix(fids, fiducialWorld, fidCoordsM, fidUpM, bin.getNumUniqueFiducials());
		double MTCScore =  getTransformationScore(transform, fiducialWorld, fidCoordsM);
		
		if(MTCScore > errorThreshold)
		{
			Matrix PSOtransform = PSOPosition.getModelTransformationMatrix(sBlock, fiducialWorld, fidCoordsM);
			double PSOScore = getTransformationScore(PSOtransform, fiducialWorld, fidCoordsM);
			
			if(PSOScore < MTCScore)
			{
				transform = PSOtransform;
			}
			
			Logger.log("Transformation score(MTC):"+MTCScore, 10);
			Logger.log("Transformation score(PSO):"+PSOScore, 10);
		}
		
		Logger.log("Transform: "+transform, 50);
		
		bin.setTransform(transform, numFiducials);
		bin.removed = false;
		
		ModelBlock mbToAdd = new ModelBlock(sBlock, transform, ModelBlock.ChangeType.UPDATE);
		
		mbToAdd.debugLines = lines;
		mbToAdd.debugPoints = fiducialWorld;
		
		BlockInfo [] bis = allPossibleReadditions();
		
		eddy.updateModel(PseudoPhysicsApplicator.applyPseudoPhysics(mbToAdd));
		//eddy.updateModel(mbToAdd);
		doReadditions(bis);
	}
	
	private boolean samePosition(BlockInfo bin,Vec3 [] model,Vec3 [] fids)
	{
		if(bin.getTransform() == null)
			return false;
		double error =0;
		for(int k = 0 ;  k < fids.length ; k++ )
		{
			double temp = fids[k].distance(Matrix.multiply(bin.getTransform(), model[k].padVec3()).toVec3());
			error +=temp*temp;
		}
		
		error /= fids.length;
		
		
		
		if(error < 1 && fids.length < bin.getNumFiducialsUsed())
		{
			//System.err.println("Same error:"+error+" num:"+fids.length);
			return true;
		}
		
		return false;
	}
	
	private Matrix calculateLambdas(Matrix tranformation, int [] camIds, Vec3[] fidCoordsM, Line[] lines)
	{
		int numFiducials = fidCoordsM.length;
		double[] dists = new double[numFiducials*(numFiducials-1)/2];
		int count = 0;
		for (int k = 0; k < numFiducials-1; k++)
		{
			for (int i = k+1; i < numFiducials; i++)
			{
				dists[count++] = fidCoordsM[k].distance(fidCoordsM[i]);
			}
		}
		
		double[] x0 = new double[numFiducials];
		double[] xt = new double[numFiducials];
		for (int k = 0; k < numFiducials; k++)
		{
			if(tranformation != null)
			{
				x0[k] = Matrix.multiply(tranformation,fidCoordsM[k].padVec3()).toVec3().distance(RuntimeData.getCameraPosition(camIds[k]));
			}
			else
			{
				x0[k] = 20;
			}
			xt[k] = 20;
		}
		
		Matrix lambdasNewton = null;
		Matrix lambdasPSO = null;
		double errorNewton = Double.MAX_VALUE;
		BPos bpos = new BPos(numFiducials, lines, dists);
		ErrorFunction errorFunc = new ErrorFunction(bpos);
		
		try
		{
			lambdasNewton = Newton.go(new Matrix(x0, true), errorFunc);
			Matrix lambdasD = Newton.go(new Matrix(xt, true), errorFunc);
			double error1 = errorFunc.calcError(lambdasNewton);
			double error2 = errorFunc.calcError(lambdasD);
			if (error2 < error1)
			{
				lambdasNewton = lambdasD;
				errorNewton = error2;
			}
			else
			{
				errorNewton = error1;
			}
		}
		catch(SingularMatrixException e) {}
		
		if(lambdasNewton == null ||  (errorNewton)  > 12)
		{
			Logger.log("Engaging PSO", 6);
			ParticleSystem system = new ParticleSystem(getPSOConfiguration(fidCoordsM, lines, numFiducials));
			Particle bestabc = null;
			bestabc = system.go();
			lambdasPSO = new Matrix(bestabc.bestPosition);
			
			if(lambdasNewton == null)
				return lambdasPSO;
			
			double psoError = errorFunc.calcError(lambdasPSO);
			
			Logger.log("--PSO error "+psoError+"--", 6);
			
			if(errorNewton > psoError)
			{
				return lambdasPSO;
			}
			else
				return lambdasNewton;
		}
		return lambdasNewton;		
	}
	
	private double getTransformationScore(Matrix transform, Vec3[] positions, Vec3[] fidCoordsM)
	{
		double error = 1;
       	for(int k = 0 ; k < positions.length ;k++)
       	{
       		double temp = positions[k].distance(Matrix.multiply(transform, fidCoordsM[k].padVec3()).toVec3());
       		error += temp*temp;
       	}
		
		return error;		
	}
	
	private ParticleSystemSettings getPSOConfiguration(Vec3[] fidCoordsM, Line[] lines, int numFids)
	{
		ParticleSystemSettings settings = new ParticleSystemSettings();
		settings.eval = new BlockPosition(fidCoordsM,lines);
		settings.tester = null;
		settings.creator = new ParticleCreator(numFids,0,90);
		
		settings.particleCount = 64;
		settings.iterationCount= 1024;
		
		settings.ringTopology = true;
		settings.ringSize =1;
		
		settings.socialStart = 0.72;
		settings.cognitiveStart = 0.72;
		settings.momentum = 1.4;
		settings.MaxComponentVelocity = 0.75;
		return settings;
	}
}

