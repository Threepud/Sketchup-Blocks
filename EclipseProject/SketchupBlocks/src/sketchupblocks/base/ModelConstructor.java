package sketchupblocks.base;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import sketchupblocks.calibrator.Calibrator;
import sketchupblocks.math.Line;
import sketchupblocks.math.LinearSystemSolver;
import sketchupblocks.math.Vec3;
import sketchupblocks.network.Lobby;

public class ModelConstructor
{
	private Lobby eddy;
	private HashMap<Integer,Camera> cameras;
	
	private Calibrator cally;
	private SessionManager sessMan;
	
	private boolean calibrated = false;
	
	public ModelConstructor(SessionManager _sessMan)
	{
		cameras = new HashMap<Integer,Camera>();
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
	
	private void processBin(Camera.Block bin)
	{
	//	sessMan.
		Vec3 [] positions = null; //Get from DB
		
		Camera.Block.Fiducial [] fids = null;
		fids = bin.fiducials.values().toArray(fids);
		Line [] lines = new Line[fids.length];
		for(int k = 0 ; k < fids.length ; k++)
			lines[k] = fids[k].line;
		
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
		//.................So nou het ek die punte.....Wat nou??????
	}
	
	void store(InputBlock iBlock)
	{
		if(iBlock.cameraEvent.type != CameraEvent.EVENT_TYPE.REMOVE)
		{
			Camera camera = cameras.get(iBlock.cameraEvent.cameraID);
			if(camera == null)
			{
				camera = new Camera(iBlock.cameraEvent.cameraID);
				cameras.put(iBlock.cameraEvent.cameraID,camera);
			}
			
			Camera.Block block = camera.blocks.get(iBlock.block.blockId);
			
			if(block == null)
			{
				block = camera.new Block(iBlock.block.blockId);
				camera.blocks.put(iBlock.block.blockId,block);
			}	
			
			Camera.Block.Fiducial fiducial = block.fiducials.get(iBlock.cameraEvent.fiducialID);
			
			if(fiducial == null)
			{
				fiducial =  block.new Fiducial(iBlock.cameraEvent.fiducialID);
				block.fiducials.put(iBlock.cameraEvent.fiducialID,fiducial);
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
		else //When a remove call is recieved
		{
			
			Camera camera = cameras.get(iBlock.cameraEvent.cameraID);
			if(camera == null)
			{
			return; // Nothing to remove
			}
			
			Camera.Block block = camera.blocks.get(iBlock.block.blockId);
			
			if(block == null)
			{
			return; // Nothing to remove
			}	
			
			if( block.fiducials.containsKey(iBlock.cameraEvent.fiducialID))
			{
				block.fiducials.remove(iBlock.cameraEvent.fiducialID);
			}	
			
			/*
			* Cleanup -- Remove blocks if not seen
			* No need to clean up camera. They will allways be there.
			*/
			
			if(camera.blocks.isEmpty())
			{
				camera.blocks.remove(iBlock.block.blockId);
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
	
	private class Camera
	{		
		class Block
		{
			class Fiducial
			{
				public Fiducial(int _fiducialsID)
				{
					fiducialsID = _fiducialsID;
					timestamp = new Date();
				}
				
				public Line line;
				public int fiducialsID;
				public Date timestamp;
			}
			
			public Block(int _blockID)
			{
				fiducials = new HashMap<Integer,Fiducial>();
				blockID = _blockID;
			}
			
			public int blockID;
			private HashMap<Integer,Fiducial> fiducials;
			
			boolean ready()
			{
			//	return false;
				Camera.Block.Fiducial [] data = new Fiducial[0];
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
		
		private HashMap<Integer,Block> blocks;
		public int cameraID;
		
		int minEvents = 2;
		int LIFETIME = 2000;	//ms
		
		public Camera(int _cameraID)
		{
		cameraID = _cameraID;
		}
	
		
		
		
	}
}