package autopilot.fmatrix.solvers.optimizers;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.ejml.data.DenseMatrix64F;

/**
 * This class contains static methods used to perform the optimization.
 * 
 * @author royer
 */
public class Optimizers
{

	/**
	 * Simplex based optimizer. Given an optimization function, the maximum number
	 * of iterations and an absolute threshold, this optimizer returns the best
	 * vector X such that f(X) is minimal.
	 * 
	 * @param pMultiVariateFunction
	 *          function to optimize
	 * @param pSideLength
	 *          side length
	 * @param pMaxIter
	 *          maximum number of iterations
	 * @param pAbsoluteThreshold
	 *          absolute conversion threshold
	 * @return optimal vector
	 */
	public static final DenseMatrix64F optimizeSimplex(	final EnhancedMultivariateFunction pMultiVariateFunction,
																											final double pSideLength,
																											final int pMaxIter,
																											final double pAbsoluteThreshold)
	{
		final long lStartTime = System.nanoTime();

		final int lDimension = pMultiVariateFunction.getDimension();
		final MultivariateOptimizer lMultivariateOptimizer = new SimplexOptimizer(0,
																																							pAbsoluteThreshold);/**/

		final NelderMeadSimplex lNelderMeadSimplex = new NelderMeadSimplex(	lDimension,
																																				pSideLength);
		final double[] lInitialGuessArray = pMultiVariateFunction.getInitialGuess();
		final InitialGuess lInitialGuess = new InitialGuess(lInitialGuessArray);

		final ObjectiveFunction lObjectiveFunction = new ObjectiveFunction(pMultiVariateFunction);
		final MaxIter lMaxIter = new MaxIter(pMaxIter);
		final MaxEval lMaxEval = new MaxEval((lDimension + 1) * pMaxIter);
		final GoalType lGoalType = GoalType.MINIMIZE;

		PointValuePair lOptimize = new PointValuePair(lInitialGuessArray,
																									0);

		lOptimize = lMultivariateOptimizer.optimize(lObjectiveFunction,
																								lInitialGuess,
																								lNelderMeadSimplex,
																								lMaxIter,
																								lMaxEval,
																								lGoalType);

		final long lStopTime = System.nanoTime();
		final double lElapsedTime = (1.0 * lStopTime - lStartTime) / 1000;
		// System.out.println("time (us):" + lElapsedTime);
		// System.out.println(Arrays.toString(lOptimize.getKey()));
		// System.out.println(lOptimize.getValue());

		final DenseMatrix64F lPoint = new DenseMatrix64F(	lDimension,
																											1,
																											false,
																											lOptimize.getKey());

		return lPoint;
	}

	public static final DenseMatrix64F optimizeBOBYQA(final EnhancedMultivariateFunction pMultiVariateFunction,
																										final double pSideLength,
																										final int pMaxIter,
																										final double pAbsoluteThreshold)
	{
		final long lStartTime = System.nanoTime();

		final int lDimension = pMultiVariateFunction.getDimension();
		final int lNumberOfInterpolationPoints = lDimension + 2;
		final MultivariateOptimizer lMultivariateOptimizer = new BOBYQAOptimizer(	lNumberOfInterpolationPoints,
																																							pSideLength,
																																							pAbsoluteThreshold);

		final ObjectiveFunction lObjectiveFunction = new ObjectiveFunction(pMultiVariateFunction);

		final double[] lInitialGuessArray = pMultiVariateFunction.getInitialGuess();
		final InitialGuess lInitialGuess = new InitialGuess(lInitialGuessArray);

		final double[] lLowerBoundArray = pMultiVariateFunction.getLowerBound();
		final double[] lUpperBoundArray = pMultiVariateFunction.getUpperBound();
		final SimpleBounds lSimpleBounds = new SimpleBounds(lLowerBoundArray,
																												lUpperBoundArray);

		final MaxIter lMaxIter = new MaxIter(pMaxIter);
		final MaxEval lMaxEval = new MaxEval(pMaxIter * lNumberOfInterpolationPoints);

		final GoalType lGoalType = GoalType.MINIMIZE;

		final PointValuePair lOptimize = lMultivariateOptimizer.optimize(	lObjectiveFunction,
																																			lSimpleBounds,
																																			lInitialGuess,
																																			lMaxIter,
																																			lMaxEval,
																																			lGoalType);

		final long lStopTime = System.nanoTime();
		final double lElapsedTime = (1.0 * lStopTime - lStartTime) / 1000;
		System.out.println("time (us):" + lElapsedTime);
		// System.out.println(Arrays.toString(lOptimize.getKey()));

		final DenseMatrix64F lPoint = new DenseMatrix64F(	lDimension,
																											1,
																											true,
																											lOptimize.getKey());

		return lPoint;
	}
}
