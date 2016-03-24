package autopilot.maxsearch;

/**
 * Abstract class for {@code MaximumSearch} implementations that provides some
 * convenience methods.
 * 
 * @author royer
 */
public abstract class MaximumSearchAbstract
{

	/**
	 * Finds maximum of provided function at a given precision.
	 * 
	 * @param pFunction
	 *          function to search maximum from
	 * @param pPrecision
	 *          precision for the search
	 * @return argument maximum
	 */
	public double findMaximum(final Function pFunction,
														final double pPrecision)
	{
		return findMaximum(	pFunction,
												pFunction.getXMin(),
												pFunction.getXMax(),
												pPrecision);
	}

	/**
	 * Returns the argument maximum of the provided function given an interval
	 * [xmin,xmax] and precision.
	 * 
	 * @param pFunction
	 *          function
	 * @param pXMin
	 *          minimum for x
	 * @param pXMax
	 *          maximum for x
	 * @param pPrecision
	 *          precision
	 * @return argmax
	 */
	public abstract double findMaximum(	Function pFunction,
																			final double pXMin,
																			final double pXMax,
																			final double pPrecision);

}
