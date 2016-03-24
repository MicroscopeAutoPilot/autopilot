package autopilot.fmatrix.solvers.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import autopilot.fmatrix.constraingraph.constrains.Constrain;
import autopilot.fmatrix.constraingraph.templates.ConstrainGraph2D2I;
import autopilot.fmatrix.solvers.SolverInterface;
import autopilot.fmatrix.solvers.l1.L1FocusMatrixSolver;
import autopilot.fmatrix.solvers.l2.L2FocusMatrixSolver;
import autopilot.fmatrix.solvers.qp.QPFocusMatrixSolver;
import autopilot.fmatrix.vector.ObservationVector;
import autopilot.fmatrix.vector.StateVector;

public class FocusMatrixSolverStabilityTests
{
	private static final int cNumberOfIterations = 1000;
	private static final double cTestRadius = 2;
	private static final double cMaxDeviation = 0.3;
	private static final double cEpsilon = 0.1;

	private static void println(Object pObject)
	{
		// System.out.println(pObject.toString());
	}

	final SolverInterface cL2FocusMatrixSolver = new L2FocusMatrixSolver();
	{
		cL2FocusMatrixSolver.setLogging(false, false);
	}

	final SolverInterface cL1FocusMatrixSolver = new L1FocusMatrixSolver();
	{
		cL1FocusMatrixSolver.setLogging(false, false);
	}

	final SolverInterface cQPFocusMatrixSolver = new QPFocusMatrixSolver();
	{
		cQPFocusMatrixSolver.setLogging(false, false);
	}

	SolverInterface cSolver = cQPFocusMatrixSolver;

	@Test
	public void testStabilitySimple2D2ISymetricAnchoredModel()
	{

		double[] lMaximalCorrections = new double[4];

		lMaximalCorrections[0] = 0.1;
		lMaximalCorrections[1] = 0.1;
		lMaximalCorrections[2] = 1;
		lMaximalCorrections[3] = 1;

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	true,
																																																	true,
																																																	1,
																																																	1,
																																																	null,
																																																	lMaximalCorrections);

		final StateVector lPreviousStateVector = new StateVector(	lConstrainGraph.getNumberOfWavelengths(),
																															lConstrainGraph.getNumberOfPlanes(),
																															2,
																															2,
																															1,
																															1);

