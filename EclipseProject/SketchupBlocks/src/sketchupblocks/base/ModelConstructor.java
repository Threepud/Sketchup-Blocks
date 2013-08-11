package sketchupblocks.base;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import sketchupblocks.database.*;
import sketchupblocks.calibrator.*;
import sketchupblocks.math.Line;
import sketchupblocks.math.LinearSystemSolver;
import sketchupblocks.math.Vec3;
import sketchupblocks.network.Lobby;

public class ModelConstructor
{
	private Lobby eddy;
	private HashMap<Integer,BlockInfo> blocks;
	
	private Calibrator cally;
	private SessionManager sessMan;
	
	private boolean calibrated = false;
	
	public ModelConstructor(SessionManager _sessMan)
	{
		blocks = new HashMap<Integer,BlockInfo>();
		cally = new Calibrator();
		sessMan = _sessMan;
	}
	  
	public void setLobby(Lobby lobby)
	{
		eddy = lobby;
	}

	  
	public void receiveBlock(InputBlock iBlock)
	{
		if (iBlock.block.blockType == Block.BlockType.COMMAND && ((CommandBlock)iBlock.block).type == CommandBlock.CommandType.CALIBRATE)
		{
			
			boolean changedPosition = cally.processBlock(iBlock);
			calibrated = cally.isCalibrated();
			/*if (calibrated && changedPosition)
			{
				if(Settings.verbose >= 3 )
					System.out.println("==Cameras are calibrated==");
				for(int k = 0 ; k < cally.cameraPositions.length ; k++)
				{
					sessMan.updateCameraPosition(k, cally.cameraPositions[k]);
					System.out.println(cally.cameraPositions[k].x+":"+cally.cameraPositions[k].y+":"+cally.cameraPositions[k].z);
				}
				
				Iterator<java.util.Map.Entry<Integer, Bin>> iter = binList.entrySet().iterator();
				while(iter.hasNext())
				{
					Camera bin = iter.next().getValue();
					if (bin.ready())
						processBin(bin);
				}
			}*/
		}
		else 
		{
			store(iBlock);
		}
		
		
	}
	
	private void processBin(BlockInfo bin)
	{
		BlockInfo.Fiducial [] fids = null;
		fids = bin.fiducials.values().toArray(fids);
		
		Line [] lines = new Line[fids.length];
		for(int k = 0 ; k < fids.length ; k++)
		{
			lines[k] = fids[k].line;
		}
		
		Vec3 [] positions = new Vec3[fids.length]; //Get from DB
		if (!(bin.smartBlock instanceof SmartBlock))
		{
			return;
		}
		
		SmartBlock sm =(SmartBlock)(bin.smartBlock);
		
		for(int k = 0 ; k < fids.length ; k++)
		{
			int fiducialIndex = -1;
			for(int l = 0 ;l < sm.associatedFiducials.length; l++)
			{
				if(sm.associatedFiducials[l] == fids[k].fiducialsID)
					fiducialIndex = l;
			}
			if(fiducialIndex == -1)
			{
				throw new RuntimeException("Smart Block fiducials don't match");
			}
			positions[k] = sm.fiducialCoordinates[fiducialIndex];
		}
			
		
		//Bin should have enough information to get position.
		ParticleSystemSettings settings = new ParticleSystemSettings();
		settings.eval = new BlockPosition(positions,lines);
		settings.tester = null;
		settings.creator = new ParticleCreator(fids.length,0,100);
		
		settings.particleCount = 100;
		settings.iterationCount= 2000;
		
		settings.ringTopology = true;
		settings.ringSize = 1;
		
		settings.socialStart = 0.72;
		settings.cognitiveStart = 0.72;
		settings.momentum = 1.4;
		settings.MaxComponentVelocity = 1;
		
		ParticleSystem system = new ParticleSystem(settings);
		
		Particle bestabc = null;
		
		bestabc = system.go();
		//.................So nou het ek die punte, soortvan.....Wat nou??????
	}
	
