package sketchupblocks.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sketchupblocks.database.*;
import sketchupblocks.base.ModelConstructor.BlockInfo.Fiducial;
import sketchupblocks.calibrator.*;
import sketchupblocks.math.Line;
import sketchupblocks.math.LineDirectionSolver;
import sketchupblocks.math.LinearSystemSolver;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.RotationMatrix3D;
import sketchupblocks.math.Vec3;
import sketchupblocks.math.Vec4;
import sketchupblocks.network.Lobby;

import java.util.concurrent.ConcurrentHashMap;

public class ModelConstructor implements Runnable 
{
	private Lobby eddy;
	private Map<Integer,BlockInfo> blockMap;
	
	private Calibrator cally;
	private SessionManager sessMan;
	
	private boolean calibrated = false;
	
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
		if (iBlock.block.blockType == Block.BlockType.COMMAND && ((CommandBlock)iBlock.block).type == CommandBlock.CommandType.CALIBRATE  )
		{
			sessMan.updateCalibratedCameras(cally.getCalibrated());
			if(!cally.isCalibrated())
			{
				boolean changedPosition = cally.processBlock(iBlock);
				calibrated = cally.isCalibrated();
				//Propagate updated camera positions to the appropriate parties.
				if (Settings.verbose >= 3)
					System.out.println("Calibrated ? "+calibrated);
				if (Settings.verbose > 3)
					System.out.println("changed: "+changedPosition);
				if (changedPosition && calibrated)
				{
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
					double timePased = Math.abs(b.lastChange.getTime() - new Date().getTime());
					if(b.ready() && calibrated && (timePased > changeWindow) )
					{
						if (Settings.verbose >= 3)
							System.out.println("Processing "+b.fiducialMap.size()+" number of lines after "+timePased);
						processBin(b);
						b.fiducialMap.clear();
					}
				}
		
			}
			catch(Exception e)
			{
				//If the is a concurrent change an exception will be thrown. Then we simply try again.
			}
		}
	}
	
	private void processBin(BlockInfo bin)
	{
		if (bin.smartBlock instanceof SmartBlock)
		{
			//System.out.println("Processing bin for "+bin.smartBlock.blockId);
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
			if (!(bin.smartBlock instanceof SmartBlock))
			{
				System.out.println("Attempting to process command block, but this feature is not yet supported");
				return;
			}
			
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
				if (k != 0 && Settings.verbose > 2)
				{
					System.out.println("Line "+k+":"+lines[k].point+" +m* " +lines[k].direction);
					System.out.println("Distance b/w "+(k-1)+" and "+(k)+" is "+fidCoordsM[k].distance(fidCoordsM[k-1]));
				}
			}
			if (Settings.verbose == 10)
				catcher(fidCoordsM, lines, fids);
			//Bin should have enough information to get position.
			ParticleSystem system = new ParticleSystem(getPSOConfiguration(fidCoordsM, lines, fids.length));
			Particle bestabc = null;
			//System.out.println("Calculated m's "+bin.blockID+" num fids: " + fids.length);	
			bestabc = system.go();
			
				/*for(int l = 0 ; l < bestabc.bestPosition.length ; l++)
				{
					System.out.print(bestabc.bestPosition[l] + " " );
				}
				System.out.println();*/
			
			Vec3 [] fiducialWorld = new Vec3[numFiducials];
			Vec3 [] upRotWorld = new Vec3[numFiducials];
			for(int k = 0 ; k < numFiducials ; k++)
			{
				fiducialWorld[k] = Vec3.add(lines[k].point, Vec3.scalar(bestabc.bestPosition[k], lines[k].direction));
				//Is ons absolutely convinced dat ons die rotations so kan gebruik?
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
		
		settings.particleCount = 160;
		settings.iterationCount= 1600;
		
		settings.ringTopology = true;
		settings.ringSize =1;
		
		settings.socialStart = 0.72;
		settings.cognitiveStart = 0.72;
		settings.momentum = 1.4;
		settings.MaxComponentVelocity = 0.51;
		return settings;
	}
	
	private void catcher(Vec3[] fidCoordsM, Line[] lines, BlockInfo.Fiducial[] fids)
	{
		if (lines.length != fids.length)
			System.out.println("894032jkfadsfajdsklfd;lsajka;fjlcdskljk;adfs;ljkfdsalj;kdasfj;kldfaskjl;fads");
		for (int k = 0; k < fids.length-1; k++)
		{
			for (int i = k+1; i < fids.length; i++)
			{
				if (fids[k].camID != fids[i].camID && fids[k].fiducialsID == fids[i].fiducialsID)
				{
					System.out.println("============================================================================");
					System.out.println("Fiducial "+fids[k].fiducialsID);
					System.out.println("Camera : "+fids[k].camID);
					System.out.println(cally.cameraPositions[fids[k].camID]);
					System.out.println("Direction: ");
					System.out.println(lines[k].direction);
					
					System.out.println("----------------------------------------------------------------------------");
					System.out.println("Camera : "+fids[i].camID);
					System.out.println(cally.cameraPositions[fids[i].camID]);
					System.out.println("Direction: ");
					System.out.println(lines[i].direction);
					System.out.println("============================================================================");
				}
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
		Vec3 lineDirection = LineDirectionSolver.solve(landmarkToCamera, angles);
		Line top = new Line(cally.cameraPositions[CamID], lineDirection);
		
		angles = new double[4];
		for (int k = 0; k < 4; k++)
		{
			angles[k] = getAngle(CamID, k, 0.5, 0.5-0.01);
		}
		// Do calculation 
		lineDirection = LineDirectionSolver.solve(landmarkToCamera, angles);
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
		public Date lastChange;
		private Map<CamFidIdentifier,Fiducial> fiducialMap;
	
		
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
				Vec3 lineDirection = LineDirectionSolver.solve(landmarkToCamera, angles);
				if(Settings.verbose >= 3)
				{
				System.out.println("Camera position: "+cally.cameraPositions[camID]);
				System.out.println("Line direction: "+lineDirection);
				}
				return new Line(cally.cameraPositions[camID], lineDirection);
			}
		}
	}	
}