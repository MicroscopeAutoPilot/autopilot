package autopilot.fmatrix.solvers.optimizers;

import org.apache.commons.math3.analysis.MultivariateFunction;

/**
 * Enhanced MultivariateFunction that can return informatin about its dimension,
 * bounds and an initial guess for optmization.
 * 
 * @author royer
 *
 */
public interface EnhancedMultivariateFunction	extends
																							MultivariateFunction
{

	/**
	 * Returns the dimension of the multivariate function.
	 * 
	 * @return dimension
	 */
	int getDimension();

	/**
	 * Returns the lower bound.
	 * 
	 * @return lower bound
	 */
	double[] getLowerBound();

	/**
	 * Returns the upper bound.
	 * 
	 * @return upper bound.
	 */
	double[] getUpperBound();

	/**
	 * Returns the initial guess.
	 * 
	 * @return initial guess.
	 */
	double[] getInitialGuess();

}
