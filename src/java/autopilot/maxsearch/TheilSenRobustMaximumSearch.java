package autopilot.maxsearch;

import autopilot.utils.math.ThreePointParabolaArgmax;

/**
 * Thiel-Sen based robust maximum search.
 * 
 * @author royer
 */
public class TheilSenRobustMaximumSearch extends UniformMaximumSearch	implements
																																			MaximumSearchInterface
{

	private final ThreePointParabolaArgmax mThreePointPerfectEstimator = new ThreePointParabolaArgmax();

	/**
	 * Constructs a Thiel-Sen based robust maximum search object with unlimited
	 * number of evaluations.
	 */
	public TheilSenRobustMaximumSearch()
	{
		super(Integer.MAX_VALUE);
	}

	/**
	 * Constructs a Thiel-Sen based robust maximum search object with a given
	 * maximum number of evaluations
	 * 
	 * @param pMaximumNumberOfEvaluations
	 *          max number of evaluations
	 */
	public TheilSenRobustMaximumSearch(final int pMaximumNumberOfEvaluations)
	{
		super(pMaximumNumberOfEvaluations);
	}

	/**
	 * @see autopilot.maxsearch.UniformMaximumSearch#findMaximum(autopilot.maxsearch.Function,
	 *      double, double, double)
	 */
	@Override
	public double findMaximum(final Function pFunction,
														double pMin,
														double pMax,
														final double pPrecision)
	{
		pMin = Math.max(pMin, pFunction.getXMin());
		pMax = Math.min(pMax, pFunction.getXMax());

		final double lWidth = pMax - pMin;

		final double lMinimalPrecision = lWidth / (mMaximumNumberOfEvaluations - 1);

		final int lNumberOfPoints = 1 + (int) Math.round(lWidth / Math.max(	pPrecision,
																																				lMinimalPrecision));

		return simpleSearch(pFunction, pMin, pMax, lNumberOfPoints);
	}

	/**
	 * @see autopilot.maxsearch.UniformMaximumSearch#simpleSearch(autopilot.maxsearch.Function,
	 *      double, double, int)
	 */
	@Override
	protected final double simpleSearch(final Function pFunction,
																			final double pMin,
																			final double pMax,
																			final int pNumberOfPoints)
	{
		final double lArgMax = super.simpleSearch(pFunction,
																							pMin,
																							pMax,
																							pNumberOfPoints);

		final int lIndexOfMax = indexOf(mX, lArgMax);

		if (lIndexOfMax == 0 || lIndexOfMax == mX.length - 1)
		{
			return lArgMax;
		}

		{
			final int li = lIndexOfMax - 1;
			final int ci = lIndexOfMax;
			final int ui = lIndexOfMax + 1;

			final double xl = mX[li];
			final double xc = mX[ci];
			final double xu = mX[ui];

			final double yl = mY[li];
			final double yc = mY[ci];
			final double yu = mY[ui];

			mThreePointPerfectEstimator.findExtremum(xl, xc, xu, yl, yc, yu);

			if (!mThreePointPerfectEstimator.reliable && !mThreePointPerfectEstimator.signPositive)
			{
				return lArgMax;
			}
		}

		if (lIndexOfMax >= 2 && lIndexOfMax <= mX.length - 3)
		{
			final double lArgMaxTPE = mThreePointPerfectEstimator.xe;

			final int li = lIndexOfMax - 2;
			final int ci = lIndexOfMax;
			final int ui = lIndexOfMax + 2;

			final double xl = mX[li];
			final double xc = mX[ci];
			final double xu = mX[ui];

			final double yl = mY[li];
			final double yc = mY[ci];
			final double yu = mY[ui];

			mThreePointPerfectEstimator.findExtremum(xl, xc, xu, yl, yc, yu);

			if (!mThreePointPerfectEstimator.reliable && !mThreePointPerfectEstimator.signPositive)
			{
				return lArgMaxTPE;
			}

			return 0.50 * lArgMaxTPE
							+ 0.50
							* mThreePointPerfectEstimator.xe;
		}

		return mThreePointPerfectEstimator.xe;

	}

	private int indexOf(final double[] pArray,
											final double pValueToSearchFor)
	{
		for (int i = 0; i < pArray.length; i++)
		{
			final double lValue = pArray[i];
			if (lValue == pValueToSearchFor)
			{
				return i;
			}
		}
		return -1;
	}
}
