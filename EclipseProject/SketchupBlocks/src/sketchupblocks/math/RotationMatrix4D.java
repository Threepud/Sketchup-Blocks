package sketchupblocks.math;

public class RotationMatrix4D extends Matrix 
{
	private static final long serialVersionUID = 1L;
	Axis axis;
	public RotationMatrix4D()
	{
		super(4, 4);
	}
	
	public RotationMatrix4D (double theta, Axis _axis)
    {
        super(4, 4);
        axis = _axis;
        
        double[][] d = new double[3][];
        switch(axis)
        {
        	case X_AXIS:
	        		d[0] = new double[]{1, 0, 0, 0};
	        		d[1] = new double[]{0, Math.cos(theta), Math.sin(theta), 0};
			        d[2] = new double[]{0, -1*Math.sin(theta), Math.cos(theta), 0};
	        			
			break;
        	case Y_AXIS:
        			d[0] = new double[]{Math.cos(theta), 0, -1*Math.sin(theta), 0};
	        		d[1] = new double[]{0, 1, 0, 0};
			        d[2] = new double[]{Math.sin(theta), 0, Math.cos(theta), 0};
			break;
        	case Z_AXIS:
	        		d[0] = new double[]{Math.cos(theta), Math.sin(theta), 0, 0};
			        d[1] = new double[]{-1*Math.sin(theta), Math.cos(theta), 0, 0};
			        d[2] = new double[]{0, 0, 1, 0};
			break;
        }
        d[3] = new double[]{0, 0, 0, 1};
        this.data = d;
    }

    public void updateTheta(double theta)
    {
    	switch(axis)
        {
        	case X_AXIS:
	        		data[1][1] = Math.cos(theta);
	        		data[1][2] = Math.sin(theta);
	        		data[2][1] = -1*Math.sin(theta);
	        		data[2][2] = Math.cos(theta);
	        			
			break;
        	case Y_AXIS:
	        		data[0][0] = Math.cos(theta);
	        		data[0][2] = -1*Math.sin(theta);
	        		data[2][0] = Math.sin(theta);
	        		data[2][2] = Math.cos(theta);
			break;
        	case Z_AXIS:
	        		data[0][0] = Math.cos(theta);
	        		data[0][1] = Math.sin(theta);
			        data[1][0] = -1*Math.sin(theta);
			        data[1][1] = Math.cos(theta);
			break;
        }
    }
}
