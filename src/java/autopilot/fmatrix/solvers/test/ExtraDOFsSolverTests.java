package autopilot.fmatrix.solvers.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import autopilot.fmatrix.constraingraph.templates.ConstrainGraph2D2I;
import autopilot.fmatrix.solvers.SolverInterface;
import autopilot.fmatrix.solvers.qp.QPFocusMatrixSolver;
import autopilot.fmatrix.vector.ObservationVector;
import autopilot.fmatrix.vector.StateVector;

public class ExtraDOFsSolverTests
{
	private static final double cEpsilon = 0.02;

	private static void println(final Object pObject)
	{
		// System.out.println(pObject.toString());
	}

	final SolverInterface cInequalityConstrainAbleSolver = new QPFocusMatrixSolver();
	{
		cInequalityConstrainAbleSolver.setLogging(false, false);
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

		cInequalityConstrainAbleSolver.solve(	lConstrainGraph,
																					lCurrentStateVector,
																					lObservationVector,
																					lNewStateVector);

		assertEquals(1, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(3), cEpsilon);

	}

}
