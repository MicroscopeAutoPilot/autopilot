package autopilot.maxsearch;

/**
 * Interface for maximum search algorithm implementations.
 * 
 * @author royer
 */
public interface MaximumSearchInterface
{

	/**
	 * Returns the argument maximum of a function f for a given precision.
	 * 
	 * @param pFunction
	 *          function f
	 * @param pPrecision
	 *          precision
	 * @return arg max of function f
	 */
	public double findMaximum(Function pFunction,
														final double pPrecision);

	/**
	 * Returns the argument maximum of a function f for a given search interval
	 * [xmin,xmax] and precision.
	 * 
	 * @param pFunction
	 *          function f
	 * @param pMin
	 *          minimum for x
	 * @param pMax
	 *          maximum for x
	 * @param pPrecision
	 *          precision
	 * @return arg max of function f within [xmin,xmax]
	 */
	public double findMaximum(Function pFunction,
														final double pMin,
														final double pMax,
														final double pPrecision);

}
