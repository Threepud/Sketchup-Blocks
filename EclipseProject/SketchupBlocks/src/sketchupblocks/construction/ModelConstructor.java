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

	  
	public void receiveBlock(InputBlock iBlock)
	{
		try
		{
			if (iBlock.block.blockType == Block.BlockType.COMMAND && ((CommandBlock)iBlock.block).type == CommandBlock.CommandType.CALIBRATE  )
			{
				boolean calibrated = RuntimeData.isSystemCalibrated();
				if(!calibrated)
				{
					boolean changedPosition = cally.processBlock(iBlock);
					
					//Propagate updated camera positions to the appropriate parties.
					Logger.log("Calibrated ? "+calibrated, 100);
					Logger.log("changed: "+changedPosition, 100);
					
					if (changedPosition && calibrated)
					{
						for (int k = 0; k < Settings.numCameras; k++)
						{
							sessMan.updateCameraPosition(k);
						}
					}
				}
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
			
			BlockInfo.Fiducial fiducial =  block.new Fiducial(iBlock.cameraEvent);
			block.fiducialMap.put(block.new CamFidIdentifier(iBlock.cameraEvent.cameraID,iBlock.cameraEvent.fiducialID),fiducial);

			block.lastChange = new Date();
			
			/*if(block.ready() && calibrated)
			{
				processBin(block);
				//blockMap.remove(block.blockID);
				block.fiducialMap.clear();
			}*/
		}
		else //When a remove call is received
		{
			BlockInfo block = blockMap.get(iBlock.block.blockId);
			
			if (block == null)
			{
				Logger.log("Proposed removal of block that has not been added!?", 1);
				return; // Nothing to remove
			}	
			
			if (block.fiducialMap.containsKey(block.new CamFidIdentifier(iBlock.cameraEvent.cameraID,iBlock.cameraEvent.fiducialID)))
			{
				block.fiducialMap.remove(block.new CamFidIdentifier(iBlock.cameraEvent.cameraID,iBlock.cameraEvent.fiducialID));
			}
			
			/*
			* Cleanup -- Remove blocks if not seen
			* No need to clean up camera. They will always be there.
			*/
			
			/*if(block.fiducialMap.isEmpty())
			{
				blockMap.remove(iBlock.block.blockId);
				if (iBlock.block instanceof SmartBlock)
					eddy.updateModel(new ModelBlock((SmartBlock)iBlock.block, null, ModelBlock.ChangeType.REMOVE));
			}*/
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
						//b.fiducialMap.clear();
					}
				}
				Thread.sleep(1);
		
			}
			catch(Exception e)
			{
				e.printStackTrace();
				//If there is a concurrent change, then an exception will be thrown and we simply try again.
			}
		}
	}
	
	private void processBin(BlockInfo bin)
	{
		if (bin.smartBlock instanceof SmartBlock)
		{
			BlockInfo.Fiducial [] fids = new BlockInfo.Fiducial[0];
			fids = bin.fiducialMap.values().toArray(fids);
			
			BlockInfo.CamFidIdentifier [] camIDs = new BlockInfo.CamFidIdentifier[0];
			camIDs = bin.fiducialMap.keySet().toArray(camIDs);
			
			int numFiducials = fids.length;
			
			Line[] lines = new Line[numFiducials];
			for(int k = 0 ; k < fids.length ; k++)
			{
				lines[k] = fids[k].getLine(); 
				lines[k].direction.normalize();
			}
			
			//For debugging purposes
			//To display debugging lines to fiducial centers
			String[] IDS = new String[fids.length];
			for(int a = 0; a < IDS.length; ++a)
				IDS[a] = fids[a].camID + "," + fids[a].fiducialsID;
			sessMan.debugLines(IDS, lines);
			
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
				fiducialWorld[k] = Vec3.add(lines[k].point, Vec3.scalar(bestabc.bestPosition[k], lines[k].direction));
				RotationMatrix3D rot = new RotationMatrix3D(fids[k].rotation, Matrix.Axis.Z_AXIS);	
				upRotWorld[k] = getUpVector(camIDs[k].cameraID);
				upRotWorld[k] =  Matrix.multiply(rot, upRotWorld[k]);
			}

			String[] IDP = new String[numFiducials];
			for(int a = 0; a < IDP.length; ++a)
				IDP[a] = fids[a].camID + "," + fids[a].fiducialsID;
			sessMan.debugPoints(IDP, fiducialWorld);
			
			
			IDP = new String[fiducialWorld.length];
			for(int a = 0; a < IDP.length; ++a)
				IDP[a] = ""+fids[a].fiducialsID;
			sessMan.debugPoints(IDP, fiducialWorld);
			
			Integer[] temp = new Integer[0];
			Matrix transform = ModelTransformationCalculator.getModelTransformationMatrix(upRotWorld, sBlock, fiducialWorld, fiducialIndices.toArray(temp));
			//Matrix transform = ModelTransformationCalculator.getModelTransformationMatrix(upRotWorld, sBlock, fiducialWorld, uniqueFidBlockIndex.toArray(temp));
			
			Logger.log("Transform: "+transform, 50);
			
			eddy.updateModel(new ModelBlock(sBlock, transform, ModelBlock.ChangeType.UPDATE));
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
		
		settings.particleCount = 32;
		settings.iterationCount= 640;
		
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

