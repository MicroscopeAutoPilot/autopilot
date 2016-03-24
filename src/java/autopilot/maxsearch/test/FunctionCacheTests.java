package autopilot.maxsearch.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import autopilot.maxsearch.Function;
import autopilot.maxsearch.FunctionAbstract;
import autopilot.maxsearch.FunctionCache;

public class FunctionCacheTests
{

	protected static final double cNoiseAmplitude = 0.01;

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
			return x;
		}

	};

	@Test
	public void testFunctionCache() throws IOException
	{
		final FunctionCache lFunctionCache = FunctionCache.wrap(cExampleFunction,
																														0.01);

		lFunctionCache.f(-2);
		lFunctionCache.f(0);
		lFunctionCache.f(2);
		final double[] ys = lFunctionCache.f(-2, -1, 0, 1, 2);
		assertEquals("[-2.0, -1.0, 0.0, 1.0, 2.0]", Arrays.toString(ys));
		System.out.println(Arrays.toString(ys));

	}

	@Test
	public void testFunctionCacheEpsilon() throws IOException
	{
		FunctionCache lFunctionCache = FunctionCache.wrap(cExampleFunction,
																											0.01);

		lFunctionCache.f(-2);
		lFunctionCache.f(0);
		lFunctionCache.f(2);

		assertTrue(lFunctionCache.isInCache(-2));
		assertTrue(lFunctionCache.isInCache(0));
		assertTrue(lFunctionCache.isInCache(2));
		assertTrue(lFunctionCache.isInCache(0.001));
		assertFalse(lFunctionCache.isInCache(1));

		lFunctionCache = FunctionCache.wrap(cExampleFunction, 0);

		lFunctionCache.f(-2);
		lFunctionCache.f(0);
		lFunctionCache.f(2);

		assertTrue(lFunctionCache.isInCache(-2));
		assertTrue(lFunctionCache.isInCache(0));
		assertTrue(lFunctionCache.isInCache(2));
		assertFalse(lFunctionCache.isInCache(0.001));
		assertFalse(lFunctionCache.isInCache(1));

	}

}
