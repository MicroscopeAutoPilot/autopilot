package autopilot.maxsearch;

/**
 * Abstract implementation of an objective function that provides the necessary
 * machinery for evaluating a list of values.
 * 
 * @author royer
 */
public abstract class FunctionAbstract implements Function
{

	/**
	 * @see autopilot.maxsearch.Function#f(double)
	 */
	@Override
	public abstract double f(double x);

	/**
	 * @see autopilot.maxsearch.Function#f(double[])
	 */
	@Override
	public double[] f(final double... pXArray)
	{
		final int length = pXArray.length;
		final double[] lYArray = new double[length];

		for (int i = 0; i < length; i++)
		{
			final double x = pXArray[i];
			lYArray[i] = f(x);
		}
		return lYArray;
	}

}
