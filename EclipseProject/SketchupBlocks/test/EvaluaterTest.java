import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runners.*;
import org.junit.runner.RunWith;

@RunWith(JUnit4.class)
public class EvaluaterTest
{
	@Test
	public void triangle()
	{
	double [] length = new double[6];
	length[0] = 1;
	length[1] = 1;
	length[2] = 1;
	length[3] = 1;
	
	length[4] = Math.sqrt(2);
	length[5] = Math.sqrt(2);
	
	double [] angles = new double[6];
	angles[0] = 1;
	angles[1] = 2;
	angles[2] = 3;
	angles[3] = 4;
	
	angles[4] = 5;
	angles[5] = 6;	
	
	TriangleEval eval = new TriangleEval(length,angles);
	
	double [] input = new double[4];
	input[0] = 3;
	input[1] = 4;
	input[2] = 5;
	input[3] = 6;	
	
	EvalResults result = eval.evaluate(input);
	
	double temp = 0;
	double sum = 0;
	// H/V
	temp = length[0]*length[0] - (input[0] *input[0] + input[1] * input[1] - 2*input[0]*input[1]*Math.cos(Math.toRadians(angles[0])));
	sum += temp * temp;
	
	temp = length[1]*length[1] - (input[1] *input[1] + input[2] * input[2] - 2*input[1]*input[2]*Math.cos(Math.toRadians(angles[1])));
	sum += temp * temp;
	
	temp = length[2]*length[2] - (input[2] *input[2] + input[3] * input[3] - 2*input[2]*input[3]*Math.cos(Math.toRadians(angles[2])));
	sum += temp * temp;
	
	temp = length[3]*length[3] - (input[3] *input[3] + input[0] * input[0] - 2*input[3]*input[0]*Math.cos(Math.toRadians(angles[3])));
	sum += temp * temp;
	// Diagonal
	temp = length[4]*length[4] - (input[0] *input[0] + input[2] * input[2] - 2*input[0]*input[2]*Math.cos(Math.toRadians(angles[4])));
	sum += temp * temp;
	
	temp = length[5]*length[5] - (input[1] *input[1] + input[3] * input[3] - 2*input[1]*input[3]*Math.cos(Math.toRadians(angles[5])));
	sum += temp * temp;
	
	assertTrue(Math.abs(result.score - 1000.0/sum) < 0.0001);
	
	}

	@Test
	public void sphere()
	{
	SphereEval eval = new SphereEval(2,2,2,2
								,new Vec3(0,0,0)
								,new Vec3(0,0,1)
								,new Vec3(0,1,0)
								,new Vec3(0,1,1)
								);
								
	EvalResults result = eval.evaluate(new double[]{1,1,1});
	assertTrue(Math.abs(result.score - (50.0/9.0)) < 0.0001);
	}
}





