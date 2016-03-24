package autopilot.stackanalysis;

import static java.lang.Math.abs;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.concurrent.Callable;

import rtlib.core.math.argmax.SmartArgMaxFinder;

public class TileFocusTask implements Callable<Double>
{

	private final SmartArgMaxFinder mSmartArgMaxFinder = new SmartArgMaxFinder();
	private final TDoubleArrayList mZ = new TDoubleArrayList();
	private final TDoubleArrayList mV = new TDoubleArrayList();
	private double mX, mY;
	private final double mProbabilityThreshold;
	private double mMaxFocusValue;
	private boolean mUnprobable;
	private boolean mSaturated;
	private Double mFitProbability;

	public TileFocusTask(	double pX,
												double pY,
												double pProbabilityThreshold)
	{
		mProbabilityThreshold = pProbabilityThreshold;
		setX(pX);
		setY(pY);
	}

	public double getX()
	{
		return mX;
	}

	public void setX(double pX)
	{
		mX = pX;
	}

	public double getY()
	{
		return mY;
	}

	public void setY(double pY)
	{
		mY = pY;
	}

	public void add(double pZ, double pV)
	{
		mZ.add(pZ);
		mV.add(pV);
	}

	@Override
	public Double call() throws Exception
	{
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		mMaxFocusValue = mV.max();

		mSmartArgMaxFinder.setDenoisingActive(false);

		final Double lArgMax = mSmartArgMaxFinder.argmax(	mZ.toArray(),
																											mV.toArray());

		mFitProbability = mSmartArgMaxFinder.getLastFitProbability();

		if (lArgMax == null || mFitProbability == null)
		{
			mUnprobable = true;
			return null;
		}

		mUnprobable = mFitProbability < mProbabilityThreshold;

		final double lZmin = mZ.min();
		final double lZmax = mZ.max();
		final double lAmplitude = lZmax - lZmin;
		final double lStep = lAmplitude / (mZ.size() - 1);

		mSaturated = abs(lArgMax - lZmin) < 0.01 * lStep || abs(lArgMax - lZmax) < 0.01 * lStep;

		/*format(	"TileFocusTask.call(%g,%g,%g)\n",
						mX,
						mY,
						lArgMax == null ? "null" : lArgMax);/**/
		return lArgMax;
	}

	private static void println(String pString)
	{
		// System.out.println(pString);
	}

	private static void format(String format, Object... args)
	{
		// System.out.format(format, args);
	}

	public double getMaxFocusValue()
	{
		return mMaxFocusValue;
	}

	public double getFitProbability()
	{
		return mFitProbability;
	}

	public boolean isUnprobable()
	{
		return mUnprobable;
	}

	public boolean isSaturated()
	{
		return mSaturated;
	}

}
