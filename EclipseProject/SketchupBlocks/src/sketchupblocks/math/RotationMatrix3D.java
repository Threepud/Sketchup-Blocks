package sketchupblocks.math;

/**
 *
 * @author cravingoxygen
 */
public class RotationMatrix3D extends Matrix
{
    public RotationMatrix3D (double theta)
    {
        super(3, 3);
        double[][] d = new double[3][];
        d[0] = new double[]{Math.cos(theta), -1*Math.sin(theta), 0};
        d[1] = new double[]{Math.sin(theta), Math.cos(theta), 0};
        d[2] = new double[]{0, 0, 1};
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