package autopilot.stackanalysis.plane2d.xi2reg;

import static java.lang.Math.min;
import static java.lang.Math.sqrt;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

public class Xi2RegPlane2DFitDistribution implements Serializable
{

	private static final ConcurrentHashMap<Integer, ChiSquaredDistribution> sChiSquaredDistributionCache = new ConcurrentHashMap<Integer, ChiSquaredDistribution>();

	private static final long serialVersionUID = 1L;
	private static final int cBufferLength = 128 * 1024 * 1024;

	public Xi2RegPlane2DFitDistribution()
	{
	}

	public double getPValue(int n, int k, double x)
	{
		if (k <= 3)
			return 1;

		final double lUncorrectedPValue = getPValue(k, x);

		final double lPValue = min(1, (1.0 * n / k) * lUncorrectedPValue);/**/

		/*final double lPValue = min(	1,
																CombinatoricsUtils.binomialCoefficientDouble(	n,
																																							k) * lUncorrectedPValue);/**/

		return lPValue;
	}

	public double getPValue(int k, double x)
	{
		if (k <= 3)
			return 1;

		final int lDegreesOfFreedom = k - 3;

		ChiSquaredDistribution lChiSquaredDistribution = sChiSquaredDistributionCache.get(lDegreesOfFreedom);

		if (lChiSquaredDistribution == null)
		{
			lChiSquaredDistribution = new ChiSquaredDistribution(lDegreesOfFreedom);
			sChiSquaredDistributionCache.put(	lDegreesOfFreedom,
																				lChiSquaredDistribution);
		}

		final double lRho = (128.0 / 9);

		final double lNorm = lChiSquaredDistribution.probability(	0,
																															lRho * (lDegreesOfFreedom));

		final double lPValue = lChiSquaredDistribution.probability(	0,
																																x		* lRho
																																		* (lDegreesOfFreedom)) / lNorm;

		return lPValue;
	}

	public double getZScore(int k, double x)
	{
		if (k < 7)
			return 0;

		final double lRho = (128.0 / 9);
		final double lMean = 1 / lRho;
		final double lSigma = sqrt(2 / lRho);
		final double lZScore = (x - lMean) / lSigma;

		return lZScore;

	}

	private static void println(String pString)
	{
		// System.out.println(pString);
	}

	private static void format(String format, Object... args)
	{
		// System.out.format(format, args);
	}
}
