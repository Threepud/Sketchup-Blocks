/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author cravingoxygen
 */
public class NewtonRaphson 
{
    static Location[] landmarks;
    static int numLocations = 5;
    static int maxIters = 5;
    static double FOVX = 51.2;
    static double FOVY = 39;
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
        return Math.toRadians((-0.5 + x)*FOVX);
    }
    
    private static double calculateOrientationXZ(double x)
    {
        return Math.toRadians((0.5 + x)*FOVY);
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
        
        viewXY[3][0] = 0.4982449412345886;
        viewXY[3][1] =  0.702478289604187;
        viewXY[4][0] = 0.4876736104488373;
        viewXY[4][1] =  0.4575694501399994;
        calculate(viewXY);
    }
    
    public static void calculate(double[][] viewXY) 
    {
        landmarks = new Location[numLocations];
        data[0][0] = -23.8; //x
        data[0][1] = 0;     //y
        data[0][2] = 34.5;  //z
        data[0][3] = 0;     //oxy
        data[0][4] = Math.toRadians(57.5); //oxz 57.5
        
        data[1][0] = 0;
        data[1][1] = -8.95;
        data[1][2] =  /*-34.5*/ + 3.25;
        data[1][3] = calculateOrientationXY(viewXY[0][0]);//0.89001054
        data[1][4] = calculateOrientationXZ(viewXY[0][1]);
        data[2][0] = 0;
        data[2][1] =  0.1;
        data[2][2] =  /*-34.5*/  + 3.25;
        data[2][3] = calculateOrientationXY(viewXY[1][0]);//0.47317624
        data[2][4] = calculateOrientationXZ(viewXY[1][1]);
        data[3][0] = 0;
        data[3][1] = 8;
        data[3][2] =  /*-34.5*/  + 3.25;
        data[3][3] = calculateOrientationXY(viewXY[2][0]);//0.12095226
        data[3][4] = calculateOrientationXZ(viewXY[2][1]);
        
        data[4][0] = 0;
        data[4][1] = 0.1;
        data[4][2] = /*-34.5*/  + 9.75;
        data[4][3] = calculateOrientationXY(viewXY[3][0]);//0.12095226
        data[4][4] = calculateOrientationXZ(viewXY[3][1]);
        
        data[5][0] = 0;
        data[5][1] = 0.1;
        data[5][2] =  /*-34.5*/  + 16.25;
        data[5][3] = calculateOrientationXY(viewXY[4][0]);//0.12095226
        data[5][4] = calculateOrientationXZ(viewXY[4][1]);
        
        errors[0][0] = 1;
        errors[0][1] = 1;
        errors[0][2] = 1;
        errors[0][3] = 0.001;
        errors[0][4] = 0.001;
        
        for (int k = 1; k <= numLocations; k++)
        {
            for (int i = 0; i < Location.numVars; i++)
            {
                errors[k][i] = 0;
            }
            System.out.println((k)+":\t"+Math.toDegrees(data[k][3])+"\t"+Math.toDegrees(data[k][4]));
        }
        
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
            }
            Location rEst = new Location(data[0]);
            /* Scanner in = new Scanner(System.in);
             for (int k = 0; k < numLocations; k++)
             {
                 System.out.println("Please enter (Lx"+k+", Ly"+k+", Reactiv_X"+k+")");
                 landmarks[k] = new Location(in.nextDouble(), in.nextDouble(), Math.toRadians((in.nextDouble() - 0.5)*FOV));

             }

             System.out.println("Please enter (Rx, Ry, R0)");
             Location rEst = new Location(in.nextDouble(), in.nextDouble(), in.nextDouble());*/

             double[] dX;
             System.out.println(rEst.x+"\t"+rEst.y+"\t"+rEst.z+"\t"+rEst.oXY+"\t"+rEst.oXZ+"\t");
             for (int iter = 1; iter <= maxIters; iter++)
             {
             //Set up A
                 double[][] A = new double[numLocations][Location.numVars];
                 double[] b = new double[Location.numVars];
                 for (int k = 0; k < numLocations; k++)
                 {
                     A[k][0] = dfdrx(k, rEst);
                     A[k][1] = dfdry(k, rEst);
                     A[k][2] = dfdrz(k, rEst);
                     A[k][3] = dfdroxy(k, rEst);
                     A[k][4] = dfdroxz(k, rEst);
                     b[k] = -1*f(k, rEst);
                 }

                // printMatrix(A);
                 LUDecomposer.decompose(A);

                 dX = LUDecomposer.solve(b);

                 rEst.x += dX[0];
                 rEst.y += dX[1];
                 rEst.z += dX[2];
                 rEst.oXY += dX[3];
                 rEst.oXZ += dX[4];
                 System.out.println(rEst.x+"\t"+rEst.y+"\t"+rEst.z+"\t"+rEst.oXY+"\t"+rEst.oXZ+"\t");
             }
             System.out.println("&****************************************************************************************************&");
             System.out.println(rEst.x+"\t"+rEst.y+"\t"+rEst.z+"\t"+Math.toDegrees(rEst.oXY)+"\t"+Math.toDegrees(rEst.oXZ)+"\t");
             System.out.println("&****************************************************************************************************&");
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
        double f1 = Math.tan(landmarks[i].oXY+r.oXY) + (landmarks[i].y - r.y)/(landmarks[i].x - r.x);
        double f2 = Math.tan(landmarks[i].oXZ+r.oXZ) + (landmarks[i].z - r.z)/(landmarks[i].x - r.x);
        return f1+f2;
    }
    
    private static double dfdrx(int i, Location r)
    {
       /* System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("Landmark: "+i);
        System.out.println("Rx: "+r.x+"\tRy: "+r.y+"\tRz: "+r.z);
        System.out.println("Lx: "+landmarks[i].x+"\tLy: "+landmarks[i].y+"\tLz: "+landmarks[i].z);
        System.out.println((landmarks[i].y - r.y)*Math.pow(landmarks[i].x - r.x, -2)+(landmarks[i].z - r.z)*Math.pow(landmarks[i].x - r.x, -2));
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");*/
        return (landmarks[i].y - r.y)*Math.pow(landmarks[i].x - r.x, -2)+(landmarks[i].z - r.z)*Math.pow(landmarks[i].x - r.x, -2);
    }
    
    private static double dfdry(int i, Location r)
    {
        return Math.pow(r.x - landmarks[i].x, -1);
    }
    
    private static double dfdrz(int i, Location r)
    {
        return Math.pow(r.x - landmarks[i].x, -1);
    }
    
    private static double dfdroxy(int i, Location r)
    {
        return Math.pow(1.0/(Math.cos(landmarks[i].oXY+r.oXY)), 2);
    }
    
    private static double dfdroxz(int i, Location r)
    {
        return Math.pow(1.0/(Math.cos(landmarks[i].oXZ+r.oXZ)), 2);
    }
}

