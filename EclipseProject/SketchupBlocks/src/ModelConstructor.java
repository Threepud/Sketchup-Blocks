import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


public class ModelConstructor
{
	private Lobby eddy;
	private HashMap<Integer,Bin> binList;
	
	private Calibrator cally;
	private SessionManager sessMan;
	
	private boolean calibrated = false;
	
	public ModelConstructor(SessionManager _sessMan)
	{
		binList = new HashMap<Integer,Bin>();
		cally = new Calibrator();
		sessMan = _sessMan;
	}
	  
	public void setLobby(Lobby lobby)
	{
		eddy = lobby;
	}

	  
	public void receiveBlock(InputBlock iBlock)
	{
		Bin currentBin = null;
		if(binList.containsKey(iBlock.block.blockId))
		{
			currentBin = binList.get(iBlock.block.blockId);
			currentBin.store(iBlock);
		}
		if (iBlock.block.blockType == Block.BlockType.COMMAND && ((CommandBlock)iBlock.block).type == CommandBlock.CommandType.CALIBRATE)
		{
			
			boolean changedPosition = cally.processBlock(iBlock);
			calibrated = cally.isCalibrated();
			if (calibrated && changedPosition)
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
					Bin bin = iter.next().getValue();
					if (bin.ready())
						processBin(bin);
				}
			}
		}
		else 
		{
			currentBin = new Bin(iBlock);
			currentBin.minEvents = (iBlock.block.blockType == Block.BlockType.SMART) ? 2 : 1 ;
			//currentBin.store(iBlock);
			binList.put(iBlock.block.blockId, currentBin);
		}
		
		if(currentBin != null && currentBin.ready() && calibrated)
		{
			processBin(currentBin);
		}		
		
	}
	
	private void processBin(Bin bin)
	{
		
	}
	
	private class Bin
	{
		int minEvents;
		int LIFETIME = 2000;	//ms
		InputBlock[] data;// = new InputBlock[Settings.numCameras];
		Line[] lines;// = new Line[Settings.numCameras];
		
		Bin(InputBlock iBlock)
		{
			data = new InputBlock[Settings.numCameras];//[iBlock.block.associatedFiducials.length];
			lines = new Line[Settings.numCameras];//[iBlock.block.associatedFiducials.length];
			store(iBlock);
		}
		
		void store(InputBlock iBlock)
		{
			//data[iBlock.cameraEvent.cameraID][iBlock.cameraEvent. = iBlock;
			
			Vec3[] landmarkToCamera = new Vec3[4];
			double[] angles = new double[4];
			for (int k = 0; k < 4; k++)
			{
				landmarkToCamera[k] = Vec3.subtract(cally.cameraPositions[iBlock.cameraEvent.cameraID], Settings.landmarks[k]);
				angles[k] = getAngle(iBlock.cameraEvent.cameraID, k, iBlock.cameraEvent.x, iBlock.cameraEvent.y);
			}
			
			Vec3 mysticalLine = LinearSystemSolver.solve(landmarkToCamera, angles);
			lines[iBlock.cameraEvent.cameraID] = new Line(cally.cameraPositions[iBlock.cameraEvent.cameraID], mysticalLine);
		}
		
		boolean ready()
		{
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
			else return false;
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
	}
}