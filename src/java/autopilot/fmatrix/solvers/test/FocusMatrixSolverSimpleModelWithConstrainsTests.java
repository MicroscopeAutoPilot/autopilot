package autopilot.fmatrix.solvers.test;

import static java.lang.Math.abs;
import static java.lang.Math.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import autopilot.fmatrix.constraingraph.constrains.Constrain;
import autopilot.fmatrix.constraingraph.templates.ConstrainGraph2D2I;
import autopilot.fmatrix.solvers.SolverInterface;
import autopilot.fmatrix.solvers.qp.QPFocusMatrixSolver;
import autopilot.fmatrix.vector.ObservationVector;
import autopilot.fmatrix.vector.StateVector;

public class FocusMatrixSolverSimpleModelWithConstrainsTests
{
	private static final double cEpsilon = 0.02;

	private static void println(final Object pObject)
	{
		// System.out.println(pObject.toString());
	}

	final SolverInterface cSolver = new QPFocusMatrixSolver();
	{
		cSolver.setLogging(false, false);
	}

	final static private double noise()
	{
		return (random() - 0.5) * cEpsilon;
	}

	@Test
	public void testSimple2D2IModelNoAnchoring()
	{

		final double[] lMaximalCorrections = new double[4];

		lMaximalCorrections[0] = 0.25;
		lMaximalCorrections[1] = 0.5;
		lMaximalCorrections[2] = 1;
		lMaximalCorrections[3] = 1;

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	false,
																																																	false,
																																																	1,
																																																	1,
																																																	null,
																																																	lMaximalCorrections);

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

		lCurrentStateVector.setDetectionZ(0, 0, 0, 0);
		lCurrentStateVector.setDetectionZ(0, 0, 1, 0);

		lObservationVector.setObservationZ(0, 0, 0, 0, 1 + noise());
		lObservationVector.setObservationZ(0, 0, 0, 1, 1 + noise());

		cSolver.solve(lConstrainGraph,
									lCurrentStateVector,
									lObservationVector,
									lNewStateVector);
		assertEquals(0, cSolver.getReturnCode());

