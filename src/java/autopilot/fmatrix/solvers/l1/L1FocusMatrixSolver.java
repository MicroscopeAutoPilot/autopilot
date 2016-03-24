package autopilot.fmatrix.solvers.l1;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.simple.SimpleMatrix;

import autopilot.fmatrix.constraingraph.ConstrainGraph;
import autopilot.fmatrix.solvers.SolverAbstract;
import autopilot.fmatrix.solvers.SolverInterface;
import autopilot.fmatrix.solvers.optimizers.Optimizers;
import autopilot.fmatrix.vector.ObservationVector;
import autopilot.fmatrix.vector.StateVector;

/**
 * L1 solver - finds the most sparse microscope state correction.
 * 
 * @author royer
 *
 */
public class L1FocusMatrixSolver extends SolverAbstract	implements
																												SolverInterface
{
	private volatile int mReturnCode = 0;

	/**
	 * @see autopilot.fmatrix.solvers.SolverInterface#delta(autopilot.fmatrix.constraingraph.ConstrainGraph,
	 *      autopilot.fmatrix.vector.StateVector,
	 *      autopilot.fmatrix.vector.ObservationVector)
	 */
	@Override
	public DenseMatrix64F delta(final ConstrainGraph pConstrainGraph,
															final StateVector pCurrentStateVector,
															final ObservationVector pObservationVector)
	{
		final KernelSpaceL1SparsifyingOptimizationFunction lMatrixModelFunction = new KernelSpaceL1SparsifyingOptimizationFunction(	pConstrainGraph,
																																																																pCurrentStateVector,
																																																																pObservationVector,
																																																																0.001);
		lMatrixModelFunction.setLogging(mStdOut, mLogFile);

		if (lMatrixModelFunction.getDimension() == 0)
		{
			println("Kernel of focus matrix is of rank 0, use L2 solver instead!");
			return lMatrixModelFunction.getL2Solution().getMatrix();
		}

		try
		{
			final DenseMatrix64F lX = Optimizers.optimizeSimplex(	lMatrixModelFunction,
																														1,
																														100000,
																														0.000001);

			return lMatrixModelFunction.computeCorrection(SimpleMatrix.wrap(lX))
																	.getMatrix();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			mReturnCode = 1;
			return lMatrixModelFunction.getL2Solution().getMatrix();
		}
	}

	/**
	 * @see autopilot.fmatrix.solvers.SolverAbstract#solve(autopilot.fmatrix.constraingraph.ConstrainGraph,
	 *      autopilot.fmatrix.vector.StateVector,
	 *      autopilot.fmatrix.vector.ObservationVector,
	 *      autopilot.fmatrix.vector.StateVector)
	 */
	@Override
	public void solve(final ConstrainGraph pFocusMatrixModel,
										final StateVector pCurrentStateVector,
										final ObservationVector pObservationVector,
										final StateVector pNewStateVector)
	{

		final DenseMatrix64F lCorrections = delta(pFocusMatrixModel,
																							pCurrentStateVector,
																							pObservationVector);

		CommonOps.add(pCurrentStateVector.getMatrix(),
									lCorrections,
									pNewStateVector.getMatrix());

		println("pCurrentStateVector=\n" + pCurrentStateVector);

		println("lNewStateVector=\n" + pNewStateVector);

	}

	@Override
	public int getReturnCode()
	{
		return mReturnCode;
	}
}