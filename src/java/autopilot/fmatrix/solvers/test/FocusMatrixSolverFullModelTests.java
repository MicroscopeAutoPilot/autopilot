package autopilot.fmatrix.solvers.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import autopilot.fmatrix.constraingraph.constrains.Constrain;
import autopilot.fmatrix.constraingraph.templates.ConstrainGraph2D2I;
import autopilot.fmatrix.solvers.SolverInterface;
import autopilot.fmatrix.solvers.l1.L1FocusMatrixSolver;
import autopilot.fmatrix.solvers.l2.L2FocusMatrixSolver;
import autopilot.fmatrix.solvers.qp.QPFocusMatrixSolver;
import autopilot.fmatrix.vector.ObservationVector;
import autopilot.fmatrix.vector.StateVector;

public class FocusMatrixSolverFullModelTests
{
	private static final double cEpsilon = 0.02;

	private static void println(final Object pObject)
	{
		// System.out.println(pObject.toString());
	}

	final SolverInterface cL2FocusMatrixSolver = new L2FocusMatrixSolver();
	{
		cL2FocusMatrixSolver.setLogging(false, false);
	}

	final SolverInterface cInequalityConstrainAbleSolver = new QPFocusMatrixSolver();
	{
		cInequalityConstrainAbleSolver.setLogging(false, false);
	}

	final SolverInterface cL1FocusMatrixSolver = new L1FocusMatrixSolver();
	{
		cL1FocusMatrixSolver.setLogging(false, false);
	}

	@Test
	public void testFull2D2ISinglePlaneModel()
	{

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2IWithExtraDOF(	false,
																																																							false,
																																																							1,
																																																							1,
																																																							null,
																																																							null);

		final StateVector lCurrentStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
																														lConstrainGraph.getNumberOfPlanes(),
																														2,
																														2,
																														1,
																														4);

