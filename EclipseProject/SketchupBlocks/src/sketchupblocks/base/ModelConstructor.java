package sketchupblocks.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import sketchupblocks.database.*;
import sketchupblocks.calibrator.*;
import sketchupblocks.math.Line;
import sketchupblocks.math.LinearSystemSolver;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.RotationMatrix3D;
import sketchupblocks.math.Vec3;
import sketchupblocks.math.Vec4;
import sketchupblocks.network.Lobby;

public class ModelConstructor
{
	private Lobby eddy;
	private HashMap<Integer,BlockInfo> blockMap;
	
	private Calibrator cally;
	private SessionManager sessMan;
	
	private boolean calibrated = false;
	
	public ModelConstructor(SessionManager _sessMan)
	{
		blockMap = new HashMap<Integer,BlockInfo>();
		cally = new Calibrator();
		sessMan = _sessMan;
	}
	  
	public void setLobby(Lobby lobby)
	{
		eddy = lobby;
	}

	  
	public void receiveBlock(InputBlock iBlock)
	{
		if (iBlock.block.blockType == Block.BlockType.COMMAND && ((CommandBlock)iBlock.block).type == CommandBlock.CommandType.CALIBRATE  )
		{
			if(!cally.isCalibrated())
			{
				boolean changedPosition = cally.processBlock(iBlock);
				calibrated = cally.isCalibrated();
				//Propagate updated camera positions to the appropriate parties.
				System.out.println("Calibrated: "+calibrated);
				System.out.println("changed: "+changedPosition);
				if (changedPosition && calibrated)
				{
					System.out.println("Sending camera positions through to model viewer");
					for (int k = 0; k < Settings.numCameras; k++)
					{
						sessMan.updateCameraPosition(k, cally.cameraPositions[k]);
					}
				}
			}
		}
		else 
		{
			store(iBlock);
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
				System.out.println("Line "+k+":"+lines[k].point+" +m* " +lines[k].direction);
			}
			
			Vec3[] fidCoordsM = new Vec3[numFiducials]; //Get from DB
			if (!(bin.smartBlock instanceof SmartBlock))
			{
				System.out.println("Attempting to process command block, but this feature is not yet supported");
				return;
			}
			
			SmartBlock sBlock =(SmartBlock)(bin.smartBlock);
			System.out.println("Doing Block:"+sBlock.blockId);
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
			System.out.println("Calculated m's "+bin.blockID+" num fids: " + fids.length);			
			bestabc = system.go();
			
				for(int l = 0 ; l < bestabc.bestPosition.length ; l++)
				{
					System.out.print(bestabc.bestPosition[l] + " " );
				}
				System.out.println();
			
			Vec3 [] fiducialWorld = new Vec3[numFiducials];
			Vec3 [] upRotWorld = new Vec3[numFiducials];
			for(int k = 0 ; k < numFiducials ; k++)
			{
				fiducialWorld[k] = Vec3.add(lines[k].point, Vec3.scalar(bestabc.bestPosition[k], lines[k].direction));
	
				RotationMatrix3D rot = new RotationMatrix3D(fids[k].rotation);	
				upRotWorld[k] = getUpVector(camIDs[k].cameraID);
				upRotWorld[k] =  Matrix.multiply(rot, upRotWorld[k]);
			}
			
			
			/*
			* upRotWorld -- the rotations as viewed by the camera
			* fiducialWorld -- Fiducial locations
			* lines[k].direction -- the k'th fiducial view vector
			* sBlock -- The smart block
			*/
			
			/*
			 * Now colapse the arrays
			 */
			/*Integer indexs[] = new Integer[numFiducials];
			int fidIDs []  = new int[numFiducials];
			int [] addCount = new int[numFiducials];
			Vec3 [] upPass = new Vec3[numFiducials];
			Vec3 [] posPass = new Vec3[numFiducials];
			int count= 1;
			//setup
			indexs[0] = fiducialIndices.get(0) ;
			upPass[0] = upRotWorld[0];
			posPass[0] = fiducialWorld[0];
			fidIDs[0] = fids[0].fiducialsID;
			addCount[0] =1;
			
			for(int k = 1 ; k < numFiducials ; k++)
				{
					boolean match = false;
					int l;
					for(l = 0 ; l < count ; l++)
						{
							if(fidIDs[l] == fids[k].fiducialsID)
							{
								match = true;
								break;
							}	
						}
					if(match)
					{
						addCount[l]++;
						upPass[l] = Vec3.add(upPass[l],upRotWorld[k] );
						posPass[l] = Vec3.add(posPass[l],fiducialWorld[k] );
					}
					else
					{
						indexs[count] = fiducialIndices.get(k)  ;
						upPass[count] = upRotWorld[k];
						posPass[count] = fiducialWorld[k];
						fidIDs[count] = fids[k].fiducialsID;
						addCount[count] =1;
						count++;
					}
						
				}
					
				for(int k = 1 ; k < count ; k++)		
				{
					upPass[k] = Vec3.scalar(1.0/addCount[k], upPass[k]);
					posPass[k] = Vec3.scalar(1.0/addCount[k], posPass[k]);
				}*/
				
			
			Integer[] temp = new Integer[0];
			Matrix transform = ModelCenterCalculator.getModelTransformationMatrix(upRotWorld, sBlock, fiducialWorld, fiducialIndices.toArray(temp));
			//Matrix transform = ModelCenterCalculator.getModelTransformationMatrix(upPass, sBlock, posPass, indexs);
			
			eddy.updateModel(new ModelBlock(sBlock, transform, ModelBlock.ChangeType.UPDATE));
		}
		else
		{
			System.out.println("Attempting to process a Command Block, but this is not yet supported");
		}
	}
	
	private ParticleSystemSettings getPSOConfiguration(Vec3[] fidCoordsM, Line[] lines, int numFids)
	{
		ParticleSystemSettings settings = new ParticleSystemSettings();
		settings.eval = new BlockPosition(fidCoordsM,lines);
		settings.tester = null;
		settings.creator = new ParticleCreator(numFids,0,90);
		
		settings.particleCount = 80;
		settings.iterationCount= 8000;
		
		settings.ringTopology = false;
		settings.ringSize = 5;
		
		settings.socialStart = 0.72;
		settings.cognitiveStart = 0.72;
		settings.momentum = 1.4;
		settings.MaxComponentVelocity = 0.1;
		return settings;
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
			
			//BlockInfo.Fiducial fiducial = block.fiducialMap.get(block.new CamFidIdentifier(iBlock.cameraEvent.cameraID, iBlock.cameraEvent.fiducialID));
			
			BlockInfo.Fiducial fiducial =  block.new Fiducial(iBlock.cameraEvent);
			block.fiducialMap.put(block.new CamFidIdentifier(iBlock.cameraEvent.cameraID,iBlock.cameraEvent.fiducialID),fiducial);

			//Get line used to be here
			
			if(block.ready() && calibrated)
			{
				processBin(block);
				//blockMap.remove(block.blockID);
				block.fiducialMap.clear();
			}
		}
		else //When a remove call is received
		{
			BlockInfo block = blockMap.get(iBlock.block.blockId);
			
			if (block == null)
			{
				System.out.println("Proposed removal of block that has not been added!?");
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
			
			if(block.fiducialMap.isEmpty())
			{
				blockMap.remove(iBlock.block.blockId);
				if (iBlock.block instanceof SmartBlock)
					eddy.updateModel(new ModelBlock((SmartBlock)iBlock.block, null, ModelBlock.ChangeType.REMOVE));
			}
		}
	}
	
	private double getAngle(int camID, int lm, double x, double y)
	{
		double fov = Settings.cameraSettings[camID].fov;
		double aspect = Settings.cameraSettings[camID].aspectRatio;
		return Math.toRadians( Math.sqrt(sqr((cally.calibrationDetails[camID][lm][0]- x)*fov)+sqr((cally.calibrationDetails[camID][lm][1]- y)*(fov/aspect)))); 
	}
	
	private Vec3 getUpVector(int CamID)
	{
		Vec3[] landmarkToCamera = new Vec3[4];
		for (int k = 0; k < 4; k++)
		{
			landmarkToCamera[k] = Vec3.subtract(cally.cameraPositions[CamID], Settings.landmarks[k]);
		}
		
		double[] angles = new double[4];
		for (int k = 0; k < 4; k++)
		{
			angles[k] = getAngle(CamID, k, 0.5, 0.5+0.01);
		}
		// Do calculation 
		Vec3 lineDirection = LinearSystemSolver.solve(landmarkToCamera, angles);
		Line top = new Line(cally.cameraPositions[CamID], lineDirection);
		
		angles = new double[4];
		for (int k = 0; k < 4; k++)
		{
			angles[k] = getAngle(CamID, k, 0.5, 0.5-0.01);
		}
		// Do calculation 
		lineDirection = LinearSystemSolver.solve(landmarkToCamera, angles);
		Line bottom = new Line(cally.cameraPositions[CamID], lineDirection);
		
		top.direction.normalize();
		bottom.direction.normalize();
		
		return Vec3.subtract(top.direction, bottom.direction);
	}
		
	private double sqr(double val)
	{
		return val*val;	
	}
		
	class BlockInfo
	{
		int minEvents = 3;
		int LIFETIME = 2000;	//ms
		public int blockID;
		public Block smartBlock;
		private HashMap<CamFidIdentifier,Fiducial> fiducialMap;
	
		
		public BlockInfo(Block _smartBlock)
		{
			blockID = _smartBlock.blockId;
			fiducialMap = new HashMap<CamFidIdentifier,Fiducial>();
			smartBlock = _smartBlock;
		}
		
		
		public boolean ready()
		{
			Fiducial[] data = new Fiducial[0];
			data = fiducialMap.values().toArray(data);
			
			int count = 0;
			for (int k = 0; k < data.length; k++)
			{
				if (data[k] != null)
				{
					if (new Date().getTime() - data[k].timestamp.getTime() < LIFETIME)
					{
						count++;
					}
					else
					{
						data[k] = null;
					}
				}
			}
			
			if (count >= minEvents)
				return true;
			else 
				return false;
		}
		
		protected class CamFidIdentifier
		{
			int cameraID;
			int fiducialID;
			
			CamFidIdentifier(int c, int f)
			{
				cameraID = c;
				fiducialID = f;
			}
			@Override
			public boolean equals(Object other)
			{
				if(other instanceof CamFidIdentifier)
					return ((CamFidIdentifier)other).cameraID == cameraID && ((CamFidIdentifier)other).fiducialID == fiducialID;
				else
					return false;
			}
			
			@Override
			public int hashCode()
			{
			return 	(cameraID*256)+fiducialID;
			}
		}
	
		protected class Fiducial
		{
			public int fiducialsID;
			public int camID;
			public double rotation;
			public Date timestamp;
			public double camViewX;
			public double camViewY;
			
			public Fiducial(CameraEvent camE)
			{
				this(camE.fiducialID, camE.rotation, camE.x, camE.y, camE.cameraID);
			}
			
			public Fiducial(int _fiducialsID, double rot, double _camViewX, double _camViewY, int _camID)
			{
				fiducialsID = _fiducialsID;
				rotation = rot;
				timestamp = new Date();
				camViewX = _camViewX;
				camViewY = _camViewY;
				camID = _camID;
			}
			
			public Line getLine()
			{
				//Line calculation
				Vec3[] landmarkToCamera = new Vec3[4];
				double[] angles = new double[4];
				
				for (int k = 0; k < 4; k++)
				{
					landmarkToCamera[k] = Vec3.subtract(Settings.landmarks[k],cally.cameraPositions[camID] );
					angles[k] = getAngle(camID, k, camViewX, camViewY);
				}
				// Do calculation 
				Vec3 lineDirection = LinearSystemSolver.solve(landmarkToCamera, angles);
				return new Line(cally.cameraPositions[camID], lineDirection);
			}
		}
	}	
}