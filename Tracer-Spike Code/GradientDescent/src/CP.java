/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cravingoxygen
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
    public Matrix calcF(Matrix inputs) throws Exception 
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
    public Matrix calcJ(Matrix inputs) throws Exception 
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
