package sketchupblocks.math;

import java.io.Serializable;

public class Vec3 implements Serializable
{
	private static final long serialVersionUID = 1L;
    public double x;
    public double y;
    public double z;

    public Vec3()
    {
        x = 0;
        y = 0;
        z = 0;
    }

    public Vec3(double _x, double _y, double _z)
    {
        x = _x;
        y = _y;
        z = _z;
    }

    public Vec3(double[] xyz)
    {
        x = xyz[0];
        y = xyz[1];
        z = xyz[2];
    }
    
    public Vec3(Vec3 oldVec)
    {
    	x = oldVec.x;
    	y = oldVec.y;
    	z = oldVec.z;
    }
    

    public Vec3 clone()
    {
    	return new Vec3(x, y, z);
    }

    public static Vec3 midpoint(Vec3 one, Vec3 two)
    {
        return new Vec3((one.x+two.x)/2.0, (one.y+two.y)/2.0, (one.z+two.z)/2.0);
    }
    
    public static double dot(Vec3 one, Vec3 two)
    {
        return (one.x * two.x + one.y * two.y + one.z * two.z);
    }

    public static Vec3 cross(Vec3 one, Vec3 two)
    {
        double newX = one.y * two.z - one.z * two.y;
        double newY = one.z * two.x - one.x * two.z;
        double newZ = one.x * two.y - one.y * two.x;

        return new Vec3(newX, newY, newZ);
    }

    public static Vec3 normalize(Vec3 one)
    {
        double magnitude = Math.sqrt(one.x * one.x + one.y * one.y + one.z * one.z);

        Vec3 two = new Vec3(one);
        two.x = two.x / magnitude;
        two.y = two.y / magnitude;
        two.z = two.z / magnitude;

        return two;
    }
    
    public double dot(Vec3 other)
    {
    	return (x*other.x + y*other.y + z*other.z);
    }

    public void normalize()
    {
        double magnitude = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);

        this.x = this.x / magnitude;
        this.y = this.y / magnitude;
        this.z = this.z / magnitude;
    }

    public double length()
    {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public static double length(Vec3 one)
    {
        return Math.sqrt(one.x * one.x + one.y * one.y + one.z * one.z);
    }

    public static Vec3 add(Vec3 one, Vec3 two)
    {
        return new Vec3(one.x + two.x, one.y + two.y, one.z + two.z);
    }

    public static Vec3 subtract(Vec3 one, Vec3 two)
    {
        return new Vec3(one.x - two.x, one.y - two.y, one.z - two.z);
    }

    public static Vec3 scalar(double constant, Vec3 one)
    {
        return new Vec3(one.x * constant, one.y * constant, one.z * constant);
    }

    public double[] toArray()
    {
        return new double[]{x, y, z};
    }
    
    public double distance(Vec3 vec)
    {
    	return Math.sqrt((this.x - vec.x) * (this.x - vec.x) + (this.y - vec.y) * (this.y - vec.y) + (this.z - vec.z) * (this.z - vec.z)); 
    }
    
    public Vec3 project(Vec3 b1, Vec3 b2)
    {
    	b1 = Vec3.normalize(b1);
    	b2 = Vec3.normalize(b2);
    	Vec3 one = Vec3.scalar(Vec3.dot(b1,  this), b1);
    	Vec3 two = Vec3.scalar(Vec3.dot(b2,  this), b2);
    	return Vec3.add(one, two);
    }
    
    public Vec4 padVec3()
    {
    	return new Vec4(x, y, z, 1);
    }
    
    @Override
    public String toString()
    {
        return "["+x+"; "+y+"; "+z+"]";
    }
    
    @Override
    public boolean equals(Object o)
    {
    	if (o instanceof Vec3)
    	{
    		Vec3 v = (Vec3)o;
    		if (x == v.x && y == v.y && z == v.z)
    		return true;
    	}
		return false;
    }
}
