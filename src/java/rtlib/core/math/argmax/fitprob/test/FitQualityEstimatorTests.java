package rtlib.core.math.argmax.fitprob.test;

import gnu.trove.list.array.TDoubleArrayList;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;

import org.junit.Test;

import rtlib.core.math.argmax.fitprob.FitQualityEstimator;
import rtlib.core.math.argmax.fitprob.RandomizedDataGaussianFitter;
import rtlib.core.math.argmax.test.ArgMaxTester;
import rtlib.core.units.Magnitude;

public class FitQualityEstimatorTests
{

	@Test
	public void basicTest()
	{
		final FitQualityEstimator lFitQualityEstimator = new FitQualityEstimator();

		{
			final double[] lX = new double[]
			{ 0, 1, 2, 3, 4, 5, 6 };
			final double[] lY = new double[]
			{ 0, 2, 2, 7, 6, 1, 0 };

			final Double lPvalue = lFitQualityEstimator.probability(lX,
																	lY);
			System.out.format("p=%g \n", lPvalue);

		}

		{
			final double[] lX = new double[]
			{ -2.0, -1.0, 0.0, 1.0, 2.0 };
			final double[] lY = new double[]
			{ 3.71E-05, 3.80E-05, 3.86E-05, 3.86E-05, 3.79E-05 };

			final Double lPvalue = lFitQualityEstimator.probability(lX,
																	lY);

			System.out.println(lPvalue);
		}

	}

	@Test
	public void performancesTest()
	{
		final FitQualityEstimator lFitQualityEstimator = new FitQualityEstimator();

		for (int i = 0; i < 100; i++)
		{
			final double[] lX = new double[]
			{ 0, 1, 2, 3, 4, 5, 6 };
			final double[] lY = new double[]
			{ 0, 2, 2, 7, 6, 1, 0 };

			final long lStart = System.nanoTime();
			final Double lPvalue = lFitQualityEstimator.probability(lX,
																	lY);
			final long lStop = System.nanoTime();
			final double lElapsed = Magnitude.nano2milli((1.0 * lStop - lStart) / 1);

			System.out.format(	"%g ms elpased to find: p=%g \n",
								lElapsed,
								lPvalue);

		}

		{
			final double[] lX = new double[]
			{ -2.0, -1.0, 0.0, 1.0, 2.0 };
			final double[] lY = new double[]
			{ 0.2, 0.4, 0.1, 0.2, 0.1 };

			double lPvalue = 0;
			final int lNumberOfIterations = 100;
			final long lStart = System.nanoTime();
			for (int i = 0; i < lNumberOfIterations; i++)
				lPvalue = lFitQualityEstimator.probability(lX, lY);
			final long lStop = System.nanoTime();
			final double lElapsed = Magnitude.nano2milli((1.0 * lStop - 1.0 * lStart) / lNumberOfIterations);

			System.out.format("%g ms per estimation. \n", lElapsed);

			System.out.println(lPvalue);
		}

	}

	@Test
	public void randomDataTest()
	{
		final FitQualityEstimator lFitQualityEstimator = new FitQualityEstimator();
		final double[] lX = new double[]
		{ 0, 1, 2, 3, 4, 5, 6 };
		final double[] lY = new double[lX.length];
		final Random lRandom = new Random(System.nanoTime());

		for (int i = 0; i < 1024; i++)
		{

			RandomizedDataGaussianFitter.generateRandomVector(	lRandom,
																lY);

			final Double lPvalue = lFitQualityEstimator.probability(lX,
																	lY);

			/*System.out.format(" p=%g \n",
												lPvalue);/**/

		}

	}

	@Test
	public void benchmark() throws IOException, URISyntaxException
	{
		final FitQualityEstimator lFitQualityEstimator = new FitQualityEstimator();

		System.out.println("nofit:");
		run(lFitQualityEstimator,
			FitQualityEstimatorTests.class,
			"./benchmark/nofit.txt",
			9);

		System.out.println("fit:");
		run(lFitQualityEstimator,
			FitQualityEstimatorTests.class,
			"./benchmark/fit.txt",
			15);

	}

	private void run(	FitQualityEstimator lGaussianFitEstimator,
						Class<?> lContextClass,
						String lRessource,
						int lNumberOfDatasets)	throws IOException,
												URISyntaxException
	{
		for (int i = 0; i < lNumberOfDatasets; i++)
		{
			final TDoubleArrayList lY = ArgMaxTester.loadData(	lContextClass,
																lRessource,
																i);
			final TDoubleArrayList lX = new TDoubleArrayList();
			for (int j = 0; j < lY.size(); j++)
				lX.add(j);

			final Double lProbability = lGaussianFitEstimator.probability(	lX.toArray(),
																			lY.toArray());
			final Double lRMSD = lGaussianFitEstimator.getRMSD();

			final double[] lFittedY = lGaussianFitEstimator.getFit(	lX.toArray(),
																	lY.toArray());

			// System.out.println("__________________________________________________________________________");
			// System.out.println("lX=" + Arrays.toString(lX.toArray()));
			// System.out.println("lY=" + Arrays.toString(lY.toArray()));
			// System.out.println("lFittedY=" + Arrays.toString(lFittedY));
			System.out.format("p=%g, rmsd=%g \n", lProbability, lRMSD);
			// System.out.println("rmsd=" + lGaussianFitEstimator.getRMSD());

			/*
			Double lNRMSD = lGaussianFitEstimator.nrmsd(lX.toArray(),
																										lY.toArray());/**/

			// System.out.println("lNRMSD=" + lNRMSD);
		}
	}
}
