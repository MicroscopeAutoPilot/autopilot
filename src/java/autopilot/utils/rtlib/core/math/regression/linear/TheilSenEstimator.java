package autopilot.utils.rtlib.core.math.regression.linear;

import autopilot.utils.rtlib.core.math.functions.UnivariateAffineFunction;
import gnu.trove.list.array.TDoubleArrayList;

import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 * Theil-Sen estimator
 * 
 * In non-parametric statistics, the Theil-Sen estimator, also known as Sen's
 * slope estimator, slope selection, the single median method, or the Kendall
 * robust line-fit method,[6] is a method for robust linear regression that
 * chooses the median slope among all lines through pairs of two-dimensional
 * sample points. It is named after Henri Theil and Pranab K. Sen, who published
 * papers on this method in 1950 and 1968 respectively. It can be computed
 * efficiently, and is insensitive to outliers; it can be significantly more
 * accurate than simple linear regression for skewed and heteroskedastic data,
 * and competes well against simple least squares even for normally distributed
 * data. It has been called
 * "the most popular nonparametric technique for estimating a linear trend" .
 * 
 * A variation of the Theil-Sen estimator due to Siegel (1982) determines, for
 * each sample point (xi,yi), the median mi of the slopes (yj - yi)/(xj - xi) of
 * lines through that point, and then determines the overall estimator as the
 * median of these medians.
 * 
 * The Theil-Sen estimator is an unbiased estimator of the true slope in simple
 * linear regression. For many distributions of the response error, this
 * estimator has high asymptotic efficiency relative to least-squares
 * estimation. Estimators with low efficiency require more independent
 * observations to attain the same sample variance of efficient unbiased
 * estimators. The Theil-Sen estimator is more robust than the least-squares
 * estimator because it is much less sensitive to outliers: It has a breakdown
 * point of, meaning that it can tolerate arbitrary corruption of up to 29.3% of
 * the input data-points without degradation of its accuracy. However, the
 * breakdown point decreases for higher-dimensional generalizations of the
 * method. A higher breakdown point, 50%, holds for the repeated median
 * estimator of Siegel.
 * 
 * Source: http://en.wikipedia.org/wiki/Theil%E2%80%93 Sen_estimator
 * 
 * @author royerloic
 */
public class TheilSenEstimator
{

	TDoubleArrayList lListX = new TDoubleArrayList();
	TDoubleArrayList lListY = new TDoubleArrayList();

	private UnivariateAffineFunction mLinear1to1;
	private boolean mOutdated = true;
	private final boolean mClassicMethod = true;

	public final void reset()
	{
		lListX.clear();
		lListY.clear();
		mOutdated = true;
	}

	public final void enter(final double pX, final double pY)
	{
		lListX.add(pX);
		lListY.add(pY);
		mOutdated = true;
	}

	public final UnivariateAffineFunction getModel()
	{
		if (mOutdated)
		{
			if (mClassicMethod)
			{
				classicmethod();
			}
			else
			{
				newmethod();
			}
			mOutdated = false;
		}

		return mLinear1to1;
	}

	private void newmethod()
	{
		final double[] arrayX = lListX.toArray();
		final double[] arrayY = lListY.toArray();
		final int length = arrayX.length;

		final Median lMedian = new Median();

		double[] lMedianOfMedianAData = new double[length];
		double[] lMedianOfMedianBData = new double[length];

		for (int i = 0; i < length; i++)
		{
			final double x1 = arrayX[i];
			final double y1 = arrayY[i];

			double[] lMedianAData = new double[i];
			double[] lMedianBData = new double[i];

			for (int j = 0; j < i; j++)
			{
				final double x2 = arrayX[j];
				final double y2 = arrayY[j];

				final double da = x1 - x2;
				if (da != 0)
				{
					final double a = (y1 - y2) / da;
					final double b = (x1 * y2 - x2 * y1) / da;
					lMedianAData[j] = a;
					lMedianBData[j] = b;
				}
			}

			if (lMedianAData.length > 0 && lMedianBData.length > 0)
			{
				final double ma = lMedian.evaluate(lMedianAData);
				lMedianOfMedianAData[i] = ma;

				final double mb = lMedian.evaluate(lMedianBData);
				lMedianOfMedianBData[i] = mb;
			}
		}

		final double a = lMedian.evaluate(lMedianOfMedianAData);
		final double b = lMedian.evaluate(lMedianOfMedianBData);

		mLinear1to1 = new UnivariateAffineFunction(a, b);
	}

	private void classicmethod()
	{
		final double[] arrayX = lListX.toArray();
		final double[] arrayY = lListY.toArray();
		final int length = arrayX.length;

		final Median lMedian = new Median();
		final double[] lMedianOfMedianData = new double[length];

		for (int i = 0; i < length; i++)
		{
			final double x1 = arrayX[i];
			final double y1 = arrayY[i];

			final double[] lMedianData = new double[i];

			for (int j = 0; j < i; j++)
			{
				final double x2 = arrayX[j];
				final double y2 = arrayY[j];

				final double da = x1 - x2;
				if (da != 0)
				{
					final double a = (y1 - y2) / da;
					lMedianData[j] = a;
				}
			}

			if (lMedianData.length > 0)
			{
				final double ma = lMedian.evaluate(lMedianData);
				lMedianOfMedianData[i] = ma;
			}
		}

		final double mma = lMedian.evaluate(lMedianOfMedianData);

		final double a = mma;

		final double[] lMedianForBData = new double[length];
		for (int i = 0; i < length; i++)
		{
			final double x = arrayX[i];
			final double y = arrayY[i];

			final double b = y - a * x;
			lMedianForBData[i] = b;
		}

		final double b = lMedian.evaluate(lMedianForBData);

		mLinear1to1 = new UnivariateAffineFunction(a, b);

	}

	public final double computeError(final UnivariateAffineFunction pModel)
	{
		final double[] arrayX = lListX.toArray();
		final double[] arrayY = lListY.toArray();
		final int length = arrayX.length;

		final Median lMedian = new Median();
		double[] lMedianData = new double[length];

		for (int j = 0; j < length; j++)
		{
			final double x = arrayX[j];
			final double y = arrayY[j];

			final double ey = pModel.value(x);

			final double error = Math.abs(y - ey);
			lMedianData[j] = error;
		}

		final double stderror = lMedian.evaluate(lMedianData);
		return stderror;
	}

	public final double predict(final double pX)
	{
		if (mOutdated)
		{
			getModel();
		}
		return mLinear1to1.value(pX);
	}

	@Override
	public String toString()
	{
		return String.format(	"TheilSenEstimator [mLinear1to1=%s]",
								getModel());
	}
}
