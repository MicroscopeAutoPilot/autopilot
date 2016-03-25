package rtlib.core.math.argmax.methods;

import static java.lang.Math.max;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.OutOfRangeException;

import rtlib.core.math.argmax.ArgMaxFinder1DInterface;
import rtlib.core.math.argmax.ComputeFitError;
import rtlib.core.math.argmax.Fitting1D;
import rtlib.core.math.argmax.Fitting1DBase;
import rtlib.core.math.argmax.UnivariateFunctionArgMax;

public class RandomSplineFitArgMaxFinder extends Fitting1DBase	implements
																ArgMaxFinder1DInterface,
																Fitting1D
{

	private static final int cNumberOfSamples = 1024;

	private PolynomialSplineFunction[] mPolynomialSplineFunctions;

	private SplineInterpolator mSplineInterpolator = new SplineInterpolator();

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		if (mPolynomialSplineFunctions == null)
			fit(pX, pY);

		double lArgMax = UnivariateFunctionArgMax.argmaxmean(	pX,
																mPolynomialSplineFunctions,
																cNumberOfSamples);

		mPolynomialSplineFunctions = null;
		return lArgMax;
	}

	@Override
	public double[] fit(double[] pX, double[] pY)
	{
		final int lNumberOfWeakInterpolators = 3 * pX.length;

		if (pX.length < 3)
		{
			mRMSD = 0;
			return Arrays.copyOf(pY, pY.length);
		}

		final int lNumberOfControlPoints = max(3, pX.length / 3);

		mPolynomialSplineFunctions = new PolynomialSplineFunction[lNumberOfWeakInterpolators];

		ThreadLocalRandom lRandom = ThreadLocalRandom.current();

		for (int i = 0; i < lNumberOfWeakInterpolators; i++)
		{
			TDoubleArrayList lXList = new TDoubleArrayList();
			TDoubleArrayList lYList = new TDoubleArrayList();

			int lMaxAttempts = 128;
			do
			{
				lXList.clear();
				lYList.clear();
				for (int d = 0; d < pX.length; d++)
					if (lRandom.nextBoolean())
					{
						lXList.add(pX[d]);
						lYList.add(pY[d]);
					}
				lMaxAttempts--;
			}
			while (lXList.size() < lNumberOfControlPoints && lMaxAttempts > 0);

			if (lMaxAttempts > 0)
				mPolynomialSplineFunctions[i] = mSplineInterpolator.interpolate(lXList.toArray(),
																				lYList.toArray());
		}

		double[] lFittedY = new double[pY.length];

		for (int j = 0; j < pX.length; j++)
		{
			int lCount = 0;
			double lValue = 0;
			for (int i = 0; i < lNumberOfWeakInterpolators; i++)
				try
				{
					lValue += mPolynomialSplineFunctions[i].value(pX[j]);
					lCount++;
				}
				catch (OutOfRangeException e)
				{
				}

			if (lCount != 0)
				lFittedY[j] = lValue / lCount;
		}

		mRMSD = ComputeFitError.rmsd(pY, lFittedY);

		return lFittedY;
	}

	@Override
	public String toString()
	{
		return String.format(	"RandomSplineFitArgMaxFinder [mPolynomialSplineFunctions=%s]",
								Arrays.toString(mPolynomialSplineFunctions));
	}

}
