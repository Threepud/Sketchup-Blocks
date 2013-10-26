package sketchupblocks.construction;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.*;
import org.junit.runners.*;
import org.junit.runner.RunWith;

import sketchupblocks.base.CameraEvent;
import sketchupblocks.database.Block;
import sketchupblocks.database.Block.BlockType;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;

@RunWith(JUnit4.class)
public class BlockInfoTest {

	BlockInfo bin ;
	
	@Before
	public void setup()
	{
		Block block = new Block();
		block.blockId = 1;;
		block.associatedFiducials = new int[]{0, 1 ,2, 3 ,4,5} ;
		block.fiducialCoordinates = new Vec3[]{new Vec3(), new Vec3() , new Vec3(),  new Vec3() , new Vec3(), new Vec3()};
		block.fiducialOrient = new Vec3[]{new Vec3(), new Vec3() , new Vec3(),  new Vec3() , new Vec3(), new Vec3()};
		block.blockType = Block.BlockType.SMART;
		bin = new BlockInfo(block);
	}
	
	@Test
	public void seenTest()
	{
		CameraEvent ce = new CameraEvent();
		ce.cameraID = 0;
		ce.fiducialID = 1;
		ce.type = CameraEvent.EVENT_TYPE.UPDATE;
		try {Thread.sleep(100);} catch (InterruptedException e) {}
		Date n = new Date();
		bin.updateFiducial(ce);
		assert(bin.getLastSeen().getTime() - n.getTime() < 30);		
	}
	
	@Test
	public void changeTest()
	{	
		try {Thread.sleep(100);} catch (InterruptedException e) {}
		
		Date n = new Date();
		bin.setTransform(Matrix.identity(4), 1);
		assert(bin.getLastSeen().getTime() - n.getTime() < 30);		
	}
	
	@Test
	public void addToMap()
	{
		CameraEvent ce = new CameraEvent();
		ce.cameraID = 0;
		ce.fiducialID = 1;
		ce.type = CameraEvent.EVENT_TYPE.UPDATE;
		bin.updateFiducial(ce);
		assert(bin.mapContainsKey(0, 1));
	}
	
	
	@Test
	public void correctFiducialGet()
	{
		CameraEvent ce = new CameraEvent();
		ce.cameraID = 0;
		ce.fiducialID = 1;
		ce.x = 0.5f;
		ce.y = 0.55f;
		ce.type = CameraEvent.EVENT_TYPE.UPDATE;
		bin.updateFiducial(ce);
		BlockInfo.Fiducial fid = bin.getFiducial(ce.cameraID,ce.fiducialID);
		assert(fid.camID == ce.cameraID);
		assert(fid.fiducialsID == ce.fiducialID);
		assert(fid.fiducialsID == ce.fiducialID);
		assert(fid.camViewX == ce.x);
		assert(fid.camViewY == ce.y);
		
	
	}
}
