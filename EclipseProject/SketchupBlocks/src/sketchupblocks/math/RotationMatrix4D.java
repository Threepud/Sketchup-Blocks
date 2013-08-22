package sketchupblocks.math;

public class RotationMatrix4D extends Matrix 
{
	public RotationMatrix4D()
	{
		super(4, 4);
	}
	
	public RotationMatrix4D (double theta)
    {
        super(4, 4);
        double[][] d = new double[4][];
        d[0] = new double[]{Math.cos(theta), -1*Math.sin(theta), 0, 0};
        d[1] = new double[]{Math.sin(theta), Math.cos(theta), 0, 0};
        d[2] = new double[]{0, 0, 1, 0};
        d[3] = new double[]{0, 0, 0, 1};
        this.data = d;
    }

    public void updateTheta(double theta)
    {
        data[0][0] = Math.cos(theta);
        data[0][1] = -1*Math.sin(theta);
        
        data[1][0] = Math.sin(theta);
        data[1][1] = Math.cos(theta);
        
        for(int x = 2; x < rows; ++x)
        	data[x][x] = 1;
    }
}
