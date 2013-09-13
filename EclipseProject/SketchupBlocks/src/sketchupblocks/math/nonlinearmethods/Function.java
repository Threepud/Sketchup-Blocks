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
    
    public abstract Matrix calcF(Matrix inputs) throws Exception;
    public abstract Matrix calcJ(Matrix inputs) throws Exception;
    
    
    protected double[] extractInput(Matrix inputs) throws Exception
    {
        if (inputs.cols != 1)
            throw new Exception("Invalid dimensions for input");
        double[] res = new double[inputs.rows];
        for (int k = 0; k < res.length; k++)
            res[k] = inputs.data[k][0];
        return res;
    }
}
