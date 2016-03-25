package rtlib.core.math.argmax.methods;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import rtlib.core.math.argmax.ArgMaxFinder1DInterface;
import rtlib.core.math.argmax.ComputeFitError;
import rtlib.core.math.argmax.Fitting1D;
import rtlib.core.math.argmax.Fitting1DBase;

public class ParabolaFitArgMaxFinder extends Fitting1DBase	implements
															ArgMaxFinder1DInterface,
															Fitting1D
{

	private PolynomialCurveFitter mPolynomialCurveFitter;
	private PolynomialFunction mPolynomialFunction;

	public ParabolaFitArgMaxFinder()
	{
		this(1024);
	}

	public ParabolaFitArgMaxFinder(int pMaxIterations)
	{
		mPolynomialCurveFitter = PolynomialCurveFitter.create(2)
														.withMaxIterations(pMaxIterations);
	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		if (mPolynomialFunction == null)
			fit(pX, pY);

		double[] lCoefficients = mPolynomialFunction.getCoefficients();
		mPolynomialFunction = null;

		if (lCoefficients.length == 3)
		{
			double a = lCoefficients[2];
			double b = lCoefficients[1];

			double lArgMax = -b / (2 * a);

			return lArgMax;
		}
		else if (lCoefficients.length == 2)
		{
			double b = lCoefficients[1];

			if (b > 0)
				return pX[pX.length - 1];
			else
				return pX[0];

		}
		else if (lCoefficients.length == 1)
		{
			return null;
		}

		return null;
	}

	@Override
	public double[] fit(double[] pX, double[] pY)
	{
		WeightedObservedPoints lObservedPoints = new WeightedObservedPoints();

		for (int i = 0; i < pX.length; i++)
			lObservedPoints.add(pX[i], pY[i]);

		try
		{
			double[] lFitInfo = mPolynomialCurveFitter.fit(lObservedPoints.toList());

			mPolynomialFunction = new PolynomialFunction(lFitInfo);

			double[] lFittedY = new double[pY.length];

			for (int i = 0; i < pX.length; i++)
				lFittedY[i] = mPolynomialFunction.value(pX[i]);

			mRMSD = ComputeFitError.rmsd(pY, lFittedY);

			return lFittedY;
		}
		catch (Throwable e)
		{
			// e.printStackTrace();
			return null;
		}
	}

	public PolynomialFunction getFunction()
	{
		return mPolynomialFunction;
	}

	@Override
	public String toString()
	{
		return String.format(	"ParabolaFitArgMaxFinder [mPolynomialFunction=%s]",
								mPolynomialFunction);
	}

}
