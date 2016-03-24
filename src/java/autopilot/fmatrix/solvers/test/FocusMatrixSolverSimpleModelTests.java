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

public class FocusMatrixSolverSimpleModelTests
{
	private static final double cEpsilon = 0.02;

	private static void println(final Object pObject)
	{
		// System.out.println(pObject.toString());
	}

	final SolverInterface cL2Solver = new L2FocusMatrixSolver();
	{
		cL2Solver.setLogging(false, false);
	}

	final SolverInterface cInequalityConstrainAbleSolver = new QPFocusMatrixSolver();
	{
		cInequalityConstrainAbleSolver.setLogging(false, false);
	}

	final SolverInterface cL1Solver = new L1FocusMatrixSolver();
	{
		cL1Solver.setLogging(false, false);
	}

	@Test
	public void testSimple2D2IModel()
	{

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	false,
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

		lObservationVector.setObservationZ(0, 0, 0, 0, 1);
		lObservationVector.setObservationZ(0, 0, 0, 1, 1);

		cL2Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(0.75, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(0.25, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(-0.25, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(0.25, lNewStateVector.getValueAt(3), cEpsilon);

		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		assertEquals(1, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(3), cEpsilon);

		cL1Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(1, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(3), cEpsilon);
	}

	@Test
	public void testSimple2D2IAssymetricAnchoredModel()
	{

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	true,
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

		lObservationVector.setObservationZ(0, 0, 0, 0, 1);
		lObservationVector.setObservationZ(0, 0, 0, 1, 1);

		cL2Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(0, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(1, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(-1, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(1, lNewStateVector.getValueAt(3), cEpsilon);

		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		assertEquals(0, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(1, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(-1, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(1, lNewStateVector.getValueAt(3), cEpsilon);

		cL1Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(0, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(1, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(-1, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(1, lNewStateVector.getValueAt(3), cEpsilon);
	}

	@Test
	public void testSimple2D2ISymetricAnchoredModel()
	{

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	true,
																																																	true,
																																																	1,
																																																	1,
																																																	null,
																																																	null);

		final StateVector lCurrentStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
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

		lCurrentStateVector.setDetectionZ(0, 0, 0, -1);
		lCurrentStateVector.setDetectionZ(0, 0, 1, -1);

		lObservationVector.setObservationZ(0, 0, 0, 0, 1);
		lObservationVector.setObservationZ(0, 0, 0, 1, 1);

		lObservationVector.setObservationZ(0, 0, 1, 0, 1);
		lObservationVector.setObservationZ(0, 0, 1, 1, 1);

		cL2Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(0, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(3), cEpsilon);

		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		assertEquals(0, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(3), cEpsilon);

		cL1Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(0, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(3), cEpsilon);

		lObservationVector.setObservationZ(0, 0, 0, 0, 1);
		lObservationVector.setObservationZ(0, 0, 1, 0, -1);

		lObservationVector.setObservationZ(0, 0, 0, 1, 0);
		lObservationVector.setObservationZ(0, 0, 1, 1, 0);

		cL2Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(-1, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(-1, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(-1, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(3), cEpsilon);

		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		assertEquals(-1, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(-1, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(-1, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(3), cEpsilon);

		cL1Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(-1, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(-1, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(-1, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(3), cEpsilon);
	}

	@Test
	public void testSimple2D2IThreePlanesModel()
	{

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	false,
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

		lObservationVector.setObservationZ(0, 0, 0, 0, 1.01);
		lObservationVector.setObservationZ(0, 0, 0, 1, 1.01);
		lObservationVector.setObservationZ(0, 1, 0, 0, 1);
		lObservationVector.setObservationZ(0, 1, 0, 1, 1);
		lObservationVector.setObservationZ(0, 2, 0, 0, 0);
		lObservationVector.setObservationZ(0, 2, 0, 1, 0);

		cL2Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(4 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(4 + 1),
									cEpsilon);/**/

		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(4 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(4 + 1),
									cEpsilon);/**/

		cL1Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(4 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(4 + 1),
									cEpsilon);/**/

		println(lNewStateVector.getValueAt(0));
		println(lNewStateVector.getValueAt(4));

	}

	@Test
	public void testSimple2D2ITwoColorsThreePlanesModel()
	{

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	false,
																																																	false,
																																																	2,
																																																	3,
																																																	null,
																																																	null);

		for (final Constrain lConstrain : lConstrainGraph.getConstantConstrains())
		{
			println(lConstrain);
		}

		final StateVector lCurrentStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
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

		lObservationVector.setObservationZ(0, 0, 0, 0, 0.9901);
		lObservationVector.setObservationZ(0, 0, 0, 1, 1.0102);
		lObservationVector.setObservationZ(0, 1, 0, 0, 0.9903);
		lObservationVector.setObservationZ(0, 1, 0, 1, 1.0104);
		lObservationVector.setObservationZ(0, 2, 0, 0, 0.9905);
		lObservationVector.setObservationZ(0, 2, 0, 1, 1.0106);

		lObservationVector.setObservationZ(1, 0, 0, 0, 1.001);
		lObservationVector.setObservationZ(1, 0, 0, 1, 1.002);
		lObservationVector.setObservationZ(1, 1, 0, 0, 1.003);
		lObservationVector.setObservationZ(1, 1, 0, 1, 1.004);
		lObservationVector.setObservationZ(1, 2, 0, 0, 1.005);
		lObservationVector.setObservationZ(1, 2, 0, 1, 1.006);

		cL2Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(12 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(12 + 1),
									cEpsilon);

		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(12 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(12 + 1),
									cEpsilon);

		cL1Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(lNewStateVector.getValueAt(0), 1, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(1), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(2), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(3), 0, cEpsilon);

	}

	@Test
	public void testSimple2D2ITwoColorsThreePlanesMissingInfoModel()
	{

		final boolean[] lMissingObservations = new boolean[]
		{ true,
			true,
			true,
			true,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false };

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	true,
																																																	true,
																																																	2,
																																																	3,
																																																	lMissingObservations,
																																																	null);

		for (final Constrain lConstrain : lConstrainGraph.getConstantConstrains())
		{
			println(lConstrain);
		}

		final StateVector lCurrentStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
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

		lCurrentStateVector.setIlluminationZ(0, 0, 0, 3.333);

		lObservationVector.setObservationZ(0, 0, 0, 0, 0.9901);
		lObservationVector.setObservationZ(0, 0, 0, 1, 1.0102);
		lObservationVector.setObservationZ(0, 1, 0, 0, 0.9903);
		lObservationVector.setObservationZ(0, 1, 0, 1, 1.0104);
		lObservationVector.setObservationZ(0, 2, 0, 0, 0.9905);
		lObservationVector.setObservationZ(0, 2, 0, 1, 1.0106);

		lObservationVector.setObservationZ(1, 0, 0, 0, 1.001);
		lObservationVector.setObservationZ(1, 0, 0, 1, 1.002);
		lObservationVector.setObservationZ(1, 1, 0, 0, 1.003);
		lObservationVector.setObservationZ(1, 1, 0, 1, 1.004);
		lObservationVector.setObservationZ(1, 2, 0, 0, 1.005);
		lObservationVector.setObservationZ(1, 2, 0, 1, 1.006);

		cL2Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(12 + 0),
									0.02);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(12 + 1),
									0.02);

		assertEquals(lNewStateVector.getValueAt(0), 0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(1), 0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(2), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(3), 0.5, cEpsilon);

		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(12 + 0),
									0.02);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(12 + 1),
									0.02);

		assertEquals(lNewStateVector.getValueAt(0), 0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(1), 0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(2), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(3), 0.5, cEpsilon);

	}

	@Test
	public void testSimple2D2IThreePlanesMissingInfoModel()
	{

		final boolean[] lMissingObservations = new boolean[]
		{ true,
			true,
			true,
			true,
			false,
			true,
			false,
			false,
			false,
			false,
			false,
			false };

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	true,
																																																	true,
																																																	1,
																																																	3,
																																																	1,
																																																	lMissingObservations,
																																																	null);

		final StateVector lCurrentStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
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

		lCurrentStateVector.setIlluminationZ(0, 0, 0, 0.678);

		lObservationVector.setObservationZ(0, 0, 0, 0, -1000);
		lObservationVector.setObservationZ(0, 0, 0, 1, -1000);
		lObservationVector.setObservationZ(0, 1, 0, 0, -1);
		lObservationVector.setObservationZ(0, 1, 0, 1, -1);

		cL2Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(lNewStateVector.getValueAt(0), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(1), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(2), 0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(3), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(4), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(5), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(6), 0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(7), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(8), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(9), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(10), 0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(11), -0.5, cEpsilon);

		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		assertEquals(lNewStateVector.getValueAt(0), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(1), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(2), 0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(3), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(4), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(5), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(6), 0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(7), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(8), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(9), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(10), 0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(11), -0.5, cEpsilon);

		cL1Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(lNewStateVector.getValueAt(0), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(1), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(2), 0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(3), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(4), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(5), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(6), 0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(7), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(8), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(9), -0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(10), 0.5, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(11), -0.5, cEpsilon);

	}

	@Test
	public void testSimple2D2IThreePlanesMissingHalfInfoModel()
	{

		final boolean[] lMissingObservations = new boolean[]
		{ false,
			false,
			false,
			false,
			true,
			true,
			true,
			true,
			true,
			true,
			true,
			true };

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	false,
																																																	false,
																																																	1,
																																																	3,
																																																	1,
																																																	lMissingObservations,
																																																	null);

		final StateVector lCurrentStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
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

		lCurrentStateVector.setIlluminationZ(0, 0, 0, 0.678);

		lCurrentStateVector.setIlluminationZ(0, 2, 0, 0.989);

		lObservationVector.setObservationZ(0, 0, 0, 0, -1);
		lObservationVector.setObservationZ(0, 0, 0, 1, -1);

		cL2Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(4 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(4 + 1),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(2),
									lNewStateVector.getValueAt(4 + 2),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(3),
									lNewStateVector.getValueAt(4 + 3),
									cEpsilon);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(8 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(8 + 1),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(2),
									lNewStateVector.getValueAt(8 + 2),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(3),
									lNewStateVector.getValueAt(8 + 3),
									cEpsilon);

		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(4 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(4 + 1),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(2),
									lNewStateVector.getValueAt(4 + 2),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(3),
									lNewStateVector.getValueAt(4 + 3),
									cEpsilon);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(8 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(8 + 1),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(2),
									lNewStateVector.getValueAt(8 + 2),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(3),
									lNewStateVector.getValueAt(8 + 3),
									cEpsilon);

		cL1Solver.solve(lConstrainGraph,
										lCurrentStateVector,
										lObservationVector,
										lNewStateVector);

		assertEquals(lNewStateVector.getValueAt(0), -1, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(1), 0, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(2), 0.6780, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(3), 0, cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(4 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(4 + 1),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(2),
									lNewStateVector.getValueAt(4 + 2),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(3),
									lNewStateVector.getValueAt(4 + 3),
									cEpsilon);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(8 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(8 + 1),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(2),
									lNewStateVector.getValueAt(8 + 2),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(3),
									lNewStateVector.getValueAt(8 + 3),
									cEpsilon);

	}
}
