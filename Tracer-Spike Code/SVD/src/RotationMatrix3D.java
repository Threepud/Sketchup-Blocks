/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cravingoxygen
 */
public class RotationMatrix3D extends Matrix
{
    RotationMatrix3D (double theta)
    {
        super(3, 3);
        double[][] d = new double[3][];
        d[0] = new double[]{Math.cos(theta), -1*Math.sin(theta), 0};
        d[1] = new double[]{Math.sin(theta), Math.cos(theta), 0};
        d[2] = new double[]{0, 0, 1};
        this.data = d;
    }

    void updateTheta(double theta)
    {
        double[][] d = new double[3][];
        d[0] = new double[]{Math.cos(theta), Math.sin(theta), 0};
        d[1] = new double[]{-1*Math.sin(theta), Math.cos(theta), 0};
        d[2] = new double[]{0, 0, 1};
        this.data = d;
    }
}