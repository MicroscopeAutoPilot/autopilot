package autopilot.utils.rtlib.core.math.argmax.methods;

import static java.lang.Math.max;
import static java.lang.Math.min;

import autopilot.utils.rtlib.core.math.argmax.ArgMaxFinder1DInterface;
import gnu.trove.list.array.TDoubleArrayList;

public class NormalizingArgMaxFinder implements
		ArgMaxFinder1DInterface
{

	private ArgMaxFinder1DInterface mArgMaxFinder1DInterface;

	public NormalizingArgMaxFinder(ArgMaxFinder1DInterface pArgMaxFinder1DInterface)
	{
		super();
		mArgMaxFinder1DInterface = pArgMaxFinder1DInterface;
	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		TDoubleArrayList lNormY = new TDoubleArrayList();

		double lMin = Double.POSITIVE_INFINITY;
		double lMax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < pY.length; i++)
		{
			lMin = min(lMin, pY[i]);
			lMax = max(lMax, pY[i]);
		}

		for (int i = 0; i < pX.length; i++)
		{
			final double lScaledValue = (pY[i] - lMin) / (lMax - lMin);
			lNormY.add(lScaledValue);
		}

		final Double lArgmax = mArgMaxFinder1DInterface.argmax(	pX,
																lNormY.toArray());

		return lArgmax;
	}

	@Override
	public String toString()
	{
		return String.format(	"NormalizingArgMaxFinder [%s]",
								mArgMaxFinder1DInterface);
	}

}
