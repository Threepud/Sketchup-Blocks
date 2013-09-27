package sketchupblocks.math.nonlinearmethods;

import sketchupblocks.math.Matrix;

/**
 *
 * @author user
 */
public abstract class Function 
{
    protected int numParameters;
    
    public Function(int _numParams)
    {
        numParameters = _numParams;
    }
    
    public abstract Matrix calcFunction(Matrix inputs);
    public abstract Matrix calcJacobian(Matrix inputs);
    
    
    protected double[] extractInput(Matrix inputs)
    {
        if (inputs.cols != 1)
            throw new RuntimeException("Invalid dimensions for input");
        double[] res = new double[inputs.rows];
        for (int k = 0; k < res.length; k++)
            res[k] = inputs.data[k][0];
        return res;
    }
}
