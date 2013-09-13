

public class Vec4 
{
    public double x;
    public double y;
    public double z;
    public double w;

    public Vec4()
    {
        x = 0;
        y = 0;
        z = 0;
        w = 0;
    }

    public Vec4(double _x, double _y, double _z, double _w)
    {
        x = _x;
        y = _y;
        z = _z;
        w = _w;
    }

    public Vec4(double[] xyzw)
    {
        x = xyzw[0];
        y = xyzw[1];
        z = xyzw[2];
        w = xyzw[3];
    }

    public Vec4(Vec3 v)
    {
    	x = v.x;
    	y = v.y;
    	z = v.z;
    	w = 0;
    }
    
    public static double dot(Vec4 one, Vec4 two)
    {
        return (one.x * two.x + one.y * two.y + one.z * two.z + one.w*two.w);
    }

    /*public static Vec4 cross(Vec4 one, Vec4 two)
    {
            double newX = one.y * two.z - one.z * two.y;
            double newY = one.z * two.x - one.x * two.z;
            double newZ = one.x * two.y - one.y * two.x;
            double newW = one.w 

            return new Vec4(newX, newY, newZ);
    }*/

    public static Vec4 normalize(Vec4 one)
    {
        double magnitude = Math.sqrt(one.x * one.x + one.y * one.y + one.z * one.z + one.w* one.w);

        one.x = one.x / magnitude;
        one.y = one.y / magnitude;
        one.z = one.z / magnitude;
        one.w = one.w / magnitude;

        return one;
    }

    public double length()
    {
        return Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public static double length(Vec4 one)
    {
        return Math.sqrt(one.x * one.x + one.y * one.y + one.z * one.z + one.w * one.w);
    }

    public static Vec4 add(Vec4 one, Vec4 two)
    {
        return new Vec4(one.x + two.x, one.y + two.y, one.z + two.z, one.w + two.w);
    }

    public static Vec4 subtract(Vec4 one, Vec4 two)
    {
        return new Vec4(one.x - two.x, one.y - two.y, one.z - two.z, one.w - two.w);
    }

    public static Vec4 scalar(double constant, Vec4 one)
    {
        return new Vec4(one.x * constant, one.y * constant, one.z * constant, one.w * constant);
    }

    public double[] toArray()
    {
        return new double[]{x, y, z, w};
    }
    
    public Vec3 toVec3()
    {
        return new Vec3(x, y, z);
    }
    
    @Override
    public String toString()
    {
        return "["+x+"; "+y+"; "+z+"; "+w+"]";
    }
}
