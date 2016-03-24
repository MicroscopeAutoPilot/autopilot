package autopilot.stackanalysis.plane2d.xi2reg.test;

import static java.lang.Math.random;
import static java.lang.Math.sqrt;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import autopilot.stackanalysis.plane2d.FitResult;
import autopilot.stackanalysis.plane2d.Plane2DFitInterface;
import autopilot.stackanalysis.plane2d.xi2reg.Xi2RegPlane2DFit;

public class Plane2DFitTests
{

	@Test
	public void basictest()
	{
		final Plane2DFitInterface lPlane2DFit = new Xi2RegPlane2DFit();
		lPlane2DFit.addPoint(0, 0, 0);
		lPlane2DFit.addPoint(0, 1, 1);
		lPlane2DFit.addPoint(1, 0, 1);
		lPlane2DFit.addPoint(1, 1, 2);
		final double[] lFit = lPlane2DFit.fit().regparams;
		assertEquals(0, lFit[0], 0.001);
		assertEquals(1, lFit[1], 0.001);
		assertEquals(1, lFit[2], 0.001);

	}

	@Test
	public void testbadfit1()
	{
		final Plane2DFitInterface lPlane2DFit = new Xi2RegPlane2DFit();
		lPlane2DFit.addPoint(0, 0, 0);
		lPlane2DFit.addPoint(0, 0.5, 0.5);
		lPlane2DFit.addPoint(0, 1, 1);
		lPlane2DFit.addPoint(0.5, 0, 0.5);
		lPlane2DFit.addPoint(1, 0, 1);
		lPlane2DFit.addPoint(1, 1, 0);

		final FitResult lFitResult = lPlane2DFit.fit();
		System.out.println(lFitResult);

		final double[] lFit = lFitResult.regparams;
		assertEquals(0, lFit[0], 0.001);
		assertEquals(1, lFit[1], 0.001);
		assertEquals(1, lFit[2], 0.001);

	}

	@Test
	public void testbadfit2()
	{
		final Plane2DFitInterface lPlane2DFit = new Xi2RegPlane2DFit();
		lPlane2DFit.addPoint(0, 0, 0);
		lPlane2DFit.addPoint(0, 1, 1);
		lPlane2DFit.addPoint(1, 0, 1);
		lPlane2DFit.addPoint(1, 2, 0);
		lPlane2DFit.addPoint(+10, -10, 0);
		final double[] lFit = lPlane2DFit.fit().regparams;
		assertEquals(0, lFit[0], 0.001);
		assertEquals(1, lFit[1], 0.001);
		assertEquals(1, lFit[2], 0.001);

	}

	@Test
	public void testMany()
	{
		final int start = 16;
		final int stop = 100;

		double failcount = 0;
		for (int n = start; n < stop; n++)
		{
			final int m = 1 + n / 2;

			System.out.println("####################################################");
			System.out.format("n=%d, m=%d \n", n, m);

			final Plane2DFitInterface lPlane2DFit = new Xi2RegPlane2DFit();

			for (int i = 0; i < m; i++)
			{
				final double x = random();
				final double y = random();
				final double noise = 0.5 * (random() - 0.5);
				lPlane2DFit.addPoint(x, y, noise);
			}

			final double lStep = 1.0 / (1 + sqrt(n));

			for (double x = 0; x < 1; x += lStep)
				for (double y = 0; y < 1; y += lStep)
				{
					final double noise = 0.05 * (random() - 0.5);
					if (random() > 0.5)
						lPlane2DFit.addPoint(x, y, x + y + noise);
				}

			int lLocalFailCount = 0;
			final FitResult lFitResult = lPlane2DFit.fit();
			for (int i = 0; i < m; i++)
				if (lFitResult.inliers[i])
					lLocalFailCount++;
			/*for (int i = m; i < lFitResult.inliers.length; i++)
				if (!lFitResult.inliers[i])
					failcount++;/**/

			System.out.println("local fail count=" + lLocalFailCount);
			assertEquals(0, lFitResult.regparams[0], 0.1);
			assertEquals(1, lFitResult.regparams[1], 0.1);
			assertEquals(1, lFitResult.regparams[2], 0.1);

			System.out.format("n=%d, m=%d \n", n, m);

			failcount += lLocalFailCount;
		}

		System.out.println("FAILCOUNT=" + failcount);

	}

}
