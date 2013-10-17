package sketchupblocks.construction;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import sketchupblocks.base.CameraEvent;
import sketchupblocks.base.RuntimeData;
import sketchupblocks.base.Settings;
import sketchupblocks.database.Block;
import sketchupblocks.base.Logger;
import sketchupblocks.math.Line;
import sketchupblocks.math.LineDirectionSolver;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.SingularMatrixException;
import sketchupblocks.math.Vec3;

public class BlockInfo 
{
	int minEvents = 3;
	int minFidVis = 2;
	int LIFETIME = 1500; //ms
	public int blockID;
	public Block smartBlock;
	public Date lastChange;
	
	//Variables for removal logic.
	public boolean removed = true;
	private Matrix transform = null;
	private int numFiducialsUsed = 0;
	
	protected Map<CamFidIdentifier,Fiducial> fiducialMap;

	
	public BlockInfo(Block _smartBlock)
	{
		blockID = _smartBlock.blockId;
		fiducialMap = new HashMap<CamFidIdentifier,Fiducial>();
		smartBlock = _smartBlock;
		lastChange = new Date();
	}
	
	public synchronized Matrix getTransform()
	{
		return transform.clone();
	}
	
	public synchronized int getNumFiducialsUsed()
	{
		return numFiducialsUsed;
	}
	
	/**/
	public synchronized void setTransform(Matrix _transform, int _numFiducialsUsed)
	{
		transform = _transform;
		numFiducialsUsed = _numFiducialsUsed;
	}
	
	public synchronized Fiducial[] getCleanFiducials()
	{
		BlockInfo.Fiducial [] fids = new BlockInfo.Fiducial[0];
		
		ArrayList<BlockInfo.Fiducial> cleanFids = new ArrayList<BlockInfo.Fiducial>(); //Get all fiducials ever seen
		
		for(BlockInfo.CamFidIdentifier keys : fiducialMap.keySet())
		{
			BlockInfo.Fiducial fid = fiducialMap.get(keys);
			if(fid.isSeen() && !fid.processed)
			{
				cleanFids.add(fid.clone());//Remove the ones that aren't currently visible
			}
		}
		return cleanFids.toArray(fids);	
	}
	
	public synchronized Date getLastSeen()
	{
		Date result = new Date();
		for(Fiducial fid : fiducialMap.values())
		{
			if(result.getTime() > fid.lastSeen.getTime())
				result = (Date)fid.lastSeen.clone();
		}
		return result;
	}
	
	public synchronized boolean ready()
	{
		Fiducial[] data = new Fiducial[0];
		boolean tryToArray = true;
		
		while(tryToArray)
		try
		{
			data = fiducialMap.values().toArray(data);
			tryToArray = false;
		}
		catch(ConcurrentModificationException e)
		{
			//Do nothing
		}
		
		ArrayList<Integer> fiducialList = new ArrayList<Integer>();
		int count = 0;
		for (int k = 0; k < data.length; k++)
		{
			if (data[k] != null)
			{
				if (new Date().getTime() - data[k].timestamp.getTime() < LIFETIME)
				{
					if(data[k].seen && !data[k].processed)
					{
						count ++;
						if( !fiducialList.contains(new Integer(data[k].fiducialsID)))
							fiducialList.add(data[k].fiducialsID);
					}
				}
				else
				{
					data[k] = null;
				}
			}
		}
		
		if (fiducialList.size() >= minFidVis && count >= minEvents)
			return true;
		else 
			return false;
	}
	
	public synchronized int getNumUniqueFiducials()
	{
		Fiducial[] data = new Fiducial[0];
		boolean tryToArray = true;
		
		while(tryToArray)
		try
		{
			data = fiducialMap.values().toArray(data);
			tryToArray = false;
		}
		catch(ConcurrentModificationException e)
		{
			//Do nothing
		}
		
		ArrayList<Integer> fiducialList = new ArrayList<Integer>();
		for (int k = 0; k < data.length; k++)
		{
			if (data[k] != null)
			{
				if(data[k].seen)
				{
					if( !fiducialList.contains(new Integer(data[k].fiducialsID)))
						fiducialList.add(data[k].fiducialsID);
				}
			}
		}

		return fiducialList.size();
	}

	protected class Fiducial
	{
		public int fiducialsID;
		public int camID;
		public double rotation;
		public Date timestamp;
		public double camViewX;
		public double camViewY;
		private boolean seen;
		private Date lastSeen;
		private boolean processed;
		
		public Vec3 worldPosition = null;
		
		public Fiducial(CameraEvent camE)
		{
			this(camE.fiducialID, camE.rotation, camE.x, camE.y, camE.cameraID);
			seen = true;
			processed = false;
		}
		
		public Fiducial clone()
		{
			Fiducial dolly = new Fiducial(fiducialsID, rotation, camViewX, camViewY, camID);
			dolly.timestamp = (Date)this.timestamp.clone();
			dolly.lastSeen = (Date)this.lastSeen.clone();
			dolly.seen = seen;
			dolly.worldPosition = worldPosition.clone();
			return dolly;
		}
		
		public Fiducial(int _fiducialsID, double rot, double _camViewX, double _camViewY, int _camID)
		{
			fiducialsID = _fiducialsID;
			rotation = rot;
			timestamp = new Date();
			camViewX = _camViewX;
			camViewY = _camViewY;
			camID = _camID;
			seen = true;
			lastSeen = new Date();
			processed = false;
		}
		
		
		
		public Line getLine()
		{
			//Line calculation
			Vec3[] landmarkToCamera = new Vec3[4];
			double[] angles = new double[4];
			
			for (int k = 0; k < 4; k++)
			{
				Vec3 camPosition = RuntimeData.getCameraPosition(camID);
				if(camPosition == null)
				{
					throw new RuntimeException("Cameras not calibrated!");				
				}
				landmarkToCamera[k] = Vec3.subtract(Settings.landmarks[k], camPosition);
				angles[k] = RuntimeData.getAngle(camID, k, camViewX, camViewY);
			}
			// Do calculation 
			Vec3 lineDirection;
			try
			{
				lineDirection = LineDirectionSolver.solve(landmarkToCamera, angles);
			}
			catch(SingularMatrixException s)
			{
				s.printStackTrace();
				throw new RuntimeException("No valid line direction");
			}
			
			Logger.log("Camera position: "+ RuntimeData.getCameraPosition(camID), 90);
			Logger.log("Line direction: "+lineDirection, 90);
			
			return new Line(RuntimeData.getCameraPosition(camID), lineDirection);
		}
		
		public boolean isSeen()
		{
			return seen;
		
		}
		
		public void setProcessed()
		{
			processed = true;
		}
		
		public void setSeen(boolean s)
		{
			seen = s;
			if(seen == false)
			{
				lastSeen = new Date();
			}
		}
		
		public Date getLastSeen()
		{
			if(seen)
			{
				return new Date();
			}
			else
			{
				return (Date)lastSeen.clone();
			}			
		}
	
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
}