		assertEquals(0.250, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(0.5, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(	-lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(2),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(3),
									cEpsilon);

	}

	@Test
	public void testSimple2D2IModelWithSymmetricAnchoring()
	{

		final double[] lMaximalCorrections = new double[4];

		lMaximalCorrections[0] = 0.25;
		lMaximalCorrections[1] = 0.25;
		lMaximalCorrections[2] = 1;
		lMaximalCorrections[3] = 1;

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	true,
																																																	true,
																																																	1,
																																																	1,
																																																	null,
																																																	lMaximalCorrections);

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

		lCurrentStateVector.setDetectionZ(0, 0, 0, 0);
		lCurrentStateVector.setDetectionZ(0, 0, 1, 0);

		lObservationVector.setObservationZ(0, 0, 0, 0, -1 + noise());
		lObservationVector.setObservationZ(0, 0, 0, 1, -1 + noise());

		cSolver.solve(lConstrainGraph,
									lCurrentStateVector,
									lObservationVector,
									lNewStateVector);
		assertEquals(0, cSolver.getReturnCode());

		assertEquals(-0.25, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(-0.25, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(	-lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(2),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(3),
									cEpsilon);

		lObservationVector.setObservationZ(0, 0, 0, 0, 1 + noise());
		lObservationVector.setObservationZ(0, 0, 0, 1, 1 + noise());

		cSolver.solve(lConstrainGraph,
									lCurrentStateVector,
									lObservationVector,
									lNewStateVector);
		assertEquals(0, cSolver.getReturnCode());

		assertEquals(0.250, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(0.25, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(	-lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(2),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(3),
									cEpsilon);

		lObservationVector.setObservationZ(0, 0, 0, 0, 0);
		lObservationVector.setObservationZ(0, 0, 0, 1, 0);
		lObservationVector.setObservationZ(0, 0, 0, 0, 1 + noise());
		lObservationVector.setObservationZ(0, 0, 1, 0, -1 + noise());

		cSolver.solve(lConstrainGraph,
									lCurrentStateVector,
									lObservationVector,
									lNewStateVector);
		assertEquals(0, cSolver.getReturnCode());

		assertEquals(0, lNewStateVector.getValueAt(0), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(1), cEpsilon);
		assertEquals(-1, lNewStateVector.getValueAt(2), cEpsilon);
		assertEquals(0, lNewStateVector.getValueAt(3), cEpsilon);

	}

	@Test
	public void testSimple2D2IThreePlanesModel()
	{

		final double[] lMaximalCorrections = new double[12];

		final double dlimit = 0.25;
		final double ilimit = 0.5;
		lMaximalCorrections[0] = dlimit;
		lMaximalCorrections[1] = dlimit;
		lMaximalCorrections[2] = ilimit;
		lMaximalCorrections[3] = ilimit;

		lMaximalCorrections[4] = dlimit;
		lMaximalCorrections[5] = dlimit;
		lMaximalCorrections[6] = ilimit;
		lMaximalCorrections[7] = ilimit;

		lMaximalCorrections[8] = dlimit;
		lMaximalCorrections[9] = dlimit;
		lMaximalCorrections[10] = ilimit;
		lMaximalCorrections[11] = ilimit;

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	false,
																																																	false,
																																																	1,
																																																	3,
																																																	null,
																																																	lMaximalCorrections);

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

		lObservationVector.setObservationZ(0, 0, 0, 0, 1 + noise());
		lObservationVector.setObservationZ(0, 0, 0, 1, 1 + noise());
		lObservationVector.setObservationZ(0, 1, 0, 0, 1 + noise());
		lObservationVector.setObservationZ(0, 1, 0, 1, 1 + noise());
		lObservationVector.setObservationZ(0, 1, 1, 0, 0);
		lObservationVector.setObservationZ(0, 1, 1, 1, 0);
		lObservationVector.setObservationZ(0, 2, 0, 0, 0);
		lObservationVector.setObservationZ(0, 2, 0, 1, 0);

		cSolver.solve(lConstrainGraph,
									lCurrentStateVector,
									lObservationVector,
									lNewStateVector);
		assertEquals(0, cSolver.getReturnCode());

		assertTrue(abs(lNewStateVector.getValueAt(4)) <= 0.252);
		assertTrue(abs(lNewStateVector.getValueAt(5)) <= 0.252);

		assertEquals(	lNewStateVector.getValueAt(4),
									lNewStateVector.getValueAt(5),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(4),
									-lNewStateVector.getValueAt(6),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(4),
									lNewStateVector.getValueAt(7),
									cEpsilon);

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(4 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(4 + 1),
									cEpsilon);/**/

	}

	@Test
	public void testSimple2D2ITwoColorsThreePlanesModel()
	{
		final double[] lMaximalCorrections = new double[24];

		final double dlimit = 0.25;
		final double ilimit = 0.5;
		for (int i = 0; i < 6; i++)
		{
			lMaximalCorrections[4 * i + 0] = dlimit;
			lMaximalCorrections[4 * i + 1] = dlimit;
			lMaximalCorrections[4 * i + 2] = ilimit;
			lMaximalCorrections[4 * i + 3] = ilimit;
		}

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	false,
																																																	false,
																																																	2,
																																																	3,
																																																	null,
																																																	lMaximalCorrections);

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

		lObservationVector.setObservationZ(0, 0, 0, 0, 1 + noise());
		lObservationVector.setObservationZ(0, 0, 0, 1, 1 + noise());
		lObservationVector.setObservationZ(0, 1, 0, 0, 1 + noise());
		lObservationVector.setObservationZ(0, 1, 0, 1, 1 + noise());
		lObservationVector.setObservationZ(0, 2, 0, 0, 1 + noise());
		lObservationVector.setObservationZ(0, 2, 0, 1, 1 + noise());

		lObservationVector.setObservationZ(1, 0, 0, 0, 1 + noise());
		lObservationVector.setObservationZ(1, 0, 0, 1, 1 + noise());
		lObservationVector.setObservationZ(1, 1, 0, 0, 1 + noise());
		lObservationVector.setObservationZ(1, 1, 0, 1, 1 + noise());
		lObservationVector.setObservationZ(1, 2, 0, 0, 1 + noise());
		lObservationVector.setObservationZ(1, 2, 0, 1, 1 + noise());

		println(lObservationVector);

		cSolver.solve(lConstrainGraph,
									lCurrentStateVector,
									lObservationVector,
									lNewStateVector);
		assertEquals(0, cSolver.getReturnCode());

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(4),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(2 * 4),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(3 * 4),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(4 * 4),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(5 * 4),
									cEpsilon);

		assertEquals(	lNewStateVector.getValueAt(5),
									-lNewStateVector.getValueAt(6),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(5),
									lNewStateVector.getValueAt(7),
									cEpsilon);

		assertEquals(0.25, lNewStateVector.getValueAt(0), cEpsilon);

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

		final double[] lMaximalCorrections = new double[12];

		final double dlimit = 0.25;
		final double ilimit = 0.5;
		lMaximalCorrections[0] = dlimit;
		lMaximalCorrections[1] = dlimit;
		lMaximalCorrections[2] = ilimit;
		lMaximalCorrections[3] = ilimit;

		lMaximalCorrections[4] = dlimit;
		lMaximalCorrections[5] = dlimit;
		lMaximalCorrections[6] = ilimit;
		lMaximalCorrections[7] = ilimit;

		lMaximalCorrections[8] = dlimit;
		lMaximalCorrections[9] = dlimit;
		lMaximalCorrections[10] = ilimit;
		lMaximalCorrections[11] = ilimit;

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	true,
																																																	true,
																																																	1,
																																																	3,
																																																	1,
																																																	lMissingObservations,
																																																	lMaximalCorrections);

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

		lObservationVector.setObservationZ(0, 0, 0, 0, +100 + noise());
		lObservationVector.setObservationZ(0, 0, 0, 1, -100 + noise());
		lObservationVector.setObservationZ(0, 1, 0, 0, -1 + noise());
		lObservationVector.setObservationZ(0, 1, 0, 1, -1 + noise());

		cSolver.solve(lConstrainGraph,
									lCurrentStateVector,
									lObservationVector,
									lNewStateVector);
		assertEquals(0, cSolver.getReturnCode());

		assertEquals(lNewStateVector.getValueAt(0), -0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(1), -0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(2), 0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(3), -0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(4), -0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(5), -0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(6), 0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(7), -0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(8), -0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(9), -0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(10), 0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(11), -0.25, cEpsilon);

	}

