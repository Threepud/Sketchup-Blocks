package sketchupblocks.math.nonlinearmethods;

import sketchupblocks.math.Matrix;
import sketchupblocks.math.Vec3;

/**
 * @author cravingoxygen
 *@about Camera Position calculator by using output of sphere intersection function.
 */
public class CP extends Function
{
    private Vec3[] landmarks;
    private double[] radii;
    private int numEquations;
    
    public CP(Vec3[] _lPos, double[] _radii)
    {
        super(3);
        numEquations = 4;
        landmarks = _lPos;
        radii = _radii;
    }
    
    @Override
    public Matrix calcFunction(Matrix inputs)
    {
        double[] cPos = extractInput(inputs);
        double[][] data = new double[numEquations][1];
        
        for (int row = 0; row < numEquations; row++)
        {
            double[] L = landmarks[row].toArray();
            for (int comp = 0; comp < cPos.length; comp++)
            {
                data[row][0] += (L[comp] - cPos[comp])*(L[comp] - cPos[comp]);
            }
            data[row][0] -= radii[row]*radii[row];
        }
        
        return new Matrix(data);
    }

    @Override
    public Matrix calcJacobian(Matrix inputs)
    {
        double[] cPos = extractInput(inputs);
        double[][] data = new double[numEquations][cPos.length];
        
        for (int k = 0; k < numEquations; k++)
        {
            double[] lm = landmarks[k].toArray();
            for (int i = 0; i < lm.length; i++)
            {
                data[k][i] = -2*(lm[i] - cPos[i]);
            }
        }
        
        return new Matrix(data);
    }
    
}
