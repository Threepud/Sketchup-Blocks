package sketchupblocks.math;

/**
 *
 * @author cravingoxygen
 */
public class RotationMatrix3D extends Matrix
{
	private Axis axis;
    public RotationMatrix3D(double theta, Axis _axis)
    {
        super(3, 3);
        axis = _axis;
        
        double[][] d = new double[3][];
        switch(axis)
        {
        	case X_AXIS:
	        		d[0] = new double[]{1, 0, 0};
	        		d[1] = new double[]{0, Math.cos(theta), Math.sin(theta)};
			        d[2] = new double[]{0, -1*Math.sin(theta), Math.cos(theta)};
	        			
			break;
        	case Y_AXIS:
        			d[0] = new double[]{Math.cos(theta), 0, -1*Math.sin(theta)};
	        		d[1] = new double[]{0, 1, 0};
			        d[2] = new double[]{Math.sin(theta), 0, Math.cos(theta)};
			break;
        	case Z_AXIS:
	        		d[0] = new double[]{Math.cos(theta), Math.sin(theta), 0};
			        d[1] = new double[]{-1*Math.sin(theta), Math.cos(theta), 0};
			        d[2] = new double[]{0, 0, 1};
			break;
        }
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