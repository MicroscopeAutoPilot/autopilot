package autopilot.utils.math;

import java.io.Serializable;

public class Maximum implements Statistic<Double>, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int mCount = 0;
	double mMax = Double.NEGATIVE_INFINITY;

	@Override
	public void reset()
	{
		mCount = 0;
		mMax = Double.NEGATIVE_INFINITY;
	}

	@Override
	public int enter(final double pValue)
	{
		mCount++;
		mMax = Math.max(mMax, pValue);
		return mCount;
	}

	@Override
	public Double getStatistic()
	{
		return mMax;
	}

	@Override
	public int getCount()
	{
		return mCount;
	}

}
