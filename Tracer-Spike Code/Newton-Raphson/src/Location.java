/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author cravingoxygen
 */
public class Location 
{
    public double x;
    public double y;
    public double z;
    public double oXY;
    public double oXZ;
    
    public static int numVars = 5;
    
    public Location(double _x, double _y, double _z, double _oXY, double _oXZ)
    {
        x = _x;
        y = _y;
        z = _z;
        oXY = _oXY;
        oXZ = _oXZ;
    }
    
    public Location(double[] XYZOxyOxz)
    {
        x = XYZOxyOxz[0];
        y = XYZOxyOxz[1];
        z = XYZOxyOxz[2];
        oXY = XYZOxyOxz[3];
        oXZ = XYZOxyOxz[4];
    }

}
