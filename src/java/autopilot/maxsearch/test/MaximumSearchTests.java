package autopilot.maxsearch.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import autopilot.maxsearch.BrentMethodSearch;
import autopilot.maxsearch.Function;
import autopilot.maxsearch.FunctionAbstract;
import autopilot.maxsearch.FunctionCache;
import autopilot.maxsearch.GoldenRatioMaximumSearch;
import autopilot.maxsearch.MaximumSearchInterface;
import autopilot.maxsearch.TheilSenRobustMaximumSearch;
import autopilot.maxsearch.UniformMaximumSearch;

public class MaximumSearchTests
{

	protected static final double cNoiseAmplitude = 0.01;

	private static final Random cRandom = new Random();
	private static Function cExampleFunction = new FunctionAbstract()
	{

		@Override
		public double getXMin()
		{
			return -10;
		}

		@Override
		public double getXMax()
		{
			return 10;
		}

		@Override
		public double f(final double x)
		{
			final double lNoise = cNoiseAmplitude * (cRandom.nextDouble() - 0.5);
			return 1 / (1 + x * x) + lNoise;
		}

	};

	@Test
	public void testSimpleSearch() throws IOException
	{
		final FunctionCache lFunctionCache = FunctionCache.wrap(cExampleFunction,
																														0.005);

		final MaximumSearchInterface lSimpleMaximumSearch = new UniformMaximumSearch(10);

		final double lArgMax = lSimpleMaximumSearch.findMaximum(lFunctionCache,
																														-2,
																														1,
																														0.01);
		final int lNumberOfEvaluationsNeeded = lFunctionCache.getCacheSize();
		System.out.println("testSimpleSearch");
		System.out.format("lNumberOfEvaluationsNeeded=%d \n",
											lNumberOfEvaluationsNeeded);
		System.out.format("argmax= %g \n", lArgMax);
		assertEquals(0, lArgMax, 0.1);

	}

	@Test
	public void testGoldenRatioSearch() throws IOException
	{
		final FunctionCache lFunctionCache = FunctionCache.wrap(cExampleFunction,
																														0.005);

		final MaximumSearchInterface lGoldenRatioSearch = new GoldenRatioMaximumSearch();

		final double lArgMax = lGoldenRatioSearch.findMaximum(lFunctionCache,
																													-2,
																													1,
																													0.01);
		final int lNumberOfEvaluationsNeeded = lFunctionCache.getCacheSize();
		System.out.println("testGoldenRatioSearch");
		System.out.format("lNumberOfEvaluationsNeeded=%d \n",
											lNumberOfEvaluationsNeeded);
		System.out.format("argmax= %g \n", lArgMax);
		assertEquals(0, lArgMax, 0.1);

	}

	@Test
	public void testBrentMethodSearch() throws IOException
	{
		final FunctionCache lFunctionCache = FunctionCache.wrap(cExampleFunction,
																														0.005);

		final MaximumSearchInterface lBrentMethodSearch = new BrentMethodSearch();

		final double lArgMax = lBrentMethodSearch.findMaximum(lFunctionCache,
																													-2,
																													1,
																													0.01);
		final int lNumberOfEvaluationsNeeded = lFunctionCache.getCacheSize();
		System.out.println("testBrentMethodSearch");
		System.out.format("lNumberOfEvaluationsNeeded=%d \n",
											lNumberOfEvaluationsNeeded);

		System.out.format("argmax= %g \n", lArgMax);
		assertEquals(0, lArgMax, 0.1);

	}

	@Test
	public void testRobustThielSenSearch() throws IOException
	{
		final FunctionCache lFunctionCache = FunctionCache.wrap(cExampleFunction,
																														0.005);

		final TheilSenRobustMaximumSearch lRobustMaximumSearch = new TheilSenRobustMaximumSearch();

		final double lArgMax = lRobustMaximumSearch.findMaximum(lFunctionCache,
																														-2,
																														1,
																														0.01);
		final int lNumberOfEvaluationsNeeded = lFunctionCache.getCacheSize();
		System.out.println("testRobustThielSenSearch");
		System.out.format("lNumberOfEvaluationsNeeded=%d \n",
											lNumberOfEvaluationsNeeded);

		System.out.format("argmax= %g \n", lArgMax);
		assertEquals(0, lArgMax, 0.1);

	}

}
