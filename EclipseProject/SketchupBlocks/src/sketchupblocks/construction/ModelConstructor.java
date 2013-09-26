package sketchupblocks.construction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import sketchupblocks.database.*;
import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.CommandBlock;
import sketchupblocks.base.InputBlock;
import sketchupblocks.base.Logger;
import sketchupblocks.base.RuntimeData;
import sketchupblocks.base.SessionManager;
import sketchupblocks.base.Settings;
import sketchupblocks.calibrator.*;
import sketchupblocks.math.Line;
import sketchupblocks.math.LineDirectionSolver;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.RotationMatrix3D;
import sketchupblocks.math.Vec3;
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
	private SessionManager sessMan;
	
	//private boolean calibrated = false;
	private int changeWindow = 100;
	
	public ModelConstructor(SessionManager _sessMan)
	{
		blockMap = new ConcurrentHashMap<Integer,BlockInfo>();
		cally = new Calibrator();
		sessMan = _sessMan;
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
	private void store(InputBlock iBlock)
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
	
	private void addBlockToModel(BlockInfo bi)
	{
		bi.removed = false;
		ModelBlock mb = new ModelBlock((SmartBlock)bi.smartBlock, bi.transform, ModelBlock.ChangeType.UPDATE);
		
		Line [] dbLines = new Line[bi.fiducialMap.size()];
		Vec3 [] dbPoints = new Vec3[bi.fiducialMap.size()];
		int k = 0 ;
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
			Vec3 direction = Vec3.subtract(RuntimeData.getCameraPosition(fid.camID),fid.worldPosition);
			dbLines[k] = new Line(RuntimeData.getCameraPosition(fid.camID),direction);
			dbPoints[k] = fid.worldPosition;
			k++;
		}
		
		
		//mb.debugPoints;
		eddy.updateModel(mb);	
	
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
			if(bi.removed && expectedToSeeBlock(bi))
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
				addBlockToModel(bi);
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
			if(Math.abs(fid.camViewX - iBlock.cameraEvent.x) < 0.1 && Math.abs(fid.camViewY - iBlock.cameraEvent.y) < 0.1) // Seen at the same place
			{
				fid.setSeen(true);
				if(block.removed && block.transform != null) // if all the fiducials are seen we add
				{
					addBlockToModel(block);							
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
			catch(Exception e)
			{
				//If there is a concurrent change, then an exception will be thrown and we simply try again.
			}
		}
	}
	
	
	
	/**
	 * This calculates the block position and adds it to the model.
	 * @param bin
	 */
	private void processBin(BlockInfo bin)
	{
		if (bin.smartBlock instanceof SmartBlock)
		{
			BlockInfo.Fiducial [] fids = new BlockInfo.Fiducial[0];
			BlockInfo.CamFidIdentifier [] camIDs = new BlockInfo.CamFidIdentifier[0];
			
			ArrayList<BlockInfo.Fiducial> cleanFids = new ArrayList<BlockInfo.Fiducial>();
			ArrayList<BlockInfo.CamFidIdentifier> cleanIDs = new ArrayList<BlockInfo.CamFidIdentifier>();
			
			for(BlockInfo.CamFidIdentifier keys : bin.fiducialMap.keySet())
			{
				BlockInfo.Fiducial fid = bin.fiducialMap.get(keys);
				if(fid.isSeen())
					{
					cleanFids.add(fid);
					cleanIDs.add(keys);
					}
			}
			fids =cleanFids.toArray(fids);			
			camIDs = cleanIDs.toArray(camIDs);
			
			int numFiducials = fids.length;
			
			
			
			Line[] lines = new Line[numFiducials];
			for(int k = 0 ; k < fids.length ; k++)
			{
				lines[k] = fids[k].getLine(); 
				lines[k].direction.normalize();
			}
			
			Vec3[] fidCoordsM = new Vec3[numFiducials]; //Get from DB
			
			
			//Generate list of the indices (into the smartblock's associatedFiducials list) of the observed fiducials.
			SmartBlock sBlock =(SmartBlock)(bin.smartBlock);
			ArrayList<Integer> fiducialIndices = new ArrayList<Integer>();
			
			for (int k = 0 ; k < numFiducials ; k++)
			{
				int fiducialIndex = -1;
				for (int i = 0 ; i < sBlock.associatedFiducials.length; i++)
				{
					if (sBlock.associatedFiducials[i] == fids[k].fiducialsID)
					{
						fiducialIndex = i;
						fiducialIndices.add(i);
						break;
					}
				}
				
				if(fiducialIndex == -1)
				{
					throw new RuntimeException("Smart Block fiducials don't match");
				}
				
				fidCoordsM[k] = sBlock.fiducialCoordinates[fiducialIndex];
			}
			
			//Bin should have enough information to get position.
			ParticleSystem system = new ParticleSystem(getPSOConfiguration(fidCoordsM, lines, fids.length));
			Particle bestabc = null;
			bestabc = system.go();
			
			Vec3 [] fiducialWorld = new Vec3[numFiducials];
			Vec3 [] upRotWorld = new Vec3[numFiducials];
			for(int k = 0 ; k < numFiducials ; k++)
			{
				fids[k].worldPosition = fiducialWorld[k] = Vec3.add(lines[k].point, Vec3.scalar(bestabc.bestPosition[k], lines[k].direction));
				RotationMatrix3D rot = new RotationMatrix3D(fids[k].rotation, Matrix.Axis.Z_AXIS);	
				upRotWorld[k] = getUpVector(camIDs[k].cameraID);
				upRotWorld[k] =  Matrix.multiply(rot, upRotWorld[k]);
			}
			
			Integer[] temp = new Integer[0];
			Matrix transform = ModelTransformationCalculator.getModelTransformationMatrix(upRotWorld, sBlock, fiducialWorld, fiducialIndices.toArray(temp));
			//Matrix transform = ModelTransformationCalculator.getModelTransformationMatrix(upRotWorld, sBlock, fiducialWorld, uniqueFidBlockIndex.toArray(temp));
			
			Logger.log("Transform: "+transform, 50);
			
			bin.transform = transform;
			bin.removed = false;
			
			ModelBlock mbToAdd = (new ModelBlock(sBlock, transform, ModelBlock.ChangeType.UPDATE));
			
			mbToAdd.debugLines = lines;
			mbToAdd.debugPoints = fiducialWorld;
			
			BlockInfo [] bis = allPossibleReadditions();
			eddy.updateModel((mbToAdd)); // PseudoPhysicsApplicator.applyPseudoPhysics
			doReadditions(bis);
		}
		else
		{
			Logger.log("Attempting to process a Command Block, but this is not yet supported", 1);
		}
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
	
	private Vec3 getUpVector(int camID)
	{
		Vec3[] landmarkToCamera = new Vec3[4];
		for (int k = 0; k < 4; k++)
		{
			landmarkToCamera[k] = Vec3.subtract(RuntimeData.getCameraPosition(camID), Settings.landmarks[k]);
		}
		
		double[] angles = new double[4];
		for (int k = 0; k < 4; k++)
		{
			angles[k] = RuntimeData.getAngle(camID, k, 0.5, 0.5+0.01);
		}
		// Do calculation 
		Vec3 lineDirection = LineDirectionSolver.solve(landmarkToCamera, angles);
		Line top = new Line(RuntimeData.getCameraPosition(camID), lineDirection);
		
		angles = new double[4];
		for (int k = 0; k < 4; k++)
		{
			angles[k] = RuntimeData.getAngle(camID, k, 0.5, 0.5-0.01);
		}
		// Do calculation 
		lineDirection = LineDirectionSolver.solve(landmarkToCamera, angles);
		Line bottom = new Line(RuntimeData.getCameraPosition(camID), lineDirection);
		
		top.direction.normalize();
		bottom.direction.normalize();
		
		return Vec3.subtract(top.direction, bottom.direction);
	}
}

