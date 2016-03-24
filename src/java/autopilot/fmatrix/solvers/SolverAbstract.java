package autopilot.fmatrix.solvers;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.simple.SimpleMatrix;

import autopilot.fmatrix.constraingraph.ConstrainGraph;
import autopilot.fmatrix.vector.ObservationVector;
import autopilot.fmatrix.vector.StateVector;

/**
 * Abstract class for all solvers. Contains basic machinery to handle logging
 * and solving the constrain graph.
 * 
 * @author royer
 */
public abstract class SolverAbstract implements SolverInterface
{

	protected boolean mStdOut;
	protected boolean mLogFile;

	@Override
	public void setLogging(final boolean pStdOut, final boolean pLogFile)
	{
		mStdOut = pStdOut;
		mLogFile = pLogFile;
	}

	@Override
	public abstract void solve(	final ConstrainGraph pFocusMatrixModel,
															final StateVector pCurrentStateVector,
															final ObservationVector pObservationVector,
															final StateVector pNewStateVector);

	/**
	 * Computes the pseudo-inverse for a given matrix.
	 * 
	 * @param pMatrix
	 *          matrix from which the pseudo inverse must be computed.
	 * @return pseudo-inverse.
	 */
	protected DenseMatrix64F computePseudoInverse(final SimpleMatrix pMatrix)
	{
		final DenseMatrix64F lMatrixPseudoInverse = new DenseMatrix64F(	pMatrix.getMatrix()
																																						.getNumCols(),
																																		pMatrix.getMatrix()
																																						.getNumRows());
		CommonOps.pinv(pMatrix.getMatrix(), lMatrixPseudoInverse);
		return lMatrixPseudoInverse;
	}

	/**
	 * Println function that is used instead of System.println for logging.
	 * 
	 * @param pString
	 */
	protected void println(final String pString)
	{
		if (mStdOut)
		{
			System.out.println(pString);
		}

	}

}
