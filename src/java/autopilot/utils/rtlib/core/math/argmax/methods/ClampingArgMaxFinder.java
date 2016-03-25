package autopilot.utils.rtlib.core.math.argmax.methods;

import static java.lang.Math.max;
import static java.lang.Math.min;
import autopilot.utils.rtlib.core.math.argmax.ArgMaxFinder1DInterface;

public class ClampingArgMaxFinder implements ArgMaxFinder1DInterface
{

	private ArgMaxFinder1DInterface mArgMaxFinder1DInterface;

	public ClampingArgMaxFinder(ArgMaxFinder1DInterface pArgMaxFinder1DInterface)
	{
		super();
		mArgMaxFinder1DInterface = pArgMaxFinder1DInterface;
	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		Double lArgmax = mArgMaxFinder1DInterface.argmax(pX, pY);

		if (lArgmax == null)
			return null;

		lArgmax = min(lArgmax, pX[pX.length - 1]);
		lArgmax = max(lArgmax, pX[0]);

		return lArgmax;
	}

	@Override
	public String toString()
	{
		return String.format(	"ClampingArgMaxFinder [%s]",
								mArgMaxFinder1DInterface);
	}

}
