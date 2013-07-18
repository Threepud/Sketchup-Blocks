/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author cravingoxygen
 */
public class NewtonRaphson2D 
{
    static Location[] landmarks;
    static int numLocations = 3;
    static int maxIters = 5;
    static double FOVX = 51.2;
   // static double FOVY = 51;
    static boolean testing = false;
    static int MAX = 5;
    
    // [estimates locations][XYO]
    static double[][] data = new double[1+numLocations][Location.numVars];
    
    // [estimates locations][XYO]
    static double[][] errors = new double[1+numLocations][Location.numVars];
    
    private static void modifyInput(double scale)
    {
        for (int k = 0; k < errors.length; k++)
        {
            for (int i = 0; i < errors[k].length; i++)
            {
                data[k][i] += scale*errors[k][i];
            }
        }
    }
    
    private static double calculateOrientationXY(double x)
    {
        return Math.toRadians((0.5 - x)*FOVX);
    }
    
    
    public static void main(String[] s)
    {
        double[][] viewXY = new double[numLocations][2];
        viewXY[0][0] = 0.7715448141098022;//0.8303525;
        viewXY[0][1] = 0.884951651096344;//0.93242186;
        viewXY[1][0] = 0.5055316090583801;//0.55363744;
        viewXY[1][1] = 0.9025942087173462;//0.92603165;
        viewXY[2][0] = 0.2664489448070526;//0.31293103;
        viewXY[2][1] =  0.9059284925460815;//0.91777617;
        
        calculate(viewXY);
    }
    
    public static void calculate(double[][] viewXY) 
    {
        landmarks = new Location[numLocations];
        data[0][0] = -22; //x
        data[0][1] = 0;     //y
        data[0][2] = 0;     //oxy
        
        data[1][0] = 0;
        data[1][1] = 8.95;
        data[1][2] = calculateOrientationXY(viewXY[0][0]);//0.89001054
        System.out.println("Landmark 1: "+Math.toDegrees(data[1][2]));
        data[2][0] = 0;
        data[2][1] =  -0.1;
        data[2][2] = calculateOrientationXY(viewXY[1][0]);//0.47317624
        System.out.println("Landmark 2: "+Math.toDegrees(data[2][2]));
        data[3][0] = 0;
        data[3][1] = -8;
        data[3][2] = calculateOrientationXY(viewXY[2][0]);//0.12095226
        System.out.println("Landmark 3: "+Math.toDegrees(data[3][2]));
        
        /*errors[0][0] = 1;
        errors[0][1] = 1;
        errors[0][2] = 0.001;
        
        for (int k = 1; k <= numLocations; k++)
        {
            for (int i = 0; i < Location.numVars; i++)
            {
                errors[k][i] = 0;
            }
        }*/
        
        int lBound = 0;
        int uBound = 0;
        
        if (testing)
        {
            lBound = -1*MAX;
            uBound = MAX;
        }
        
        for (int t = lBound; t <= uBound; t++)
        {
            if (testing)
                modifyInput(t);
            
            for (int k = 1; k <= numLocations; k++)
            {
                landmarks[k-1] = new Location(data[k]);
                System.out.println("L"+k+"\tx: "+landmarks[k-1].x+"\ty: "+landmarks[k-1].y+"/to: "+landmarks[k-1].o);
            }
            Location rEst = new Location(data[0]);

             double[] dX;
             System.out.println(rEst.x+"\t"+rEst.y+"\t"+rEst.o);
             for (int iter = 1; iter <= maxIters; iter++)
             {
             //Set up A
                 double[][] A = new double[numLocations][Location.numVars];
                 double[] b = new double[Location.numVars];
                 for (int k = 0; k < numLocations; k++)
                 {
                     A[k][0] = dfdrx(k, rEst);
                     A[k][1] = dfdry(k, rEst);
                     A[k][2] = dfdroxy(k, rEst);
                     b[k] = -1*f(k, rEst);
                 }

                 LUDecomposer.decompose(A);

                 dX = LUDecomposer.solve(b);

                 rEst.x += dX[0];
                 rEst.y += dX[1];
                 rEst.o += dX[2];
                 System.out.println(dX[0]+" | "+dX[1]+" | "+dX[2]);
                 System.out.println(rEst.x+"\t"+rEst.y+"\t"+rEst.o);
             }
             System.out.println("&********************************&");
             System.out.println(rEst.x+"\t"+rEst.y+"\t"+Math.toDegrees(rEst.o));
             System.out.println("&********************************&");
        }
        
    }
    
    private static void printMatrix(double[][] a)
    {
        for (int k = 0; k < a.length; k++)
        {
            String line = "| ";
            for (int i = 0; i < a[k].length; i++)
            {
                line += " "+a[k][i]+" ";
            }
            line += " |";
            System.out.println(line);
        }
    }
    
    private static double f(int i, Location r)
    {
        return Math.tan(landmarks[i].o+r.o) + (landmarks[i].y - r.y)/(landmarks[i].x - r.x);
    }
    
    private static double dfdrx(int i, Location r)
    {
        return (landmarks[i].y - r.y)*Math.pow(landmarks[i].x - r.x, -2);
    }
    
    private static double dfdry(int i, Location r)
    {
        return Math.pow(r.x - landmarks[i].x, -1);
    }
    
    private static double dfdroxy(int i, Location r)
    {
        return Math.pow(1.0/(Math.cos(landmarks[i].o+r.o)), 2);
    }

}

