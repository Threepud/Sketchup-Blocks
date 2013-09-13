package sketchupblocks.math.nonlinearmethods;

import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;

/**
 *
 * @author cravingoxygen
 */
public class BPos extends Function
{
    private int numEquations;
    private Vec3[] camPos;
    private Vec3[] dirs;
    private double[] dists;
    
    public BPos(int _numPoints, Vec3[] _camPos, Vec3[] _dirs, double[] _dists)
    {
        super(_numPoints);
        numEquations = _numPoints*(_numPoints-1)/2;
        camPos = _camPos;
        dirs = _dirs;
        dists = _dists;
    }

    @Override
    public Matrix calcF(Matrix inputs) throws Exception
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
        //System.out.println("Returning F: ");
        //System.out.println(new Matrix(res));
        return new Matrix(res);
    }
    
    @Override
    public Matrix calcJ(Matrix inputs) throws Exception 
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
                sum += 2*dirs[k].x*diff.x;
                sum += 2*dirs[k].y*diff.y;
                sum += 2*dirs[k].z*diff.y;
                res[num][k] = sum;
                
                sum = 0;
                sum -= 2*dirs[i].x*diff.x;
                sum += 2*dirs[i].y*diff.y;
                sum += 2*dirs[i].z*diff.y;
                res[num][i] = sum;
                num++;
            }
        }
        //System.out.println("Returning J: ");
        //System.out.println(new Matrix(res));
        return new Matrix(res);
    }
    
    private Vec3 evalPoint(int k, int i, double[] lambdas)
    {
    	//System.out.println(lambdas.length);
        Vec3 res = Vec3.subtract(camPos[k], camPos[i]);
        res = Vec3.add(res, Vec3.scalar(lambdas[k], dirs[k]));
        res = Vec3.add(res, Vec3.scalar(-lambdas[i], dirs[i]));
        return res;
    }
    
}
