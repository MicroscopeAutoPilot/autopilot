package autopilot.utils.rtlib.core.math.argmax.fitprob;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Arrays;
import java.util.Random;

import autopilot.utils.rtlib.core.math.argmax.methods.GaussianFitArgMaxFinder;
import autopilot.utils.rtlib.core.math.argmax.methods.ParabolaFitArgMaxFinder;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class RandomizedDataGaussianFitter
{
	private static final int cMaxIterationsForRandomizedDataFitting = 512;

	GaussianFitArgMaxFinder mGaussianFitArgMaxFinder = new GaussianFitArgMaxFinder(cMaxIterationsForRandomizedDataFitting);
	ParabolaFitArgMaxFinder mParabolaFitArgMaxFinder = new ParabolaFitArgMaxFinder(cMaxIterationsForRandomizedDataFitting / 2);

	private Random mRandom = new Random(System.nanoTime());
	private double[] mX;
	private double[] mY;
	private UnivariateDifferentiableFunction mUnivariateDifferentiableFunction;

	public RandomizedDataGaussianFitter()
	{
	}

	public RandomizedDataGaussianFitter(double[] pX, double[] pY)
	{
		mX = pX;
		mY = pY;
	}

	public Double computeRMSDForRandomData(double[] pX)
	{
		double[] lRandomY = generateRandomVector(	mRandom,
													new double[pX.length]);
		return computeRMSD(pX, lRandomY);
	}

	public Double computeRMSD() throws Exception
	{
		return computeRMSD(mX, mY);
	}

	public Double computeRMSD(double[] pX, double[] pY)
	{
		Double lRMSD = fitGaussian(pX, pY);
		if (lRMSD == null)
			lRMSD = fitparabola(pX, pY);
		return lRMSD;
	}

	private Double fitparabola(double[] pX, double[] pY)
	{

		try
		{
			double[] lFit = mParabolaFitArgMaxFinder.fit(pX, pY);
			if (lFit == null)
				return null;
			setFunction(mParabolaFitArgMaxFinder.getFunction());
			double lRMSD = mParabolaFitArgMaxFinder.getRMSD();

			double[] lCoefficients = mParabolaFitArgMaxFinder.getFunction()
																.getCoefficients();

			if (lCoefficients.length == 1)
				return null;
			if (lCoefficients.length == 3)
			{
				double a = lCoefficients[2];
				if (a > 0)
					return null;
			}

			return lRMSD;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			return null;
		}/**/
	}

	private Double fitGaussian(double[] pX, double[] pY)
	{
		try
		{
			double[] lFit = mGaussianFitArgMaxFinder.fit(pX, pY);
			if (lFit == null)
				return null;
			setFunction(mGaussianFitArgMaxFinder.getFunction());

			double lRMSD = mGaussianFitArgMaxFinder.getRMSD();
			return lRMSD;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public UnivariateDifferentiableFunction getFunction()
	{
		return mUnivariateDifferentiableFunction;
	}

	public void setFunction(UnivariateDifferentiableFunction pUnivariateDifferentiableFunction)
	{
		mUnivariateDifferentiableFunction = pUnivariateDifferentiableFunction;
	}

	public static double[] shuffle(	boolean pShuffle,
									Random pRandom,
									double[] pArray)
	{
		double[] lNewArray = Arrays.copyOf(pArray, pArray.length);
		if (pShuffle)
			for (int i = lNewArray.length - 1; i > 0; i--)
			{
				int lIndex = pRandom.nextInt(i + 1);
				double lValue = lNewArray[lIndex];
				lNewArray[lIndex] = lNewArray[i];
				lNewArray[i] = lValue;
			}

		return lNewArray;
	}

	public static double[] generateRandomVector(Random pRandom,
												double[] pArray)
	{
		for (int i = pArray.length - 1; i > 0; i--)
		{
			pArray[i] = pRandom.nextDouble();
		}

		normalizeInPlace(pArray);

		return pArray;
	}

	public static double[] normalizeCopy(double[] pY)
	{
		double[] lNormY = new double[pY.length];
		double lMin = Double.POSITIVE_INFINITY;
		double lMax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < pY.length; i++)
		{
			lMin = min(lMin, pY[i]);/**/
			lMax = max(lMax, pY[i]);/**/
		}

		for (int i = 0; i < pY.length; i++)
		{
			final double lScaledValue = (pY[i] - lMin) / (lMax - lMin);
			lNormY[i] = lScaledValue;
		}
		return lNormY;
	}

	public static double[] normalizeInPlace(double[] pY)
	{
		double lMin = Double.POSITIVE_INFINITY;
		double lMax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < pY.length; i++)
		{
			lMin = min(lMin, pY[i]);/**/
			lMax = max(lMax, pY[i]);/**/
		}

		for (int i = 0; i < pY.length; i++)
		{
			final double lScaledValue = (pY[i] - lMin) / (lMax - lMin);
			pY[i] = lScaledValue;
		}
		return pY;
	}

}
