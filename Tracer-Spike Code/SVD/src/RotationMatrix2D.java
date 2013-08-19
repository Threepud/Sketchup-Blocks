

public class RotationMatrix2D extends Matrix
{
	RotationMatrix2D(double theta)
	{
		super(2, 2);
		double[][] d = new double[2][];
		d[0] = new double[]{Math.cos(theta), -1*Math.sin(theta)};
		d[1] = new double[]{Math.sin(theta), Math.cos(theta)};
		this.data = d;
	}
	
	void updateTheta(double theta)
	{
		double[][] d = new double[2][];
		d[0] = new double[]{Math.cos(theta), -1*Math.sin(theta)};
		d[1] = new double[]{Math.sin(theta), Math.cos(theta)};
		this.data = d;
	}
}