	@Test
	public void testSimple2D2IThreePlanesMissingHalfInfoModel()
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
			true,
			true,
			true,
			true };

		final double[] lMaximalCorrections = new double[12];

		final double dlimit = 0.25;// 0.25;
		final double ilimit = 0.5;// 0.5;
		lMaximalCorrections[0] = dlimit;
		lMaximalCorrections[1] = dlimit;
		lMaximalCorrections[2] = ilimit;
		lMaximalCorrections[3] = ilimit;

		lMaximalCorrections[4] = dlimit;
		lMaximalCorrections[5] = dlimit;
		lMaximalCorrections[6] = ilimit;
		lMaximalCorrections[7] = ilimit;

		lMaximalCorrections[8] = dlimit;
		lMaximalCorrections[9] = dlimit;
		lMaximalCorrections[10] = ilimit;
		lMaximalCorrections[11] = ilimit;

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	true,
																																																	true,
																																																	1,
																																																	3,
																																																	1,
																																																	lMissingObservations,
																																																	lMaximalCorrections);

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

		lCurrentStateVector.setIlluminationZ(0, 0, 0, 0);
		lCurrentStateVector.setIlluminationZ(0, 2, 0, 0);

		lObservationVector.setObservationZ(0, 0, 0, 0, -1 + noise());
		lObservationVector.setObservationZ(0, 0, 0, 1, -1 + noise());
		lObservationVector.setObservationZ(0, 1, 0, 0, -1 + noise());
		lObservationVector.setObservationZ(0, 1, 0, 1, -1 + noise());

		cSolver.solve(lConstrainGraph,
									lCurrentStateVector,
									lObservationVector,
									lNewStateVector);
		assertEquals(0, cSolver.getReturnCode());

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
		assertEquals(0.25, lNewStateVector.getValueAt(8 + 2), cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(3),
									lNewStateVector.getValueAt(8 + 3),
									cEpsilon);

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
			true,
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
			false };

		final double[] lMaximalCorrections = new double[lMissingObservations.length];

		final double dlimit = 0.25;
		final double ilimit = 0.5;
		for (int i = 0; i < lMaximalCorrections.length / 4; i++)
		{
			lMaximalCorrections[4 * i + 0] = dlimit;
			lMaximalCorrections[4 * i + 1] = dlimit;
			lMaximalCorrections[4 * i + 2] = ilimit;
			lMaximalCorrections[4 * i + 3] = ilimit;
		}

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2I(	true,
																																																	true,
																																																	2,
																																																	3,
																																																	lMissingObservations,
																																																	lMaximalCorrections);

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

		lCurrentStateVector.setIlluminationZ(0, 0, 0, 0);

		final double a = 1 + noise();
		final double b = 1 + noise();
		final double c = 1 + noise();
		final double d = 1 + noise();
		final double e = 1 + noise();
		final double f = 1 + noise();

		lObservationVector.setObservationZ(0, 0, 0, 0, a);
		lObservationVector.setObservationZ(0, 0, 0, 1, b);
		lObservationVector.setObservationZ(0, 1, 0, 0, c);
		lObservationVector.setObservationZ(0, 1, 0, 1, d);
		lObservationVector.setObservationZ(0, 2, 0, 0, e);
		lObservationVector.setObservationZ(0, 2, 0, 1, f);

		lObservationVector.setObservationZ(1, 0, 0, 0, a);
		lObservationVector.setObservationZ(1, 0, 0, 1, b);
		lObservationVector.setObservationZ(1, 1, 0, 0, c);
		lObservationVector.setObservationZ(1, 1, 0, 1, d);
		lObservationVector.setObservationZ(1, 2, 0, 0, e);
		lObservationVector.setObservationZ(1, 2, 0, 1, f);

		cSolver.solve(lConstrainGraph,
									lCurrentStateVector,
									lObservationVector,
									lNewStateVector);
		assertEquals(0, cSolver.getReturnCode());

		assertEquals(	lNewStateVector.getValueAt(0),
									lNewStateVector.getValueAt(12 + 0),
									cEpsilon);
		assertEquals(	lNewStateVector.getValueAt(1),
									lNewStateVector.getValueAt(12 + 1),
									cEpsilon);

		assertEquals(lNewStateVector.getValueAt(0), 0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(1), 0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(2), -0.25, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(3), 0.25, cEpsilon);

	}

	@Test
	public void testFull2D2ITwoColorThreePlanesSymmetricAnchorMissingInfoModel()
	{

		final boolean[] lMissingInfo = new boolean[2 * 3 * 10];

		lMissingInfo[4] = true;
		lMissingInfo[5] = true;
		// lMissingInfo[10 + 4] = true;
		// lMissingInfo[10 + 5] = true;

		final double[] lMaximalCorrections = new double[lMissingInfo.length];

		final double dlimit = 0.25;
		final double ilimit = 0.5;
		final double ylimit = 10;
		final double alimit = 0.3;
		final double blimit = 0.3;
		for (int i = 0; i < lMaximalCorrections.length / 10; i++)
		{
			lMaximalCorrections[10 * i + 0] = dlimit;
			lMaximalCorrections[10 * i + 1] = dlimit;
			lMaximalCorrections[10 * i + 2] = ilimit;
			lMaximalCorrections[10 * i + 3] = ilimit;
			lMaximalCorrections[10 * i + 4] = ylimit;
			lMaximalCorrections[10 * i + 5] = ylimit;
			lMaximalCorrections[10 * i + 6] = alimit;
			lMaximalCorrections[10 * i + 7] = alimit;
			lMaximalCorrections[10 * i + 8] = blimit;
			lMaximalCorrections[10 * i + 9] = blimit;
		}

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2IWithExtraDOF(	true,
																																																							true,
																																																							2,
																																																							3,
																																																							lMissingInfo,
																																																							lMaximalCorrections);

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

		final long lStartTime3 = System.nanoTime();
		cSolver.solve(lConstrainGraph,
									lCurrentStateVector,
									lObservationVector,
									lNewStateVector);
		assertEquals(0, cSolver.getReturnCode());

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

		assertEquals(lNewStateVector.getValueAt(4), 10, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(5), 10, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(14), 10, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(15), 10, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(24), 10, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(25), 10, cEpsilon);

		assertEquals(lNewStateVector.getValueAt(34), 10, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(35), 10, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(44), 10, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(45), 10, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(54), 10, cEpsilon);
		assertEquals(lNewStateVector.getValueAt(55), 10, cEpsilon);

	}

	@Test
	public void testSimple2D2IThreePlanesFullModelFromMikeColeman()
	{

		final boolean[] lMissingObservations = new boolean[30];
		lMissingObservations[0] = true;
		lMissingObservations[22] = true;

		final double[] lMaximalCorrections = new double[30];

		final double dlimit = 0.25;
		final double ilimit = 0.5;
		final double ylimit = 1;
		final double alimit = 0.3;
		final double blimit = 0.3;
		for (int i = 0; i < lMaximalCorrections.length / 10; i++)
		{
			lMaximalCorrections[10 * i + 0] = dlimit;
			lMaximalCorrections[10 * i + 1] = dlimit;
			lMaximalCorrections[10 * i + 2] = ilimit;
			lMaximalCorrections[10 * i + 3] = ilimit;
			lMaximalCorrections[10 * i + 4] = ylimit;
			lMaximalCorrections[10 * i + 5] = ylimit;
			lMaximalCorrections[10 * i + 6] = alimit;
			lMaximalCorrections[10 * i + 7] = alimit;
			lMaximalCorrections[10 * i + 8] = blimit;
			lMaximalCorrections[10 * i + 9] = blimit;
		}

		final ConstrainGraph2D2I lConstrainGraph = FocusMatrixSolverTestsUtils.getConstrainGraph2D2IWithExtraDOF(	false,
																																																							false,
																																																							1,
																																																							3,
																																																							lMissingObservations,
																																																							lMaximalCorrections);

		final StateVector lCurrentStateVector = new StateVector(lConstrainGraph.getNumberOfWavelengths(),
																														lConstrainGraph.getNumberOfPlanes(),
																														2,
																														2,
																														1,
																														4);
		final double[] lCurrentState = new double[]
		{ 8.54,
			0.91,
			-31.52,
			-33.90,
			129.71,
			70.71,
			0.00,
			0.00,
			0.00,
			0.00,
			8.54,
			0.91,
			48.52,
			31.76,
			0.00,
			0.00,
			0.00,
			0.00,
			0.00,
			0.00,
			8.54,
			0.91,
			-20.11,
			-2.17,
			0.00,
			0.00,
			0.00,
			0.00,
			0.00,
			0.00 };
		lCurrentStateVector.getFrom(lCurrentState);

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
		final double[] lObservations = new double[]
		{ 0.00,
			1.96,
			-4.00,
			4.00,
			0.00,
			0.00,
			0.00,
			0.00,
			0.00,
			0.00,
			-4.00,
			4.00,
			-4.00,
			4.00,
			0.00,
			0.00,
			0.00,
			0.00,
			0.00,
			0.00,
			4.00,
			-0.20,
			0.00,
			2.67,
			0.00,
			0.00,
			0.00,
			0.00,
			0.00,
			0.00 };
		lObservationVector.getFrom(lObservations);

		cSolver.solve(lConstrainGraph,
									lCurrentStateVector,
									lObservationVector,
									lNewStateVector);
		assertEquals(0, cSolver.getReturnCode());

		println(lNewStateVector);
	}

}
