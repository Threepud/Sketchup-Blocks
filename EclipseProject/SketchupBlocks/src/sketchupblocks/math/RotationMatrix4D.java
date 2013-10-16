package sketchupblocks.math;

import sketchupblocks.math.Matrix.Axis;

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
        
        double[][] d = new double[4][];
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
	
	public RotationMatrix4D(double x, double y, double z)
    {
        super(4, 4);
        axis = Axis.X_AXIS;
        
		double[][] dx = new double[4][];
		dx[0] = new double[]{1, 0, 0, 0};
		dx[1] = new double[]{0, Math.cos(x), Math.sin(x), 0};
	    dx[2] = new double[]{0, -1*Math.sin(x), Math.cos(x), 0};
	    dx[3] = new double[]{0, 0, 0, 1};
	    Matrix mx = new Matrix(dx);  			

	    double[][] dy = new double[4][];
		dy[0] = new double[]{Math.cos(y), 0, -1*Math.sin(y), 0};
		dy[1] = new double[]{0, 1, 0, 0};
        dy[2] = new double[]{Math.sin(y), 0, Math.cos(y), 0};
        dy[3] = new double[]{0, 0, 0, 1};
        Matrix my = new Matrix(dy);
        
        double[][] dz = new double[4][];
		dz[0] = new double[]{Math.cos(z), Math.sin(z), 0, 0};
        dz[1] = new double[]{-1*Math.sin(z), Math.cos(z), 0, 0};
        dz[2] = new double[]{0, 0, 1, 0};
        dz[3] = new double[]{0, 0, 0, 1};
        Matrix mz = new Matrix(dy); 
        
	
        
        this.data = Matrix.multiply(mz, Matrix.multiply(my, mx)).data;
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