		final StateVector lNewStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
																												lConstrainGraph.getNumberOfPlanes(),
																												2,
																												2,
																												1,
																												4);

		final ObservationVector lObservationVector = new ObservationVector(	lConstrainGraph.getNumberOfWavelengths(),
																																				lConstrainGraph.getNumberOfPlanes(),
																																				2,
																																				2,
																																				1,
																																				4);

		lObservationVector.setObservationZ(0, 0, 0, 0, 1.01);
		lObservationVector.setObservationZ(0, 0, 0, 1, 1.02);

		println("lObservationVectorFRESH=\n" + lObservationVector);

		final long lStartTime1 = System.nanoTime();
		cL2FocusMatrixSolver.solve(	lConstrainGraph,
																lCurrentStateVector,
																lObservationVector,
																lNewStateVector);

		final long lStopTime1 = System.nanoTime();

		final long lElapsedTimeNanos1 = lStopTime1 - lStartTime1;
		final double lElapsedTimeMillis1 = (double) lElapsedTimeNanos1 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis1);

		assertEquals(lNewStateVector.getValueAt(0), 0.75, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(1), 0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(2), -0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(3), 0.25, cEpsilon);

		final long lStartTime2 = System.nanoTime();
		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		final long lStopTime2 = System.nanoTime();

		final long lElapsedTimeNanos2 = lStopTime2 - lStartTime2;
		final double lElapsedTimeMillis2 = (double) lElapsedTimeNanos2 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis2);

		println("lNewStateVector=" + lNewStateVector);

		assertEquals(lNewStateVector.getValueAt(0), 1, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(1), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(2), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(3), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(4), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(5), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(6), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(7), 0, cEpsilon);

		final long lStartTime3 = System.nanoTime();
		cL1FocusMatrixSolver.solve(	lConstrainGraph,
																lCurrentStateVector,
																lObservationVector,
																lNewStateVector);
		final long lStopTime3 = System.nanoTime();

		final long lElapsedTimeNanos3 = lStopTime3 - lStartTime3;
		final double lElapsedTimeMillis3 = (double) lElapsedTimeNanos3 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis3);

		assertEquals(lNewStateVector.getValueAt(0), 1, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(1), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(2), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(3), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(4), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(5), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(6), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(7), 0, cEpsilon);

	}

	@Test
	public void testFull2D2IThreePlanesModel()
	{

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2IWithExtraDOF(	false,
																																																							false,
																																																							1,
																																																							3,
																																																							null,
																																																							null);

		final StateVector lCurrentStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
																														lConstrainGraph.getNumberOfPlanes(),
																														2,
																														2,
																														1,
																														4);

		final StateVector lNewStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
																												lConstrainGraph.getNumberOfPlanes(),
																												2,
																												2,
																												1,
																												4);

		final ObservationVector lObservationVector = new ObservationVector(	lConstrainGraph.getNumberOfWavelengths(),
																																				lConstrainGraph.getNumberOfPlanes(),
																																				2,
																																				2,
																																				1,
																																				4);

		lObservationVector.setObservationZ(0, 0, 0, 0, 1.01);
		lObservationVector.setObservationZ(0, 0, 0, 1, 1.01);
		lObservationVector.setObservationZ(0, 1, 0, 0, 1.02);
		lObservationVector.setObservationZ(0, 1, 0, 1, 1.02);
		lObservationVector.setObservationZ(0, 2, 0, 0, 1.03);
		lObservationVector.setObservationZ(0, 2, 0, 1, 1.03);

		println("lObservationVectorFRESH=\n" + lObservationVector);

		final long lStartTime1 = System.nanoTime();
		cL2FocusMatrixSolver.solve(	lConstrainGraph,
																lCurrentStateVector,
																lObservationVector,
																lNewStateVector);

		final long lStopTime1 = System.nanoTime();

		final long lElapsedTimeNanos1 = lStopTime1 - lStartTime1;
		final double lElapsedTimeMillis1 = (double) lElapsedTimeNanos1 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis1);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(10 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(10 + 1),
									cEpsilon);

		final long lStartTime2 = System.nanoTime();
		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		final long lStopTime2 = System.nanoTime();

		final long lElapsedTimeNanos2 = lStopTime2 - lStartTime2;
		final double lElapsedTimeMillis2 = (double) lElapsedTimeNanos2 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis2);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(10 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(10 + 1),
									cEpsilon);

		final long lStartTime3 = System.nanoTime();
		cL1FocusMatrixSolver.solve(	lConstrainGraph,
																lCurrentStateVector,
																lObservationVector,
																lNewStateVector);

		final long lStopTime3 = System.nanoTime();

		final long lElapsedTimeNanos3 = lStopTime3 - lStartTime3;
		final double lElapsedTimeMillis3 = (double) lElapsedTimeNanos3 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis3);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(10 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(10 + 1),
									cEpsilon);

	}

	@Test
	public void testFull2D2IThreePlanesSymmetricAnchorModel()
	{

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2IWithExtraDOF(	true,
																																																							true,
																																																							1,
																																																							3,
																																																							null,
																																																							null);

		final StateVector lCurrentStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
																														lConstrainGraph.getNumberOfPlanes(),
																														2,
																														2,
																														1,
																														4);

		final StateVector lNewStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
																												lConstrainGraph.getNumberOfPlanes(),
																												2,
																												2,
																												1,
																												4);

		final ObservationVector lObservationVector = new ObservationVector(	lConstrainGraph.getNumberOfWavelengths(),
																																				lConstrainGraph.getNumberOfPlanes(),
																																				2,
																																				2,
																																				1,
																																				4);

		lObservationVector.setObservationZ(0, 0, 0, 0, 1.01);
		lObservationVector.setObservationZ(0, 0, 0, 1, 1.01);
		lObservationVector.setObservationZ(0, 1, 0, 0, 1.02);
		lObservationVector.setObservationZ(0, 1, 0, 1, 1.02);
		lObservationVector.setObservationZ(0, 2, 0, 0, 1.03);
		lObservationVector.setObservationZ(0, 2, 0, 1, 1.03);

		println("lObservationVectorFRESH=\n" + lObservationVector);

		final long lStartTime1 = System.nanoTime();
		cL2FocusMatrixSolver.solve(	lConstrainGraph,
																lCurrentStateVector,
																lObservationVector,
																lNewStateVector);

		final long lStopTime1 = System.nanoTime();

		final long lElapsedTimeNanos1 = lStopTime1 - lStartTime1;
		final double lElapsedTimeMillis1 = (double) lElapsedTimeNanos1 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis1);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(10 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(10 + 1),
									cEpsilon);

		final long lStartTime2 = System.nanoTime();
		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		final long lStopTime2 = System.nanoTime();

		final long lElapsedTimeNanos2 = lStopTime2 - lStartTime2;
		final double lElapsedTimeMillis2 = (double) lElapsedTimeNanos2 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis2);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(10 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(10 + 1),
									cEpsilon);

		final long lStartTime3 = System.nanoTime();
		cL1FocusMatrixSolver.solve(	lConstrainGraph,
																lCurrentStateVector,
																lObservationVector,
																lNewStateVector);

		final long lStopTime3 = System.nanoTime();

		final long lElapsedTimeNanos3 = lStopTime3 - lStartTime3;
		final double lElapsedTimeMillis3 = (double) lElapsedTimeNanos3 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis3);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(10 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(10 + 1),
									cEpsilon);

	}

	@Test
	public void testFull2D2ITwoColorThreePlanesSymmetricAnchorModel()
	{

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2IWithExtraDOF(	true,
																																																							true,
																																																							2,
																																																							3,
																																																							null,
																																																							null);

		final StateVector lCurrentStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
																														lConstrainGraph.getNumberOfPlanes(),
																														2,
																														2,
																														1,
																														4);

		final StateVector lNewStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
																												lConstrainGraph.getNumberOfPlanes(),
																												2,
																												2,
																												1,
																												4);

		final ObservationVector lObservationVector = new ObservationVector(	lConstrainGraph.getNumberOfWavelengths(),
																																				lConstrainGraph.getNumberOfPlanes(),
																																				2,
																																				2,
																																				1,
																																				4);

		lObservationVector.setObservationZ(0, 0, 0, 0, 1.01);
		lObservationVector.setObservationZ(0, 0, 0, 1, 1.01);
		lObservationVector.setObservationZ(0, 1, 0, 0, 1.02);
		lObservationVector.setObservationZ(0, 1, 0, 1, 1.02);
		lObservationVector.setObservationZ(0, 2, 0, 0, 1.03);
		lObservationVector.setObservationZ(0, 2, 0, 1, 1.03);

		lObservationVector.setObservationZ(1, 0, 0, 0, -1.01);
		lObservationVector.setObservationZ(1, 0, 0, 1, -1.01);
		lObservationVector.setObservationZ(1, 1, 0, 0, -1.02);
		lObservationVector.setObservationZ(1, 1, 0, 1, -1.02);
		lObservationVector.setObservationZ(1, 2, 0, 0, -1.03);
		lObservationVector.setObservationZ(1, 2, 0, 1, -1.03);

		println("lObservationVectorFRESH=\n" + lObservationVector);

		final long lStartTime1 = System.nanoTime();
		cL2FocusMatrixSolver.solve(	lConstrainGraph,
																lCurrentStateVector,
																lObservationVector,
																lNewStateVector);

		final long lStopTime1 = System.nanoTime();

		final long lElapsedTimeNanos1 = lStopTime1 - lStartTime1;
		final double lElapsedTimeMillis1 = (double) lElapsedTimeNanos1 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis1);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(10 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(10 + 1),
									cEpsilon);

		final long lStartTime2 = System.nanoTime();
		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		final long lStopTime2 = System.nanoTime();

		final long lElapsedTimeNanos2 = lStopTime2 - lStartTime2;
		final double lElapsedTimeMillis2 = (double) lElapsedTimeNanos2 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis2);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(10 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(10 + 1),
									cEpsilon);

		final long lStartTime3 = System.nanoTime();
		cL1FocusMatrixSolver.solve(	lConstrainGraph,
																lCurrentStateVector,
																lObservationVector,
																lNewStateVector);

		final long lStopTime3 = System.nanoTime();

		final long lElapsedTimeNanos3 = lStopTime3 - lStartTime3;
		final double lElapsedTimeMillis3 = (double) lElapsedTimeNanos3 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis3);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(10 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(10 + 1),
									cEpsilon);

	}

	@Test
	public void testFull2D2ITwoColorThreePlanesSymmetricAnchorMissingInfoModel()
	{

		final boolean[] lMissingInfo = new boolean[2 * 3 * 10];

		lMissingInfo[4] = true;
		lMissingInfo[5] = true;
		// lMissingInfo[10 + 4] = true;
		// lMissingInfo[10 + 5] = true;

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2IWithExtraDOF(	true,
																																																							true,
																																																							2,
																																																							3,
																																																							lMissingInfo,
																																																							null);

		for (final Constrain lConstrain : lConstrainGraph.getConstantConstrains())
			println(lConstrain);

		final StateVector lCurrentStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
																														lConstrainGraph.getNumberOfPlanes(),
																														2,
																														2,
																														1,
																														4);

		final StateVector lNewStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
																												lConstrainGraph.getNumberOfPlanes(),
																												2,
																												2,
																												1,
																												4);

		final ObservationVector lObservationVector = new ObservationVector(	lConstrainGraph.getNumberOfWavelengths(),
																																				lConstrainGraph.getNumberOfPlanes(),
																																				2,
																																				2,
																																				1,
																																				4);

		lNewStateVector.setIlluminationX(0, 0, 0, -333);

		lObservationVector.setObservationZ(0, 0, 0, 0, 1.01);
		lObservationVector.setObservationZ(0, 0, 0, 1, 1.01);
		lObservationVector.setObservationZ(0, 1, 0, 0, 1.02);
		lObservationVector.setObservationZ(0, 1, 0, 1, 1.02);
		lObservationVector.setObservationZ(0, 2, 0, 0, 1.03);
		lObservationVector.setObservationZ(0, 2, 0, 1, 1.03);

		lObservationVector.setObservationZ(1, 0, 0, 0, -1.01);
		lObservationVector.setObservationZ(1, 0, 0, 1, -1.01);
		lObservationVector.setObservationZ(1, 1, 0, 0, -1.02);
		lObservationVector.setObservationZ(1, 1, 0, 1, -1.02);
		lObservationVector.setObservationZ(1, 2, 0, 0, -1.03);
		lObservationVector.setObservationZ(1, 2, 0, 1, -1.03);

		lObservationVector.setObservationIlluminationX(0, 0, 0, 100);
		lObservationVector.setObservationIlluminationX(0, 0, 1, 100);
		lObservationVector.setObservationIlluminationX(0, 1, 0, 150);
		lObservationVector.setObservationIlluminationX(0, 1, 1, 150);
		lObservationVector.setObservationIlluminationX(0, 2, 0, 100);
		lObservationVector.setObservationIlluminationX(0, 2, 1, 100);
		lObservationVector.setObservationIlluminationX(1, 0, 0, 100);
		lObservationVector.setObservationIlluminationX(1, 0, 1, 100);
		lObservationVector.setObservationIlluminationX(1, 1, 0, 150);
		lObservationVector.setObservationIlluminationX(1, 1, 1, 150);
		lObservationVector.setObservationIlluminationX(1, 2, 0, 100);
		lObservationVector.setObservationIlluminationX(1, 2, 1, 100);

		println("lObservationVectorFRESH=\n" + lObservationVector);

		final long lStartTime1 = System.nanoTime();
		cL2FocusMatrixSolver.solve(	lConstrainGraph,
																lCurrentStateVector,
																lObservationVector,
																lNewStateVector);

		final long lStopTime1 = System.nanoTime();

		final long lElapsedTimeNanos1 = lStopTime1 - lStartTime1;
		final double lElapsedTimeMillis1 = (double) lElapsedTimeNanos1 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis1);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(10 + 0),
									0.02);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(10 + 1),
									0.02);

		assertEquals(lNewStateVector.getValueAt(4), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(5), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(14), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(15), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(24), 100, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(25), 100, cEpsilon);

		assertEquals(lNewStateVector.getValueAt(34), 100, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(35), 100, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(44), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(45), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(54), 100, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(55), 100, cEpsilon);

		final long lStartTime2 = System.nanoTime();
		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);
		final long lStopTime2 = System.nanoTime();

		final long lElapsedTimeNanos2 = lStopTime2 - lStartTime2;
		final double lElapsedTimeMillis2 = (double) lElapsedTimeNanos2 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis2);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(10 + 0),
									0.02);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(10 + 1),
									0.02);

		assertEquals(lNewStateVector.getValueAt(4), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(5), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(14), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(15), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(24), 100, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(25), 100, cEpsilon);

		assertEquals(lNewStateVector.getValueAt(34), 100, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(35), 100, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(44), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(45), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(54), 100, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(55), 100, cEpsilon);

		final long lStartTime3 = System.nanoTime();
		cL1FocusMatrixSolver.solve(	lConstrainGraph,
																lCurrentStateVector,
																lObservationVector,
																lNewStateVector);

		final long lStopTime3 = System.nanoTime();

		final long lElapsedTimeNanos3 = lStopTime3 - lStartTime3;
		final double lElapsedTimeMillis3 = (double) lElapsedTimeNanos3 / 1000000;
		println("lElapsedTimeMillis=" + lElapsedTimeMillis3);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(10 + 0),
									0.02);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(10 + 1),
									0.02);

		assertEquals(lNewStateVector.getValueAt(4), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(5), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(14), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(15), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(24), 100, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(25), 100, cEpsilon);

		assertEquals(lNewStateVector.getValueAt(34), 100, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(35), 100, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(44), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(45), 150, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(54), 100, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(55), 100, cEpsilon);

	}

}
