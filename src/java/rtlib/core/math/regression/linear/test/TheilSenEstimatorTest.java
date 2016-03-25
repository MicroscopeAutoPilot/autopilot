package rtlib.core.math.regression.linear.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.math.regression.linear.TheilSenEstimator;

public class TheilSenEstimatorTest
{

	@Test
	public void TheilSenEstimatorTest() throws IOException
	{
		final TheilSenEstimator lTheilSenEstimator = new TheilSenEstimator();

		final Random rnd = new Random();

		final double noise = 0.001;

		final double a = 2;
		final double b = 1;
		for (int i = 0; i < 100; i++)
		{
			for (int j = 0; j < 100; j++)
			{
				final double x = 0 + i + noise * rnd.nextGaussian();
				final double y = b	+ a
									* i
									+ noise
									* rnd.nextGaussian();
				lTheilSenEstimator.enter(x, y);
			}
		}

		final UnivariateAffineFunction lModel = lTheilSenEstimator.getModel();

		final double stderror = lTheilSenEstimator.computeError(lModel);

		System.out.println(lModel);
		System.out.format("Error: %f \n", stderror);
		
		assertTrue(stderror<0.1);

	}

}
