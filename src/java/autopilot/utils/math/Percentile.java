package autopilot.utils.math;

import gnu.trove.list.array.TDoubleArrayList;

public class Percentile implements Statistic<Double>
{

	private final org.apache.commons.math3.stat.descriptive.rank.Percentile mPercentile = new org.apache.commons.math3.stat.descriptive.rank.Percentile();;
	private final TDoubleArrayList mFastDoubleList = new TDoubleArrayList();
	private int mCount = 0;

	private boolean mOutdated = true;
	private double mCachedValue;
	private double mPercentilePoint = 0.5;

	public Percentile(final double pPercentilePoint)
	{
		super();
		mPercentilePoint = pPercentilePoint;
	}

	public double getPercentilePoint()
	{
		return mPercentilePoint;
	}

	public void setPercentilePoint(final double pPercentilePoint)
	{
		mOutdated |= mPercentilePoint != pPercentilePoint;
		mPercentilePoint = pPercentilePoint;
	}

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

			mPercentile.setData(lUnderlyingArray);
			lValue = mPercentile.evaluate(100 * mPercentilePoint);

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
