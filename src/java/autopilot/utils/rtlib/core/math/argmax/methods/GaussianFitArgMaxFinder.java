package autopilot.utils.rtlib.core.math.argmax.methods;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import autopilot.utils.rtlib.core.math.argmax.ArgMaxFinder1DInterface;
import autopilot.utils.rtlib.core.math.argmax.ComputeFitError;
import autopilot.utils.rtlib.core.math.argmax.Fitting1D;
import autopilot.utils.rtlib.core.math.argmax.Fitting1DBase;

public class GaussianFitArgMaxFinder extends Fitting1DBase	implements
															ArgMaxFinder1DInterface,
															Fitting1D
{

	private double mLastMean;
	private GaussianCurveFitter mGaussianCurveFitter;
	protected Gaussian mGaussian;

	public GaussianFitArgMaxFinder()
	{
		this(1024);
	}

	public GaussianFitArgMaxFinder(int pMaxIterations)
	{
		super();
		mGaussianCurveFitter = GaussianCurveFitter.create()
													.withMaxIterations(pMaxIterations);
	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		if (mGaussian == null)
			if (fit(pX, pY) == null)
				return null;

		mGaussian = null;
		return mLastMean;
	}

	@Override
	public double[] fit(double[] pX, double[] pY)
	{
		WeightedObservedPoints lObservedPoints = new WeightedObservedPoints();

		for (int i = 0; i < pX.length; i++)
			lObservedPoints.add(pX[i], pY[i]);

		mGaussian = null;

		try
		{
			double[] lFitInfo = mGaussianCurveFitter.fit(lObservedPoints.toList());
			// System.out.println(Arrays.toString(lFitInfo));

			double lNorm = lFitInfo[0];
			double lMean = lFitInfo[1];
			double lSigma = lFitInfo[2];

			mLastMean = lMean;

			mGaussian = new Gaussian(lNorm, lMean, lSigma);

			double[] lFittedY = new double[pY.length];

			for (int i = 0; i < pX.length; i++)
				lFittedY[i] = mGaussian.value(pX[i]);

			mRMSD = ComputeFitError.rmsd(pY, lFittedY);

			return lFittedY;
		}
		catch (Throwable e)
		{
			return null;
		}

	}

	public Gaussian getFunction()
	{
		return mGaussian;
	}

	@Override
	public String toString()
	{
		return String.format(	"GaussianFitArgMaxFinder [mLastMean=%s, mGaussian=%s]",
								mLastMean,
								mGaussian);
	}

}
