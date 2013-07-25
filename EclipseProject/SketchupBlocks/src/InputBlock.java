import java.util.Date;

class InputBlock
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