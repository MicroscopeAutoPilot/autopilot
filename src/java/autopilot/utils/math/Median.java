package autopilot.utils.math;

import gnu.trove.list.array.TDoubleArrayList;

public class Median implements Statistic<Double>
{

	private final org.apache.commons.math3.stat.descriptive.rank.Median mMedian = new org.apache.commons.math3.stat.descriptive.rank.Median();;
	private final TDoubleArrayList mFastDoubleList = new TDoubleArrayList();
	private int mCount = 0;

	private boolean mOutdated = true;
	private double mCachedValue;

	@Override
	public final void reset()
	{
		mFastDoubleList.clear();
		mOutdated = true;
		mCount = 0;
	}

	@Override
	public final int enter(final double pValue)
	{
		mOutdated = true;
		mFastDoubleList.add(pValue);
		mCount++;
		return mCount;
	}

	@Override
	public final Double getStatistic()
	{
		if (!mOutdated)
		{
			return mCachedValue;
		}
		else
		{
			final double lValue;

			final double[] lUnderlyingArray = mFastDoubleList.toArray();

			mMedian.setData(lUnderlyingArray);
			lValue = mMedian.evaluate();

			mOutdated = false;
			mCachedValue = lValue;
			return mCachedValue;
		}
	}

	@Override
	public final int getCount()
	{
		return mCount;
	}

	public final TDoubleArrayList getValuesList()
	{
		return mFastDoubleList;
	}

}