	void store(InputBlock iBlock)
	{
		if(iBlock.cameraEvent.type != CameraEvent.EVENT_TYPE.REMOVE)
		{
			BlockInfo block = blocks.get(iBlock.block.blockId);
			
			if(block == null)
			{
				block = new BlockInfo(iBlock.block);
				blocks.put(iBlock.block.blockId,block);
			}	
			
			BlockInfo.Fiducial fiducial = block.fiducials.get(block.new CamFid(iBlock.cameraEvent.cameraID,iBlock.cameraEvent.fiducialID));
			
			if(fiducial == null)
			{
				fiducial =  block.new Fiducial(iBlock.cameraEvent.fiducialID);
				block.fiducials.put(block.new CamFid(iBlock.cameraEvent.cameraID,iBlock.cameraEvent.fiducialID),fiducial);
			}		

			//Line calculation
			Vec3[] landmarkToCamera = new Vec3[4];
			double[] angles = new double[4];
			for (int k = 0; k < 4; k++)
			{
				landmarkToCamera[k] = Vec3.subtract(cally.cameraPositions[iBlock.cameraEvent.cameraID], Settings.landmarks[k]);
				angles[k] = getAngle(iBlock.cameraEvent.cameraID, k, iBlock.cameraEvent.x, iBlock.cameraEvent.y);
			}
			// Do calculation 
			Vec3 mysticalLine = LinearSystemSolver.solve(landmarkToCamera, angles);
			fiducial.line = new Line(cally.cameraPositions[iBlock.cameraEvent.cameraID], mysticalLine);
			
			if(block.ready())
			{
				processBin(block);
			}
		}
		else //When a remove call is received
		{
			BlockInfo block = blocks.get(iBlock.block.blockId);
			
			if(block == null)
			{
				return; // Nothing to remove
			}	
			
			if(block.fiducials.containsKey(block.new CamFid(iBlock.cameraEvent.cameraID,iBlock.cameraEvent.fiducialID)))
			{
				block.fiducials.remove(block.new CamFid(iBlock.cameraEvent.cameraID,iBlock.cameraEvent.fiducialID));
			}	
			
			/*
			* Cleanup -- Remove blocks if not seen
			* No need to clean up camera. They will always be there.
			*/
			
			if(block.fiducials.isEmpty())
			{
				blocks.remove(iBlock.block.blockId);
			}
		}
	}
	
	private double getAngle(int camID, int lm, double x, double y)
	{
		double fov = Settings.cameraSettings[camID].fov;
		double aspect = Settings.cameraSettings[camID].aspectRatio;
		return Math.sqrt(sqr((cally.calibrationDetails[camID][lm][0]- x)*fov)+sqr((cally.calibrationDetails[camID][lm][1]- y)*(fov/aspect))); 
	}
		
	private double sqr(double val)
	{
		return val*val;	
	}
		
	class BlockInfo
	{
		int minEvents = 2;
		int LIFETIME = 2000;	//ms
	
		protected class CamFid
		{
			int cameraID;
			int fiducialID;
			
			CamFid(int c, int f)
			{
				cameraID = c;
				fiducialID = f;
			}
		}
	
		protected class Fiducial
		{
			public Line line;
			public int fiducialsID;
			public Date timestamp;
			
			public Fiducial(int _fiducialsID)
			{
				fiducialsID = _fiducialsID;
				timestamp = new Date();
			}
		}
		
		public BlockInfo(Block _smartBlock)
		{
			fiducials = new HashMap<CamFid,Fiducial>();
			smartBlock = _smartBlock;
		}
		
		public int blockID;
		public Block smartBlock;
		private HashMap<CamFid,Fiducial> fiducials;
		
		public boolean ready()
		{
			Fiducial[] data = new Fiducial[0];
			data = fiducials.values().toArray(data);
			
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
	}	
}