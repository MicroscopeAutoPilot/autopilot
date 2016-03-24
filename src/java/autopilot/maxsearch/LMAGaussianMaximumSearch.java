package autopilot.maxsearch;

import java.util.Arrays;

import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.fitting.GaussianFitter;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;

/**
 * Levenberg–Marquardt Gaussian fit based search algorithm.
 * 
 * @author royer
 */
public class LMAGaussianMaximumSearch extends MaximumSearchAbstract	implements
																																		MaximumSearchInterface
{

	public int mMaximumNumberOfEvaluations;
	private double mInflection = 1;
	protected double[] mY, mX;
	private double mLastBestY;
	private double mLastComputedRMSD;

	/**
	 * Constructs a {@code LMAGaussianMaximumSearch} with a nearly unlimited
	 * number of evaluations allowed.
	 */
	public LMAGaussianMaximumSearch()
	{
		this(Integer.MAX_VALUE);
	}

	/**
	 * Constructs a {@code LMAGaussianMaximumSearch} with a given maximum number
	 * of evaluations.
	 * 
	 * @param pMaximumNumberOfEvaluations
	 *          max number of evaluations
	 */
	public LMAGaussianMaximumSearch(final int pMaximumNumberOfEvaluations)
	{
		mMaximumNumberOfEvaluations = pMaximumNumberOfEvaluations;
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
		pMin = Math.max(pMin, pFunction.getXMin());
		pMax = Math.min(pMax, pFunction.getXMax());

		final double lWidth = pMax - pMin;

		final double lMinimalPrecision = lWidth / (mMaximumNumberOfEvaluations - 1);

		final int lNumberOfPoints = 1 + (int) Math.round(lWidth / Math.max(	pPrecision,
																																				lMinimalPrecision));

		return simpleSearch(pFunction, pMin, pMax, lNumberOfPoints);
	}

	protected double simpleSearch(final Function pFunction,
																final double pMin,
																final double pMax,
																final int pNumberOfPoints)
	{
		mX = new double[pNumberOfPoints];
		for (int i = 0; i < pNumberOfPoints; i++)
		{
			final double xn = (double) i / (pNumberOfPoints - 1);
			final double xntransformed = 2 * (xn - 0.5);
			final double xninflection = Math.signum(xntransformed) * Math.pow(Math.abs(xntransformed),
																																				mInflection);
			final double xninversetransformed = 0.5 + 0.5 * xninflection;
			final double x = pMin + (pMax - pMin) * xninversetransformed;
			mX[i] = x;
		}
		mY = pFunction.f(mX);

		final double lHighestX = argmax(mX, mY);

		UnivariateDifferentiableFunction lUnivariateDifferentiableFunction;

		final double lBestX;

		final GaussianFitter lGaussianFitter = new GaussianFitter(new LevenbergMarquardtOptimizer());

		final int length = mY.length;
		final double[] lSortedFocusMeasureArray = new double[length];

		for (int z = 0; z < length; z++)
		{
			lSortedFocusMeasureArray[z] = mY[z];
		}
		Arrays.sort(lSortedFocusMeasureArray);

		final double mMinFocusMeasure = lSortedFocusMeasureArray[lSortedFocusMeasureArray.length / 10];
		for (int z = 0; z < length; z++)
		{
			final double x = mX[z];
			final double y = mY[z] - mMinFocusMeasure;
			lGaussianFitter.addObservedPoint(x, y);
		}

		final double[] parameters = lGaussianFitter.fit();
		final double lNorm = parameters[0];
		final double lMean = parameters[1];
		final double lSigma = parameters[2];

		lUnivariateDifferentiableFunction = new Gaussian(	lNorm,
																											lMean,
																											lSigma);

		final double lXmin = min(mX);
		final double lXmax = max(mX);

		if (lMean < lXmin || lMean > lXmax)
		{
			lBestX = lHighestX;
		}
		else
		{
			lBestX = lMean;
		}

		mLastComputedRMSD = computeNormalizedRMSD(mX,
																							mY,
																							lUnivariateDifferentiableFunction);

		mLastBestY = max(mY);

		return lBestX;
	}

	private double computeNormalizedRMSD(	final double[] pX,
																				final double[] pY,
																				final UnivariateDifferentiableFunction pUnivariateDifferentiableFunction)
	{
		final double lMinY = min(pY);
		final double lHeight = max(pY) - lMinY;

		double lRMSD = 0;
		for (int i = 0; i < pX.length; i++)
		{
			final double x = mX[i];
			final double y = (mY[i] - lMinY) / lHeight;
			final double ey = (pUnivariateDifferentiableFunction.value(x) - lMinY) / lHeight;
			lRMSD += Math.pow(y - ey, 2);
		}
		lRMSD = lRMSD / pX.length;
		lRMSD = Math.sqrt(lRMSD);
		return lRMSD;
	}

	private double max(final double[] pX)
	{
		double maxx = Double.NEGATIVE_INFINITY;
		for (final double x : pX)
		{
			maxx = maxx < x ? x : maxx;
		}
		return maxx;
	}

	private double min(final double[] pX)
	{
		double minx = Double.POSITIVE_INFINITY;
		for (final double x : pX)
		{
			minx = minx > x ? x : minx;
		}
		return minx;
	}

	private double argmax(final double[] pX, final double[] pY)
	{
		double xm = Double.NaN;
		double ym = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < pY.length; i++)
		{
			final double y = pY[i];
			if (y > ym)
			{
				ym = y;
				xm = pX[i];
			}
		}
		return xm;
	}

	/**
	 * Returns the inflection parameter
	 * 
	 * @return inflection parameter
	 */
	public double getInflection()
	{
		return mInflection;
	}

	/**
	 * Sets the inflection parameter
	 * 
	 * @param inflection
	 *          inflection parameter
	 */
	public void setInflection(final double inflection)
	{
		mInflection = inflection;
	}

	/**
	 * Returns the last best Y found.
	 * 
	 * @return last best y
	 */
	public double getLastBestY()
	{
		return mLastBestY;
	}

	/**
	 * Returns the last normalized RMSD computed.
	 * 
	 * @return last norm. RMSD computed
	 */
	public double getLastComputedNormalizedRMSD()
	{
		return mLastComputedRMSD;
	}

}
