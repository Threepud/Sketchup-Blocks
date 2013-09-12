/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
public class SIS extends Function
{
    private double[] d;
    private double[] cosMu;
    private int numEquations = 6;
    
    public SIS(double[] _d, double[] _mu)
    {
        super(4);
        d = _d;
        cosMu = new double[_mu.length];
        for (int k = 0; k < _mu.length; k++)
        {
            cosMu[k] = Math.cos(_mu[k]);
        }
    }
    
    
    @Override
    public Matrix calcF(Matrix inputs) throws Exception
    {
        double[] res = new double[numEquations];
        double[] ABCD = extractInput(inputs);
        
        res[0] = ABCD[0]*ABCD[0] + ABCD[1]*ABCD[1] - 2*ABCD[0]*ABCD[1]*cosMu[0] - d[0]*d[0];
        res[1] = ABCD[1]*ABCD[1] + ABCD[2]*ABCD[2] - 2*ABCD[1]*ABCD[2]*cosMu[1] - d[1]*d[1];
        res[2] = ABCD[2]*ABCD[2] + ABCD[3]*ABCD[3] - 2*ABCD[2]*ABCD[3]*cosMu[2] - d[2]*d[2];
        res[3] = ABCD[3]*ABCD[3] + ABCD[0]*ABCD[0] - 2*ABCD[3]*ABCD[0]*cosMu[3] - d[3]*d[3];
        res[4] = ABCD[0]*ABCD[0] + ABCD[2]*ABCD[2] - 2*ABCD[0]*ABCD[2]*cosMu[4] - d[4]*d[4];
        res[5] = ABCD[1]*ABCD[1] + ABCD[3]*ABCD[3] - 2*ABCD[1]*ABCD[3]*cosMu[5] - d[5]*d[5];
        return new Matrix(res);
    }

    @Override
    public Matrix calcJ(Matrix inputs) throws Exception
    {
        double[][] data = new double[numEquations][];
        double[] ABCD = extractInput(inputs);
        
        int row = 0;
        data[row++] = new double[]{2*ABCD[0] - 2*ABCD[1]*cosMu[0], 2*ABCD[1] - 2*ABCD[0]*cosMu[0], 0, 0 };
        data[row++] = new double[]{0, 2*ABCD[1] - 2*ABCD[2]*cosMu[1], 2*ABCD[2] - 2*ABCD[1]*cosMu[1], 0};
        data[row++] = new double[]{0, 0, 2*ABCD[2] - 2*ABCD[3]*cosMu[2], 2*ABCD[3] - 2*ABCD[2]*cosMu[2]};
        data[row++] = new double[]{2*ABCD[0] - 2*ABCD[3]*cosMu[3], 0, 0, 2*ABCD[3] - 2*ABCD[0]*cosMu[3]};
        data[row++] = new double[]{2*ABCD[0] - 2*ABCD[2]*cosMu[4], 0, 2*ABCD[2] - 2*ABCD[0]*cosMu[4], 0};
        data[row++] = new double[]{0, 2*ABCD[1] - 2*ABCD[3]*cosMu[5], 0, 2*ABCD[3] - 2*ABCD[1]*cosMu[5]};
        
        return new Matrix(data);
    }
    
}
