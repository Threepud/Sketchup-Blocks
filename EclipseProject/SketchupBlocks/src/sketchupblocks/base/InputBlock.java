package sketchupblocks.base;

import java.util.Date;

import sketchupblocks.database.Block;

/**
 * InputBlock is a wrapper class that contains the information relevant to an observed block.
 * In particular, it contains the camera event that observed a fiducial on theb lock.
 * @author cravingoxygen
 *
 */
public class InputBlock
{
    public Block block;
    public CameraEvent cameraEvent;
    public Date timestamp;
    
    public InputBlock(Block _block, CameraEvent cevent)
    {
    	block = _block;
    	cameraEvent = cevent;
    	timestamp = new Date();
    }
    
}