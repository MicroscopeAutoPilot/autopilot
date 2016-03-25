package autopilot.utils.rtlib.core.math.argmax.methods;

import autopilot.utils.rtlib.core.math.argmax.ArgMaxFinder1DInterface;

public class DenoisingArgMaxFinder implements ArgMaxFinder1DInterface
{

	private final ArgMaxFinder1DInterface mArgMaxFinder1DInterface;

	public DenoisingArgMaxFinder(ArgMaxFinder1DInterface pArgMaxFinder1DInterface)
	{
		super();
		mArgMaxFinder1DInterface = pArgMaxFinder1DInterface;
	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		final int lLength = pY.length;

		final double[] lY = new double[lLength];

		if (pY[0] > pY[1])
			lY[0] = pY[1];
		else
			lY[0] = pY[0];

		for (int i = 1; i < lLength - 1; i++)
		{
			if (pY[i] > pY[i - 1] && pY[i] > pY[i + 1])
				lY[i] = 0.5 * (pY[i - 1] + pY[i + 1]);
			else
				lY[i] = pY[i];
		}
		if (pY[lLength - 2] < pY[lLength - 1])
			lY[lLength - 1] = pY[lLength - 2];
		else
			lY[lLength - 1] = pY[lLength - 1];

		/*System.out.println("_____________________");
		for (final double y : lY)
			System.out.println(y); /**/

		final Double lArgmax = mArgMaxFinder1DInterface.argmax(pX, lY);

		return lArgmax;
	}

	@Override
	public String toString()
	{
		return String.format(	"DenoisingArgMaxFinder [%s]",
								mArgMaxFinder1DInterface);
	}

}
