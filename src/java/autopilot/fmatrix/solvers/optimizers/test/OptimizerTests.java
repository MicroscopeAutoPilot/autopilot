package autopilot.fmatrix.solvers.optimizers.test;

import java.util.Arrays;

import org.apache.commons.math3.analysis.MultivariateFunction;
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
import org.junit.Test;

public class OptimizerTests
{

	final MultivariateFunction mMultivariateFunction = new MultivariateFunction()
	{

		@Override
		public double value(final double[] pPoint)
		{
			return Math.abs(pPoint[0] - 2.01 * pPoint[1] + 2.01 / 2) + Math.abs(pPoint[0] - 1.99
																																					* pPoint[1]
																																					+ 1.99
																																					/ 2)
							+ Math.abs(pPoint[0])
							+ Math.abs(pPoint[1]);
		}
	};

	@Test
	public void testSimplex()
	{
		final long lStartTime = System.nanoTime();

		final MultivariateOptimizer lMultivariateOptimizer = new SimplexOptimizer(0,
																																							0.0001);/**/

		final NelderMeadSimplex lNelderMeadSimplex = new NelderMeadSimplex(2);
		final InitialGuess lInitialGuess = new InitialGuess(new double[]
		{ 10, 10 });
		final ObjectiveFunction lObjectiveFunction = new ObjectiveFunction(mMultivariateFunction);
		final MaxIter lMaxIter = new MaxIter(10000);
		final MaxEval lMaxEval = new MaxEval(10000);
		final GoalType lGoalType = GoalType.MINIMIZE;

		PointValuePair lOptimize = null;

		lOptimize = lMultivariateOptimizer.optimize(lObjectiveFunction,
																								lInitialGuess,
																								lNelderMeadSimplex,
																								lMaxIter,
																								lMaxEval,
																								lGoalType);

		final long lStopTime = System.nanoTime();
		final double lElapsedTime = (1.0 * lStopTime - lStartTime) / 1000;
		System.out.println("time (us):" + lElapsedTime);
		System.out.println(Arrays.toString(lOptimize.getKey()));
		System.out.println(lOptimize.getValue());
	}

	@Test
	public void testBOBYQAOptimizer()
	{
		final long lStartTime = System.nanoTime();

		final MultivariateOptimizer lMultivariateOptimizer = new BOBYQAOptimizer(	5,
																																							10,
																																							0.0001);

		final InitialGuess lInitialGuess = new InitialGuess(new double[]
		{ 10, 10 });
		final ObjectiveFunction lObjectiveFunction = new ObjectiveFunction(mMultivariateFunction);
		final SimpleBounds lSimpleBounds = new SimpleBounds(new double[]
		{ -50, -50 }, new double[]
		{ 50, 50 });
		final MaxIter lMaxIter = new MaxIter(10000);
		final MaxEval lMaxEval = new MaxEval(10000);
		final GoalType lGoalType = GoalType.MINIMIZE;

		PointValuePair lOptimize = null;

		lOptimize = lMultivariateOptimizer.optimize(lObjectiveFunction,
																								lSimpleBounds,
																								lInitialGuess,
																								lMaxIter,
																								lMaxEval,
																								lGoalType);
		final long lStopTime = System.nanoTime();
		final double lElapsedTime = (1.0 * lStopTime - lStartTime) / 1000;
		System.out.println("time (us):" + lElapsedTime);
		System.out.println(Arrays.toString(lOptimize.getKey()));
		System.out.println(lOptimize.getValue());

	}
}
