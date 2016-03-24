package autopilot.stackanalysis.plane2d.xi2reg.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import autopilot.stackanalysis.plane2d.xi2reg.Xi2RegPlane2DFitDistribution;

public class Plane2DFitDistributionTests
{

	@Test
	public void testDistribution() throws InterruptedException,
																ExecutionException
	{
		final Xi2RegPlane2DFitDistribution lPlane2DFitDistribution = new Xi2RegPlane2DFitDistribution();

		{

			final double lPValue = lPlane2DFitDistribution.getPValue(	20,
																																0.0001);
			assertEquals(4.158857805025906E-22, lPValue, 1.0E-23);

			System.out.println("lPValue= " + lPValue);
		}

		{
			final double lPValue = lPlane2DFitDistribution.getPValue(	4,
																																0.000000001);
			assertEquals(9.51687452606813E-5, lPValue, 1.0E-6);
			System.out.println("lPValue=" + lPValue);
		}

		{
			final double lPValue = lPlane2DFitDistribution.getPValue(	10,
																																0.01);
			assertEquals(0.005100086743998871, lPValue, 1.0E-3);

			System.out.println("lPValue=" + lPValue);
		}

		{
			final double lPValue = lPlane2DFitDistribution.getPValue(	211,
																																0.01);
			assertEquals(2.0390130240913588E-51, lPValue, 1.0E-52);

			System.out.println("lPValue=" + lPValue);
		}

	}

}
