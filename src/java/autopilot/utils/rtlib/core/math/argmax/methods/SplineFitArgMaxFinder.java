package autopilot.utils.rtlib.core.math.argmax.methods;

import autopilot.utils.rtlib.core.math.argmax.ArgMaxFinder1DInterface;
import autopilot.utils.rtlib.core.math.argmax.Fitting1DBase;
import autopilot.utils.rtlib.core.math.argmax.UnivariateFunctionArgMax;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import autopilot.utils.rtlib.core.math.argmax.ComputeFitError;
import autopilot.utils.rtlib.core.math.argmax.Fitting1D;

public class SplineFitArgMaxFinder extends Fitting1DBase implements
		ArgMaxFinder1DInterface,
														Fitting1D
{

	private static final int cNumberOfSamples = 1024;

	private PolynomialSplineFunction mPolynomialSplineFunction;

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		if (mPolynomialSplineFunction == null)
			fit(pX, pY);

		double lArgMax = UnivariateFunctionArgMax.argmax(	pX,
															mPolynomialSplineFunction,
															cNumberOfSamples);

		mPolynomialSplineFunction = null;
		return lArgMax;
	}

	@Override
	public double[] fit(double[] pX, double[] pY)
	{

		SplineInterpolator lSplineInterpolator = new SplineInterpolator();

		mPolynomialSplineFunction = lSplineInterpolator.interpolate(pX,
																	pY);

		double[] lFittedY = new double[pY.length];

		for (int i = 0; i < pX.length; i++)
			lFittedY[i] = mPolynomialSplineFunction.value(pX[i]);

		mRMSD = ComputeFitError.rmsd(pY, lFittedY);

		return lFittedY;
	}

	@Override
	public String toString()
	{
		return String.format(	"SplineFitArgMaxFinder [mPolynomialSplineFunction=%s]",
								mPolynomialSplineFunction);
	}

}
