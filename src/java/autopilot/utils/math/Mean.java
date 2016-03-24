package autopilot.utils.math;

public class Mean implements Statistic<Double>
{

	private int mCount = 0;
	private double mTotal = 0;

	private boolean mOutdated = true;
	private double mCachedValue;

	@Override
	public void reset()
	{
		mOutdated = true;
		mCount = 0;
		mTotal = 0;
	}

	@Override
	public int enter(final double pValue)
	{
		mOutdated = true;
		mCount++;
		mTotal += pValue;
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
			final double lValue;

			if (mCount == 0)
			{
				lValue = Double.NaN;
			}
			else
			{
				lValue = mTotal / mCount;
			}

			mOutdated = false;
			mCachedValue = lValue;
			return mCachedValue;
		}
	}

	@Override
	public int getCount()
	{
		return mCount;
	}

}
