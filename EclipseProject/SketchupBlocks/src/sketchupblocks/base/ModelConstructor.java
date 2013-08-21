package sketchupblocks.base;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import sketchupblocks.database.*;
import sketchupblocks.calibrator.*;
import sketchupblocks.math.Line;
import sketchupblocks.math.LinearSystemSolver;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;
import sketchupblocks.math.Vec4;
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
		
		BlockInfo.CamFid [] camIDs = null;
		camIDs = bin.fiducials.keySet().toArray(camIDs);
		
		int numFiducials = fids.length;
		
		Line [] lines = new Line[numFiducials];
		for(int k = 0 ; k < fids.length ; k++)
		{
			lines[k] = fids[k].line;
		}
		
		Vec3 [] positions = new Vec3[numFiducials]; //Get from DB
		if (!(bin.smartBlock instanceof SmartBlock))
		{
			return;
		}
		
		SmartBlock sm =(SmartBlock)(bin.smartBlock);
		
		for(int k = 0 ; k < numFiducials ; k++)
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
		//Nou het ek die positions van die fiducials in 3D space nodig, die kameras se viewvectors en die Smart blocks wat involved is.
		Vec3 [] fiducialWorld = new Vec3[numFiducials];
		Vec3 [] rotations = new Vec3[numFiducials];
			for(int k = 0 ; k < numFiducials ; k++)
				{
<<<<<<< HEAD
					fiducialWorld[k] = Vec3.add(lines[k].point, Vec3.scalar(bestabc.bestPosition[k], lines[k].direction));
=======
					fiducialWorld[k] = Vec3.add(lines[k].point,Vec3.scalar(bestabc.bestPosition[k], lines[k].direction));
					
					RotationMatrix rTry = new RotationMatrix(fids[k].rotation);
					rotations[k] = getUpVector(camIDs[k].cameraID);
					rotations[k] =  Matrix.multiply(rTry, new Vec4(rotations[k])).toVec3();
>>>>>>> Added code to add get rotation
				}
			
		
			
		/*
		* rotations -- the rotations as viewed by the camera
		* fiducialWorld -- Fiducial locations
		* lines[k].direction -- the k'th fiducial view vector
		* sm -- The smart block
		*/
		
	}
	
	Vec3 getUpVector(int CamID)
	{
		Vec3[] landmarkToCamera = new Vec3[4];
		double[] angles = new double[4];
		for (int k = 0; k < 4; k++)
		{
			landmarkToCamera[k] = Vec3.subtract(cally.cameraPositions[CamID], Settings.landmarks[k]);
			angles[k] = getAngle(CamID, k, 0.5, 0.5+0.01);
		}
		// Do calculation 
		Vec3 mysticalLine = LinearSystemSolver.solve(landmarkToCamera, angles);
		Line top = new Line(cally.cameraPositions[CamID], mysticalLine);
		
		landmarkToCamera = new Vec3[4];
		angles = new double[4];
		for (int k = 0; k < 4; k++)
		{
			landmarkToCamera[k] = Vec3.subtract(cally.cameraPositions[CamID], Settings.landmarks[k]);
			angles[k] = getAngle(CamID, k, 0.5, 0.5-0.01);
		}
		// Do calculation 
		mysticalLine = LinearSystemSolver.solve(landmarkToCamera, angles);
		Line bottom = new Line(cally.cameraPositions[CamID], mysticalLine);
		
		top.direction.normalize();
		bottom.direction.normalize();
		
		return Vec3.subtract(top.direction, bottom.direction);
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
				fiducial =  block.new Fiducial(iBlock.cameraEvent.fiducialID,iBlock.cameraEvent.rotation);
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
			public double rotation;
			public int fiducialsID;
			public Date timestamp;
			
			public Fiducial(int _fiducialsID, double rot)
			{
				fiducialsID = _fiducialsID;
				rotation = rot;
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