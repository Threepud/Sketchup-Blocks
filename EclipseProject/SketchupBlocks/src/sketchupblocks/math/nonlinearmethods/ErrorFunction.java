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
    
    public double calcG(Matrix inputs)
    {
        Matrix fout = f.calcF(inputs);
        double res = 0;
        
        for (int k = 0; k < fout.rows; k++)
            res += fout.data[k][0]*fout.data[k][0];
        return res;
    }
    
    public Matrix calcDelG(Matrix inputs)
    {
        Matrix J = f.calcJ(inputs);
        Matrix F = f.calcF(inputs);
        return Matrix.multiply(Matrix.scalar(2, J).transpose(), F);
    }
    
    @Override
    public Matrix calcF(Matrix inputs)
    {
        return f.calcF(inputs);
    }

    @Override
    public Matrix calcJ(Matrix inputs)
    {
        return f.calcJ(inputs);
    }
    
}