		final StateVector lNewStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
																												lConstrainGraph.getNumberOfPlanes(),
																												2,
																												2,
																												1,
																												1);

		final ObservationVector lObservationVector = new ObservationVector(	lConstrainGraph.getNumberOfWavelengths(),
																																				lConstrainGraph.getNumberOfPlanes(),
																																				2,
																																				2,
																																				1,
																																				1);

		for (int i = 0; i < cNumberOfIterations; i++)
		{
			lObservationVector.setObservationZ(0, 0, 0, 0, rnd(0, 0.3));
			lObservationVector.setObservationZ(0, 0, 0, 1, rnd(1, 0.3));
			lObservationVector.setObservationZ(0, 0, 1, 0, rnd(2, 0.3));
			lObservationVector.setObservationZ(0, 0, 1, 1, rnd(3, 0.3));

			cSolver.solve(lConstrainGraph,
										lPreviousStateVector,
										lObservationVector,
										lNewStateVector);

			println(lNewStateVector);

			assertEquals(0, lNewStateVector.getValueAt(0), cTestRadius);
			assertEquals(0, lNewStateVector.getValueAt(1), cTestRadius);
			assertEquals(	lNewStateVector.getValueAt(1),
										lNewStateVector.getValueAt(0),
										cEpsilon);
			// assertEquals(0, lNewStateVector.getValueAt(2), cTestRadius);
			// assertEquals(0, lNewStateVector.getValueAt(3), cTestRadius);

			lPreviousStateVector.getMatrix()
													.set(lNewStateVector.getMatrix());
		}
	}

	@Test
	public void testStabilitySimple2D2ITwoColorsThreePlanesSymmetricAnchorModel()
	{

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	true,
																																																	true,
																																																	2,
																																																	3,
																																																	null,
																																																	null);

		for (final Constrain lConstrain : lConstrainGraph.getConstantConstrains())
		{
			println(lConstrain);
		}

		final StateVector lPreviousStateVector = new StateVector(	lConstrainGraph.getNumberOfWavelengths(),
																															lConstrainGraph.getNumberOfPlanes(),
																															2,
																															2,
																															1,
																															1);

		final StateVector lNewStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
																												lConstrainGraph.getNumberOfPlanes(),
																												2,
																												2,
																												1,
																												1);

		final ObservationVector lObservationVector = new ObservationVector(	lConstrainGraph.getNumberOfWavelengths(),
																																				lConstrainGraph.getNumberOfPlanes(),
																																				2,
																																				2,
																																				1,
																																				1);

		lPreviousStateVector.setDetectionZ(0, 0, 0, 1);
		lPreviousStateVector.setDetectionZ(0, 1, 0, 1);
		lPreviousStateVector.setDetectionZ(0, 2, 0, 1);
		lPreviousStateVector.setDetectionZ(1, 0, 0, -1);
		lPreviousStateVector.setDetectionZ(1, 1, 0, -1);
		lPreviousStateVector.setDetectionZ(1, 2, 0, -1);

		for (int i = 0; i < cNumberOfIterations; i++)
		{

			lObservationVector.setObservationZ(0, 0, 0, 0, rnd(0, 0.3));
			lObservationVector.setObservationZ(0, 0, 0, 1, rnd(1, 0.3));
			lObservationVector.setObservationZ(0, 1, 0, 0, rnd(2, 0.3));
			lObservationVector.setObservationZ(0, 1, 0, 1, rnd(3, 0.3));
			lObservationVector.setObservationZ(0, 1, 1, 0, rnd(4, 0.3));
			lObservationVector.setObservationZ(0, 1, 1, 1, rnd(5, 0.3));
			lObservationVector.setObservationZ(0, 2, 1, 0, rnd(6, 0.3));
			lObservationVector.setObservationZ(0, 2, 1, 1, rnd(7, 0.3));

			lObservationVector.setObservationZ(1, 0, 0, 0, rnd(8, 0.3));
			lObservationVector.setObservationZ(1, 0, 0, 1, rnd(9, 0.3));
			lObservationVector.setObservationZ(1, 1, 0, 0, rnd(10, 0.3));
			lObservationVector.setObservationZ(1, 1, 0, 1, rnd(11, 0.3));
			lObservationVector.setObservationZ(1, 1, 1, 0, rnd(12, 0.3));
			lObservationVector.setObservationZ(1, 1, 1, 1, rnd(13, 0.3));
			lObservationVector.setObservationZ(1, 2, 1, 0, rnd(14, 0.3));
			lObservationVector.setObservationZ(1, 2, 1, 1, rnd(15, 0.3));

			println(lObservationVector);

			cSolver.solve(lConstrainGraph,
										lPreviousStateVector,
										lObservationVector,
										lNewStateVector);

			println(lNewStateVector);

			assertEquals(lNewStateVector.getValueAt(0), 0, 3);
			assertEquals(lNewStateVector.getValueAt(1), 0, 3);
			assertEquals(lNewStateVector.getValueAt(2), 0, 3);
			assertEquals(lNewStateVector.getValueAt(3), 0, 3);

			lPreviousStateVector.getMatrix()
													.set(lNewStateVector.getMatrix());
		}

	}

	HashMap<Integer, Double> lSum = new HashMap<Integer, Double>();

	private double rnd(final int pId, final double pAmplitude)
	{
		Double lDouble = lSum.get(pId);
		if (lDouble == null)
		{
			lSum.put(pId, (double) 0);
			lDouble = (double) 0;
		}

		double lDelta = pAmplitude * 2 * (Math.random() - 0.5);

		if (Math.abs(lDouble) > cMaxDeviation)
		{
			final double sign = Math.signum(lDouble);
			lDelta = -sign * Math.abs(lDelta);
		}

		final double lNewDouble = lDouble + lDelta;

		lSum.put(pId, lNewDouble);

		return lDelta;
	}

}
