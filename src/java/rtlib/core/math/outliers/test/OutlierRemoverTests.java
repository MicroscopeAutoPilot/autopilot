package rtlib.core.math.outliers.test;

import static org.junit.Assert.assertTrue;
import gnu.trove.list.array.TDoubleArrayList;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.Test;

import rtlib.core.math.outliers.OutlierRemover;

public class OutlierRemoverTests
{

	@Test
	public void test()
	{

		final NormalDistribution lNormalDistribution = new NormalDistribution();

		final TDoubleArrayList lSamples = new TDoubleArrayList();
		for (int i = 0; i < 1000; i++)
		{
			final double lSample = lNormalDistribution.sample();
			lSamples.add(lSample);
		}

		lSamples.add(10);
		lSamples.add(-10);

		System.out.println(lSamples.min());
		System.out.println(lSamples.max());

		assertTrue(lSamples.min() <= -10);
		assertTrue(lSamples.max() >= 10);

		final TDoubleArrayList lOutliersRemoved = OutlierRemover.removeOutliers(lSamples.toArray(),
																				6);

		System.out.println(lOutliersRemoved.min());
		System.out.println(lOutliersRemoved.max());

		assertTrue(lOutliersRemoved.min() > -4);
		assertTrue(lOutliersRemoved.max() < 4);

	}

}
