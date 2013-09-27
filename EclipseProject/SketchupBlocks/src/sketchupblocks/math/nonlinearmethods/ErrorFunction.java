package sketchupblocks.math.nonlinearmethods;

import sketchupblocks.math.Matrix;

/**
 *
 * @author user
 */
public class ErrorFunction extends Function
{
    protected Function f;
    
    public ErrorFunction(Function _f)
    {
        super(_f.numParameters);
        f = _f;
    }
    
    public double calcError(Matrix inputs)
    {
        Matrix fout = f.calcFunction(inputs);
        double res = 0;
        
        for (int k = 0; k < fout.rows; k++)
            res += fout.data[k][0]*fout.data[k][0];
        return res;
    }
    
    public Matrix calcDelError(Matrix inputs)
    {
        Matrix J = f.calcJacobian(inputs);
        Matrix F = f.calcFunction(inputs);
        return Matrix.multiply(Matrix.scalar(2, J).transpose(), F);
    }
    
    @Override
    public Matrix calcFunction(Matrix inputs)
    {
        return f.calcFunction(inputs);
    }

    @Override
    public Matrix calcJacobian(Matrix inputs)
    {
        return f.calcJacobian(inputs);
    }
    
}
