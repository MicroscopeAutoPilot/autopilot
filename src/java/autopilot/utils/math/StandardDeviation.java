package autopilot.utils.math;

public class StandardDeviation implements Statistic<Double>
{

	int mCount = 0;
	double mTotal = 0;
	double mTotalSquares = 0;

	private boolean mOutdated = true;
	private double mCachedValue;

	@Override
	public void reset()
	{
		mOutdated = true;
		mCount = 0;
		mTotal = 0;
		mTotalSquares = 0;
	}

	@Override
	public int enter(final double pValue)
	{
		mOutdated = true;
		mCount++;
		mTotal += pValue;
		mTotalSquares += pValue * pValue;
		return mCount;
	}

	@Override
	public Double getStatistic()
	{
		if (!mOutdated)
		{
			return mCachedValue;
		}
		else
		{
			final double stddev = Math.sqrt(mCount * mTotalSquares
																			- mTotal
																			* mTotal) / mCount;
			mCachedValue = stddev;
			mOutdated = false;
			return mCachedValue;
		}
	}

	@Override
	public int getCount()
	{
		return mCount;
	}

}
