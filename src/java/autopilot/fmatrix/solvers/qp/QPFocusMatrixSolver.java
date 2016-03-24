package autopilot.fmatrix.solvers.qp;

import static java.lang.Math.abs;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.varia.NullAppender;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.simple.SimpleMatrix;

import autopilot.fmatrix.compiler.CompileMatrix;
import autopilot.fmatrix.compiler.CompileObservationVector;
import autopilot.fmatrix.constraingraph.ConstrainGraph;
import autopilot.fmatrix.constraingraph.constrains.Constrain;
import autopilot.fmatrix.constraingraph.variables.ObservableVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;
import autopilot.fmatrix.constraingraph.variables.Variable;
import autopilot.fmatrix.solvers.SolverAbstract;
import autopilot.fmatrix.solvers.SolverInterface;
import autopilot.fmatrix.solvers.l1.L1FocusMatrixSolver;
import autopilot.fmatrix.vector.ObservationVector;
import autopilot.fmatrix.vector.StateVector;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;

/**
 * Quadratic Solver - This is the best solver, it uses a specially tailored
 * quadratic problem (see AutoPilot paper supplements).
 * 
 * @author royer
 *
 */
public class QPFocusMatrixSolver extends SolverAbstract	implements
																												SolverInterface
{

	static
	{
		// Getting rid of Log4J output:
		try
		{
			org.apache.log4j.BasicConfigurator.configure(new NullAppender());
		}
		catch (Throwable e)
		{
		}
	}

	private static final double cEpsilon = 0.0001;
	private static final int cMaxIterations = 10000;
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
		final long lStartTime = System.nanoTime();
		try
		{
			SimpleMatrix lMatrix = CompileMatrix.compile(pConstrainGraph);

			// System.out.println(lMatrix);

			SimpleMatrix lTrueY = CompileObservationVector.compile(	pConstrainGraph,
																															pCurrentStateVector,
																															pObservationVector.getMatrix());

			// System.out.println(lTrueY);

			DenseMatrix64F lX = null;

			final boolean lPositiveDefinite = lMatrix.transpose()
																								.mult(lMatrix)
																								.determinant() != 0;
			if (!lPositiveDefinite)
			{
				println("Matrix is not positive definite");
				if (pConstrainGraph.getNumberOfInequalityConstrains() == 0)
				{
					mReturnCode = -1;
					println("System is under-constrained, trying the L1 solver instead");
					return delegateToL1Solver(pConstrainGraph,
																		pCurrentStateVector,
																		pObservationVector);
				}
			}

			final int lDimension = pCurrentStateVector.getNumElements();
			double[] X0 = new double[lDimension];

			double[][] P = new double[lDimension][lDimension];
			double[] Q = new double[lDimension];
			double R = 0;

			double[][] A = new double[lDimension][lTrueY.getNumElements()];
			double[] B = new double[lTrueY.getNumElements()];

			computeP(lMatrix, P);
			computeQ(lMatrix, lTrueY, Q);
			R = computeR(lTrueY);
			computeA(lMatrix, A);
			computeB(lMatrix, B);

			// System.out.println("P=" + Arrays.deepToString(P));
			// System.out.println("Q=" + Arrays.toString(Q));
			// System.out.println("R=" + R);

			PDQuadraticMultivariateRealFunction lObjectiveFunction = new PDQuadraticMultivariateRealFunction(	P,
																																																				Q,
																																																				R);

			// inequalities
			ConvexMultivariateRealFunction[] lConstrainsInequalities = convertConstrainsIntoQPInequalities(pConstrainGraph);
			ConvexMultivariateRealFunction[] lLocalityInequalities = computeLocalityInequalities(	pConstrainGraph,
																																														lMatrix,
																																														lTrueY);

			ConvexMultivariateRealFunction[] lInequalities = ArrayUtils.addAll(	lConstrainsInequalities,
																																					lLocalityInequalities);

			// optimization problem
			OptimizationRequest lOptimizationRequest = new OptimizationRequest();
			lOptimizationRequest.setF0(lObjectiveFunction);
			lOptimizationRequest.setInitialPoint(X0);
			/*if ( == 0)
			{
				lOptimizationRequest.setA(A);
				lOptimizationRequest.setB(B);
			}/**/
			lOptimizationRequest.setFi(lInequalities);
			lOptimizationRequest.setToleranceFeas(1.E-4);
			lOptimizationRequest.setTolerance(1.E-4);

			// optimization
			JOptimizer lJOptimizer = new JOptimizer();
			lJOptimizer.setOptimizationRequest(lOptimizationRequest);
			int returnCode = lJOptimizer.optimize();
			if (returnCode != 0)
			{
				mReturnCode = 1;
				println("JOptimizer did not succeed or warned of a problem, we delegate to L1 solver instead (c=" + returnCode
								+ ")");
				return delegateToL1Solver(pConstrainGraph,
																	pCurrentStateVector,
																	pObservationVector);
			}

			double[] lQPSolution = lJOptimizer.getOptimizationResponse()
																				.getSolution();

			lX = DenseMatrix64F.wrap(lDimension, 1, lQPSolution);

			final long lStopTime = System.nanoTime();
			final double lElapsedTime = (1.0 * lStopTime - lStartTime) / 1000;
			println(QPFocusMatrixSolver.class.getSimpleName() + " time (us):"
							+ lElapsedTime);

			final SimpleMatrix lTentativeY = lMatrix.mult(SimpleMatrix.wrap(lX));

			println("lMatrix=" + lMatrix);
			println("lTentativeY=" + lTentativeY);
			println("lTrueY=" + lTrueY);

			return lX;
		}
		catch (Throwable e)
		{
			println("Problem while attempting to solve with QP, trying the L1 solver instead (e=" + e.getLocalizedMessage()
							+ ")");
			mReturnCode = 10;
			return delegateToL1Solver(pConstrainGraph,
																pCurrentStateVector,
																pObservationVector);
		}
	}

	DenseMatrix64F delegateToL1Solver(final ConstrainGraph pConstrainGraph,
																		final StateVector pCurrentStateVector,
																		final ObservationVector pObservationVector)
	{
		L1FocusMatrixSolver lL1FocusMatrixSolver = new L1FocusMatrixSolver();
		return lL1FocusMatrixSolver.delta(pConstrainGraph,
																			pCurrentStateVector,
																			pObservationVector);
	}

	private void computeP(SimpleMatrix pMatrix, double[][] P)
	{
		SimpleMatrix lMatrixP = pMatrix.transpose()
																		.mult(pMatrix)
																		.scale(2);
		for (int i = 0; i < P.length; i++)
			for (int j = 0; j < P.length; j++)
				P[i][j] = lMatrixP.get(i, j);

	}

	private void computeQ(SimpleMatrix pMatrix,
												SimpleMatrix pTrueY,
												double[] Q)
	{
		SimpleMatrix lMatrixQ = pMatrix.transpose()
																		.mult(pTrueY)
																		.scale(-2);
		for (int i = 0; i < Q.length; i++)
			Q[i] = lMatrixQ.get(i, 0);

	}

	private double computeR(SimpleMatrix pTrueY)
	{
		return pTrueY.transpose().mult(pTrueY).get(0);
	}

	private void computeA(SimpleMatrix pMatrix, double[][] A)
	{
		for (int i = 0; i < A.length; i++)
			for (int j = 0; j < A[0].length; j++)
				A[i][j] = pMatrix.get(j, i);
	}

	private void computeB(SimpleMatrix pMatrix, double[] B)
	{
		for (int i = 0; i < B.length; i++)
			B[i] = pMatrix.get(i, 0);
	}

	private ConvexMultivariateRealFunction[] convertConstrainsIntoQPInequalities(ConstrainGraph pConstrainGraph)
	{
		ArrayList<Constrain> lInequalityConstrains = pConstrainGraph.getInequalityConstrains();
		final int lNumberOfInequalityConstrains = lInequalityConstrains.size();
		ConvexMultivariateRealFunction[] lConvexMultivariateRealFunctions = new ConvexMultivariateRealFunction[2 * lNumberOfInequalityConstrains];

		int lNumberOfStateVariables = pConstrainGraph.getNumberOfStateVariables();

		if (lInequalityConstrains.size() == 0)
			return null;

		for (int i = 0; i < lNumberOfInequalityConstrains; i++)
		{
			Constrain lInequalityConstrain = lInequalityConstrains.get(i);
			StateVariable lStateVariable = lInequalityConstrain.getVariable1();
			int lStateVariableIndex = pConstrainGraph.getStateVariableIndex(lStateVariable);

			Variable lConstrainVariable = lInequalityConstrain.getConstrainVariable();
			double lInequalityConstrainValue = lConstrainVariable.getValue();

			double[] lInequalityVectorUpper = new double[lNumberOfStateVariables];
			lInequalityVectorUpper[lStateVariableIndex] = 1;

			final double lUpperBound = lInequalityConstrainValue;
			LinearMultivariateRealFunction lLinearMultivariateRealFunctionUpper = new LinearMultivariateRealFunction(	lInequalityVectorUpper,
																																																								-lUpperBound);

			final double lLowerBound = -lInequalityConstrainValue;
			double[] lInequalityVectorLower = new double[lNumberOfStateVariables];
			lInequalityVectorLower[lStateVariableIndex] = -1;

			LinearMultivariateRealFunction lLinearMultivariateRealFunctionLower = new LinearMultivariateRealFunction(	lInequalityVectorLower,
																																																								lLowerBound);

			lConvexMultivariateRealFunctions[i * 2] = lLinearMultivariateRealFunctionUpper;
			lConvexMultivariateRealFunctions[i * 2 + 1] = lLinearMultivariateRealFunctionLower;

		}

		return lConvexMultivariateRealFunctions;
	}

	private ConvexMultivariateRealFunction[] computeLocalityInequalities(	ConstrainGraph pConstrainGraph,
																																				SimpleMatrix pMatrix,
																																				SimpleMatrix pTrueY)
	{
		final int rows = pMatrix.numRows();
		final int cols = pMatrix.numCols();

		ArrayList<ConvexMultivariateRealFunction> lConvexMultivariateRealFunctionList = new ArrayList<ConvexMultivariateRealFunction>(2 * rows);

		for (int i = 0; i < rows; i++)
		{
			if (i < pConstrainGraph.getNumberOfObservableVariables())
			{
				ObservableVariable lObservableVariable = pConstrainGraph.getObservableVariableByIndex(i);
				if (lObservableVariable.isMissing())
					continue;
			}

			final double[] lInequalityVectorUpperBound = new double[cols];
			for (int j = 0; j < cols; j++)
				lInequalityVectorUpperBound[j] = pMatrix.get(i, j);

			final double[] lInequalityVectorLowerBound = new double[cols];
			for (int j = 0; j < cols; j++)
				lInequalityVectorLowerBound[j] = -pMatrix.get(i, j);

			final double lYValue = pTrueY.get(i, 0);

			final double lUpperBound = lYValue <= 0	? cEpsilon
																							: (1 + cEpsilon) * abs(lYValue)/**/;
			// abs(lYValue) + cEpsilon/*

			LinearMultivariateRealFunction lLinearMultivariateRealFunctionUpper = new LinearMultivariateRealFunction(	lInequalityVectorUpperBound,
																																																								-lUpperBound);

			final double lLowerBound = lYValue >= 0	? -cEpsilon
																							: -(1 + cEpsilon) * abs(lYValue)/**/;
			// -abs(lYValue) - cEpsilon /*
			LinearMultivariateRealFunction lLinearMultivariateRealFunctionLower = new LinearMultivariateRealFunction(	lInequalityVectorLowerBound,
																																																								lLowerBound);

			lConvexMultivariateRealFunctionList.add(lLinearMultivariateRealFunctionUpper);
			lConvexMultivariateRealFunctionList.add(lLinearMultivariateRealFunctionLower);

		}

		return lConvexMultivariateRealFunctionList.toArray(new ConvexMultivariateRealFunction[lConvexMultivariateRealFunctionList.size()]);
	}

	/**
	 * @see autopilot.fmatrix.solvers.SolverAbstract#solve(autopilot.fmatrix.constraingraph.ConstrainGraph,
	 *      autopilot.fmatrix.vector.StateVector,
	 *      autopilot.fmatrix.vector.ObservationVector,
	 *      autopilot.fmatrix.vector.StateVector)
	 */
	@Override
	public void solve(final ConstrainGraph pFocusMatrixModel,
										final StateVector pPreviousStateVector,
										final ObservationVector pObservationVector,
										final StateVector pNewStateVector)
	{

		final DenseMatrix64F lCorrections = delta(pFocusMatrixModel,
																							pPreviousStateVector,
																							pObservationVector);

		CommonOps.add(pPreviousStateVector.getMatrix(),
									lCorrections,
									pNewStateVector.getMatrix());

		println("pPreviousStateVector=\n" + pPreviousStateVector);

		println("lNewStateVector=\n" + pNewStateVector);

	}

	@Override
	public int getReturnCode()
	{
		return mReturnCode;
	}

}