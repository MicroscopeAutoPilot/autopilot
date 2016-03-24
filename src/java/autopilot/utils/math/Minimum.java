package autopilot.utils.math;

import java.io.Serializable;

public class Minimum implements Statistic<Double>, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int mCount = 0;
	double mMin = Double.POSITIVE_INFINITY;

	@Override
	public void reset()
	{
		mCount = 0;
		mMin = Double.POSITIVE_INFINITY;
	}

	@Override
	public int enter(final double pValue)
	{
		mCount++;
		mMin = Math.min(mMin, pValue);
		return mCount;
	}

	@Override
	public Double getStatistic()
	{
		return mMin;
	}

	@Override
	public int getCount()
	{
		return mCount;
	}

}
