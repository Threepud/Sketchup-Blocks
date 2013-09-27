package sketchupblocks.math.nonlinearmethods;

import sketchupblocks.math.Line;
import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;

/**
 *
 * @author cravingoxygen
 * @about Block position calculator. This calculates the length of the line between a camera and a block.
 */
public class BPos extends Function
{
    private int numEquations;
    private Vec3[] camPos;
    private Vec3[] dirs;
    private double[] dists;
    
    public BPos(int _numPoints, Line[] _lines, double[] _dists)
    {
        super(_numPoints);
        numEquations = _numPoints*(_numPoints-1)/2;
        camPos = new Vec3[_lines.length];
        dirs = new Vec3[_lines.length];
        for (int k = 0; k < _lines.length; k++)
        {
        	camPos[k] = _lines[k].point;
        	dirs[k] = _lines[k].direction;
        }
        dists = _dists;
    }

    @Override
    public Matrix calcFunction(Matrix inputs)
    {
        double[] lambdas = extractInput(inputs);
        
        double[] res = new double[numEquations];
        int num = 0;
        
        for (int k = 0; k < numParameters-1; k++)
        {
            for (int i = k+1; i < numParameters; i++)
            {
                Vec3 diff = evalPoint(k, i, lambdas);
                double sum = 0;
                sum += diff.x*diff.x;
                sum += diff.y*diff.y;
                sum += diff.z*diff.z;
                sum -= dists[num]*dists[num];
                res[num++] = sum;
            }
        }
        return new Matrix(res);
    }
    
    @Override
    public Matrix calcJacobian(Matrix inputs)
    {
        double[] lambdas = extractInput(inputs);
        
        double[][] res = new double[numEquations][numParameters];
        
        int num = 0;
        for (int k = 0; k < numParameters-1; k++)
        {
            for (int i = k+1; i < numParameters; i++)
            {
                Vec3 diff = evalPoint(k, i, lambdas);
                double sum = 0;
                sum += dirs[k].x*diff.x;
                sum += dirs[k].y*diff.y;
                sum += dirs[k].z*diff.y;
                res[num][k] = 2*sum;
                
                sum = 0;
                sum += dirs[i].x*diff.x;
                sum += dirs[i].y*diff.y;
                sum += dirs[i].z*diff.y;
                res[num][i] = -2*sum;
                num++;
            }
        }
        return new Matrix(res);
    }
    
    private Vec3 evalPoint(int k, int i, double[] lambdas)
    {
        Vec3 res = Vec3.subtract(camPos[k], camPos[i]);
        res = Vec3.add(res, Vec3.scalar(lambdas[k], dirs[k]));
        res = Vec3.add(res, Vec3.scalar(-lambdas[i], dirs[i]));
        return res;
    }
    
}
