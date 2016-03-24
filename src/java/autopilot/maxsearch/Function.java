package autopilot.maxsearch;

/**
 * Interface for all objective functions
 * 
 * @author royer
 */
public interface Function
{
	/**
	 * Returns the min range in X
	 * 
	 * @return min range in X
	 */
	public double getXMin();

	/**
	 * Returns the maximum range in X
	 * 
	 * @return max range in X
	 */
	public double getXMax();

	/**
	 * Evaluates the function f at x.
	 * 
	 * @param x
	 *          value for x
	 * @return f(x)
	 */
	public double f(final double x);

	/**
	 * Evaluates function f for a list of values x1, x2, ..., xn
	 * 
	 * @param x
	 *          list/array of values x1, x2, ..., xn
	 * @return array f(x1), f(x2), ... , f(xn)
	 */
	public double[] f(final double... x);
}
