package autopilot.fmatrix.solvers.l1;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.NormOps;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

import autopilot.fmatrix.compiler.CompileMatrix;
import autopilot.fmatrix.compiler.CompileObservationVector;
import autopilot.fmatrix.constraingraph.ConstrainGraph;
import autopilot.fmatrix.solvers.l2.L2FocusMatrixSolver;
import autopilot.fmatrix.solvers.optimizers.EnhancedMultivariateFunction;
import autopilot.fmatrix.vector.ObservationVector;
import autopilot.fmatrix.vector.StateVector;

/**
 * This class represents the function used for L1 minimization. It is used to
 * find X such that Y=MX and |X|l1 is minimum.
 * 
 * @author royer
 */
public class KernelSpaceL1SparsifyingOptimizationFunction	implements
																													EnhancedMultivariateFunction
{

	private boolean mStdOut = false;
	private boolean mLogFile = false;

	private final ConstrainGraph mConstrainGraph;
	private final SimpleMatrix mFocusMatrix;
	private final SimpleMatrix mObservationAndConstrainsVector;
	private final ObservationVector mObservationVector;
	private final SimpleMatrix mX;
	private SimpleMatrix mCorrections;
	private final int mKernelDimension;
	private final SimpleMatrix mKernelSpanningMatrix;
	private final SimpleMatrix mL2Solution;
	private final StateVector mCurrentStateVector;

	/**
	 * Constructs the optimization function from a constrain graph, current state
	 * vector, observation vector and an epsilon parameter that controls the
	 * definition of the system's kernel (if the eigenvalue is below epsilon in
	 * norm then the correspond eigenvector is in the kernel)
	 * 
	 * @param pConstrainGraph
	 *          constrain graph
	 * @param pCurrentStateVector
	 *          current state vector
	 * @param pObservationVector
	 *          observation vector
	 * @param pNullSpaceEpsilon
	 *          null space epsilon
	 */
	public KernelSpaceL1SparsifyingOptimizationFunction(final ConstrainGraph pConstrainGraph,
																											final StateVector pCurrentStateVector,
																											final ObservationVector pObservationVector,
																											final double pNullSpaceEpsilon)
	{
		super();
		mConstrainGraph = pConstrainGraph;
		mCurrentStateVector = pCurrentStateVector;
		mFocusMatrix = CompileMatrix.compile(pConstrainGraph);

		final SimpleSVD lSvd = mFocusMatrix.svd();
		mKernelDimension = SvdUtils.kernelDim(lSvd, pNullSpaceEpsilon);
		mKernelSpanningMatrix = SvdUtils.kernelMatrix(lSvd,
																									pNullSpaceEpsilon);

		mX = new SimpleMatrix(mKernelDimension, 1);

		SvdUtils.svddiag(mFocusMatrix.getMatrix());

		mObservationVector = pObservationVector;
		mObservationAndConstrainsVector = CompileObservationVector.compile(	pConstrainGraph,
																																				pCurrentStateVector,
																																				pObservationVector.getMatrix());
		mCorrections = new SimpleMatrix(mFocusMatrix.getMatrix()
																								.getNumCols(), 1);

		mL2Solution = getL2Solution();

	}

	public void setLogging(final boolean pStdOut, final boolean pLogFile)
	{
		mStdOut = pStdOut;
		mLogFile = pLogFile;
	}

	@Override
	public int getDimension()
	{
		return mKernelDimension;
	}

	@Override
	public double[] getLowerBound()
	{
		final int lDimension = getDimension();
		final double[] lLowerBound = new double[lDimension];
		for (int i = 0; i < lDimension; i++)
		{
			lLowerBound[i] = -1000;
		}
		return lLowerBound;
	}

	@Override
	public double[] getUpperBound()
	{
		final int lDimension = getDimension();
		final double[] lUpperBound = new double[lDimension];
		for (int i = 0; i < lDimension; i++)
		{
			lUpperBound[i] = 1000;
		}
		return lUpperBound;
	}

	@Override
	public double[] getInitialGuess()
	{
		final double[] lInitialGuess = new double[getDimension()];
		return lInitialGuess;
	}

	public SimpleMatrix getL2Solution()
	{
		final L2FocusMatrixSolver lL2FocusMatrixSolver = new L2FocusMatrixSolver();
		lL2FocusMatrixSolver.setLogging(mStdOut, mLogFile);
		final DenseMatrix64F lDeltaL2 = lL2FocusMatrixSolver.delta(	mConstrainGraph,
																																mCurrentStateVector,
																																mObservationVector);
		return SimpleMatrix.wrap(lDeltaL2);
	}

	public SimpleMatrix computeCorrection(final SimpleMatrix pX)
	{
		final SimpleMatrix lNullSpacePoint = mKernelSpanningMatrix.mult(pX);

		mCorrections.set(mL2Solution);
		mCorrections = mCorrections.plus(lNullSpacePoint);
		return mCorrections;
	}

	@Override
	public double value(final double[] pPoint)
	{
		for (int i = 0; i < pPoint.length; i++)
		{
			mX.set(i, pPoint[i]);
		}

		computeCorrection(mX);

		final double lSolutionSparsity = NormOps.normP(	mCorrections.getMatrix(),
																										1) / mCorrections.getNumElements();

		final double lObjectiveFunctionValue = lSolutionSparsity;

		return lObjectiveFunctionValue;
	}

}
