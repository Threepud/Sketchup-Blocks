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
    public double o;
    
    public static int numVars = 3;
    
    public Location(double _x, double _y, double _o)
    {
        x = _x;
        y = _y;
        o = _o;
    }
    
    public Location(double[] xyo)
    {
        x = xyo[0];
        y = xyo[1];
        o = xyo[2];
    }

}
