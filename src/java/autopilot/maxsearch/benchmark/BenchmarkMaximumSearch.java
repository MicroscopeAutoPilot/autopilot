package autopilot.maxsearch.benchmark;

import java.io.IOException;
import java.util.Random;

import autopilot.maxsearch.Function;
import autopilot.maxsearch.FunctionAbstract;
import autopilot.maxsearch.FunctionCache;
import autopilot.maxsearch.MaximumSearchInterface;
import autopilot.utils.math.Maximum;
import autopilot.utils.math.Median;

public class BenchmarkMaximumSearch
{
	private static final Random rnd = new Random();

	private final double mNoiseAmplitude;
	private final Function cExampleFunction = new FunctionAbstract()
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
			final double lNoise = mNoiseAmplitude * (rnd.nextDouble() - 0.5);
			return 1 / (1 + x * x) + lNoise;
		}

	};

	private final Median mMedianNumberOfEvaluationsNeeded = new Median();
	private final Maximum mMaximumNumberOfEvaluationsNeeded = new Maximum();
	private final Median mMedianDistanceToMaximum = new Median();
	private final Maximum mMaximumDistanceToMaximum = new Maximum();

	private final int mRepeats;

	public BenchmarkMaximumSearch(final double pNoiseAmplitude,
																final int pRepeats)
	{
		super();
		mNoiseAmplitude = pNoiseAmplitude;
		mRepeats = pRepeats;
	}

	public String benchmark(final MaximumSearchInterface pMaximumSearch) throws IOException
	{
		mMedianNumberOfEvaluationsNeeded.reset();
		mMaximumNumberOfEvaluationsNeeded.reset();
		mMedianDistanceToMaximum.reset();
		mMaximumDistanceToMaximum.reset();

		for (int i = 0; i < mRepeats; i++)
		{
			benchmarkOnce(pMaximumSearch);
		}

		final double lMedianNumberOfEvaluationsNeeded = mMedianNumberOfEvaluationsNeeded.getStatistic();
		final double lMaximumNumberOfEvaluationsNeeded = mMaximumNumberOfEvaluationsNeeded.getStatistic();
		final double lMedianDistanceToMaximum = mMedianDistanceToMaximum.getStatistic();
		final double lMaximumDistanceToMaximum = mMaximumDistanceToMaximum.getStatistic();

		final String lResultLine = String.format(	"%g\t%g\t%g\t%g\n",
																							lMedianNumberOfEvaluationsNeeded,
																							lMaximumNumberOfEvaluationsNeeded,
																							lMedianDistanceToMaximum,
																							lMaximumDistanceToMaximum);

		return lResultLine;
	}

	private void benchmarkOnce(final MaximumSearchInterface pMaximumSearch)
	{
		final FunctionCache lFunctionCache = FunctionCache.wrap(cExampleFunction,
																														0);
		final Function lFunction = lFunctionCache;

		final double lMin = -1 - rnd.nextDouble();
		final double lMax = 1 + rnd.nextDouble();

		final double lArgMax = pMaximumSearch.findMaximum(lFunction,
																											lMin,
																											lMax,
																											0.01);
		final int lNumberOfEvaluationsNeeded = lFunctionCache.getCacheSize();
		// System.out.format("lNumberOfEvaluationsNeeded=%d \n",
		// lNumberOfEvaluationsNeeded);
		// System.out.format("argmax= %g \n", lArgMax);

		enterResult(lNumberOfEvaluationsNeeded, Math.abs(lArgMax));
	}

	private void enterResult(	final int pNumberOfEvaluationsNeeded,
														final double pDistanceToMaximum)
	{
		mMedianNumberOfEvaluationsNeeded.enter(pNumberOfEvaluationsNeeded);
		mMaximumNumberOfEvaluationsNeeded.enter(pNumberOfEvaluationsNeeded);

		mMedianDistanceToMaximum.enter(pDistanceToMaximum);
		mMaximumDistanceToMaximum.enter(pDistanceToMaximum);
	}

}
