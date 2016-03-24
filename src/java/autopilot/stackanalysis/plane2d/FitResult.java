package autopilot.stackanalysis.plane2d;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import java.util.Arrays;

public class FitResult
{
	public double ms;
	public double pvalue;
	public double zscore;
	public double[] regparams;
	public boolean[] inliers;

	public boolean betterThan(FitResult pOtherFitResult)
	{
		if (pvalue < pOtherFitResult.pvalue)
			return true;
		return false;
	}

	public boolean same(FitResult pOtherFitResult)
	{
		if (pvalue == pOtherFitResult.pvalue)
			if (zscore == pOtherFitResult.zscore)
				return true;
		return false;
	}

	public double distanceTo(double x, double y, double z)
	{
		if (regparams == null)
			return Double.NaN;

		final double a = regparams[1];
		final double b = regparams[2];
		final double c = -1;
		final double d = regparams[0];

		final double dist = abs(a * x + b * y + c * z + d) / sqrt(a * a
																															+ b
																															* b
																															+ c
																															* c);
		return dist;
	}

	public int countInliers()
	{
		int lCount = 0;
		for (int i = 0; i < inliers.length; i++)
			if (inliers[i])
				lCount++;
		return lCount;
	}

	@Override
	public String toString()
	{
		return String.format(	"FitResult [nbi=%d, ms=%s, pvalue=%s, zscore=%s regparams=%s]",
													countInliers(),
													ms,
													pvalue,
													zscore,
													Arrays.toString(regparams));
	}

}
