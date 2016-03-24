package autopilot.maxsearch;

import gnu.trove.list.array.TDoubleArrayList;
import autopilot.utils.math.ThreePointParabolaArgmax;

/**
 * Brent search method
 * 
 * @author royer
 */
public class BrentMethodSearch extends MaximumSearchAbstract implements
																														MaximumSearchInterface
{

	private final TDoubleArrayList mXPositionList = new TDoubleArrayList();
	private final ThreePointParabolaArgmax mThreePointParabolaArgmax = new ThreePointParabolaArgmax();
	private double mLastXPosition;
	private double mPrecision;

	/**
	 * Constructs a {@code BrentMethodSearch} object.
	 */
	public BrentMethodSearch()
	{
		super();
	}

	/**
	 * @see autopilot.maxsearch.MaximumSearchAbstract#findMaximum(autopilot.maxsearch.Function,
	 *      double, double, double)
	 */
	@Override
	public double findMaximum(final Function pFunction,
														double pMin,
														double pMax,
														final double pPrecision)
	{
		mPrecision = pPrecision;
		pMin = Math.max(pMin, pFunction.getXMin());
		pMax = Math.min(pMax, pFunction.getXMax());

		mXPositionList.clear();
		mLastXPosition = 0.5 * (pMin + pMax);

		final double lCenter = 0.5 * (pMax + pMin);

		return findMaximumRecursive(pFunction, pMin, lCenter, pMax);
	}

	private double findMaximumRecursive(final Function pFunction,
																			final double pXMin,
																			final double pXCenter,
																			final double pXMax)
	{
		final double lWidth = pXMax - pXMin;
		if (lWidth < mPrecision)
		{
			return argmax(pFunction);
		}

		final double lYMin = evaluate(pFunction, pXMin);
		final double lYCenter = evaluate(pFunction, pXCenter);
		final double lYMax = evaluate(pFunction, pXMax);

		mThreePointParabolaArgmax.findExtremum(	pXMin,
																						pXCenter,
																						pXMax,
																						lYMin,
																						lYCenter,
																						lYMax);
		final double lXExtremum = mThreePointParabolaArgmax.xe;

		double lNewX;
		if (mThreePointParabolaArgmax.reliable && pXMin <= lXExtremum
				&& lXExtremum <= pXMax)
		{
			lNewX = lXExtremum;

			// Parabolic step accepted:
			// are we too close to the center point?
			// System.out.println("Parabolic step accepted, are we too close to the lower, center, or higher point?");
			if (Math.abs(lNewX - pXCenter) < 0.5 * mPrecision)
			{
				// we translate the new point towards the higher position:
				// System.out.println("We translate the new point towards the higher position:");
				if (lYMin > lYMax)
				{
					lNewX -= 0.5 * mPrecision;
				}
				else
				{
					lNewX += 0.5 * mPrecision;
				}
			}
			else if (Math.abs(lNewX - pXMin) < 0.5 * mPrecision)
			{
				// we translate the new point towards the center position:
				// System.out.println("We translate the new point towards the center position:");
				lNewX += 0.5 * mPrecision;
			}
			else if (Math.abs(lNewX - pXMax) < 0.5 * mPrecision)
			{
				// we translate the new point towards the center position:
				// System.out.println("We translate the new point towards the center position:");
				lNewX -= 0.5 * mPrecision;
			}

		}
		else
		{
			// Parabolic step rejected:
			// Where should we put the new point?
			// System.out.println("Parabolic step rejected, Where shoule we put the new point?");
			if (lYMin > lYMax)
			{
				lNewX = 0.5 * (pXCenter + pXMin);
			}
			else
			{
				lNewX = 0.5 * (pXCenter + pXMax);
			}
		}

		evaluate(pFunction, lNewX);

		final double lBestXPosition = argmax(pFunction);

		final int lIndexOfBestPosition = getIndex(lBestXPosition);

		if (lIndexOfBestPosition == 0)
		{
			return findMaximumRecursive(pFunction,
																	getPosition(0),
																	getPosition(1),
																	getPosition(2));
		}
		else if (lIndexOfBestPosition == mXPositionList.size() - 1)
		{
			return findMaximumRecursive(pFunction,
																	getPosition(lIndexOfBestPosition - 2),
																	getPosition(lIndexOfBestPosition - 1),
																	getPosition(lIndexOfBestPosition));
		}
		else
		{
			return findMaximumRecursive(pFunction,
																	getPosition(lIndexOfBestPosition - 1),
																	getPosition(lIndexOfBestPosition),
																	getPosition(lIndexOfBestPosition + 1));

		}

	}

	private double argmax(final Function pFunction)
	{
		double lArgMax = 0;
		double lMax = Double.NEGATIVE_INFINITY;
		for (final double lX : mXPositionList.toArray())
		{
			final double lY = pFunction.f(lX);
			if (lY > lMax)
			{
				lMax = lY;
				lArgMax = lX;
			}
		}
		return lArgMax;
	}

	private double getPosition(final int pIndex)
	{
		return mXPositionList.get(pIndex);
	}

	private Integer getIndex(final double pBestPosition)
	{
		mXPositionList.sort();
		final int lIndex = mXPositionList.binarySearch(pBestPosition);
		return lIndex;
	}

	private void addTwoPoints(final Function pFunction,
														final double a,
														final double b)
	{

		final double da = Math.abs(a - mLastXPosition);
		final double db = Math.abs(b - mLastXPosition);
		if (da < db)
		{
			evaluate(pFunction, a);
			evaluate(pFunction, b);
		}
		else
		{
			evaluate(pFunction, b);
			evaluate(pFunction, a);
		}
	}

	private double evaluate(final Function pFunction, double x)
	{
		if (!mXPositionList.contains(x))
		{
			x += mPrecision * (Math.random() - 0.5);
		}
		mXPositionList.add(x);
		mXPositionList.sort();
		mLastXPosition = x;
		final double lValue = pFunction.f(x);
		// System.out.println("f(" + x + ")=" + lValue);
		return lValue;
	}
}
