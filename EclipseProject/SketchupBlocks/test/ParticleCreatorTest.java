import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runners.*;
import org.junit.runner.RunWith;

import sketchupblocks.calibrator.Particle;
import sketchupblocks.calibrator.ParticleCreator;

@RunWith(JUnit4.class)
public class ParticleCreatorTest
{
	@Test
	public void testLimitgetParticle()
	{
	ParticleCreator pc = new ParticleCreator(3,0,1);
	
		for(int k = 0  ; k < 100 ; k++)
		{
		Particle p = pc.getParticle(1,2,3,4);
			for(int l = 0 ; l < 3 ; l++)
				{
				assertTrue( p.attributes[l] >= 0);
				assertTrue( p.attributes[l] <= 1);
				}
		assertTrue(p.socialComponent == 1);
		assertTrue(p.cognitiveComponent == 2);
		assertTrue(p.momentumComponent == 3);
		assertTrue(p.ComponentMax == 4);
		}
	}


}