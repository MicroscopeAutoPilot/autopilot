package autopilot.fmatrix.solvers.l2;

import java.util.ArrayList;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.simple.SimpleMatrix;

import autopilot.fmatrix.compiler.CompileMatrix;
import autopilot.fmatrix.compiler.CompileObservationVector;
import autopilot.fmatrix.constraingraph.ConstrainGraph;
import autopilot.fmatrix.constraingraph.constrains.Constrain;
import autopilot.fmatrix.solvers.SolverAbstract;
import autopilot.fmatrix.solvers.SolverInterface;
import autopilot.fmatrix.vector.ObservationVector;
import autopilot.fmatrix.vector.StateVector;

/**
 * Least Square (L2) solver. Finds the microscope state correction vector of
 * least 'energy'.Finds the microscope state correction vector of least 'energy'
 * 
 * @author royer
 */
public class L2FocusMatrixSolver extends SolverAbstract	implements
																												SolverInterface
{

	private volatile int mReturnCode = 0;

	/**
	 * @see autopilot.fmatrix.solvers.SolverInterface#delta(autopilot.fmatrix.constraingraph.ConstrainGraph,
	 *      autopilot.fmatrix.vector.StateVector,
	 *      autopilot.fmatrix.vector.ObservationVector)
	 */
	@Override
	public DenseMatrix64F delta(final ConstrainGraph pFocusMatrixModel,
															final StateVector pCurrentStateVector,
															final ObservationVector pObservationVector)
	{
		final SimpleMatrix lFocusMatrix = CompileMatrix.compile(pFocusMatrixModel);

		final ArrayList<Constrain> lObservableConstrains = pFocusMatrixModel.getObservableConstrains();
		println(lObservableConstrains.toString());
		final ArrayList<Constrain> lCostantConstrains = pFocusMatrixModel.getConstantConstrains();
		println(lCostantConstrains.toString());

		println("lFocusMatrix=\n" + lFocusMatrix);

		final DenseMatrix64F lFocusMatrixPseudoInverse = computePseudoInverse(lFocusMatrix);

		println("lFocusMatrixPseudoInverse=\n" + lFocusMatrixPseudoInverse);

		final SimpleMatrix lTemplateObservationVector = CompileObservationVector.compile(	pFocusMatrixModel,
																																											pCurrentStateVector,
																																											pObservationVector.getMatrix());

		println("lTemplateObservationVector=\n" + lTemplateObservationVector);

		final DenseMatrix64F lCorrections = new DenseMatrix64F(	lFocusMatrixPseudoInverse.getNumRows(),
																														1);

		CommonOps.mult(	lFocusMatrixPseudoInverse,
										lTemplateObservationVector.getMatrix(),
										lCorrections);

		println("lCorrections=\n" + lCorrections);
		return lCorrections;
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

	/* (non-Javadoc)
	 * @see autopilot.fmatrix.solvers.SolverInterface#getReturnCode()
	 */
	@Override
	public int getReturnCode()
	{
		return mReturnCode;
	}
}
