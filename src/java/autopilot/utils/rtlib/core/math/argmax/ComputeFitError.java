package autopilot.utils.rtlib.core.math.argmax;

import static java.lang.Math.pow;

public class ComputeFitError
{
	public static final double rmsd(double[] pY, double[] pFittedY)
	{
		double lAverageError = 0;
		for (int i = 0; i < pY.length; i++)
		{
			double lError = pow(pY[i] - pFittedY[i], 2);
			lAverageError += lError;
		}

		lAverageError = lAverageError / pY.length;

		return pow(lAverageError, 0.5);
	}
}
