package autopilot.fmatrix.solvers.test;

import autopilot.fmatrix.constraingraph.templates.ConstrainGraph2D2I;

public class FocusMatrixSolverTestsUtils
{

	public static final ConstrainGraph2D2I getConstrainGraph2D2I(	final boolean pAnchorDetection,
																																final boolean pSymmetricAnchor,
																																final int pNumberOfWavelengths,
																																final int pNumberOfPlanes,
																																final boolean[] pMissingObservations,
																																final double[] pMaximalCorrections)
	{
		return getConstrainGraph2D2I(	pAnchorDetection,
																	pSymmetricAnchor,
																	pNumberOfWavelengths,
																	pNumberOfPlanes,
																	pNumberOfPlanes / 2,
																	pMissingObservations,
																	pMaximalCorrections);
	}

	public static final ConstrainGraph2D2I getConstrainGraph2D2I(	final boolean pAnchorDetection,
																																final boolean pSymmetricAnchor,
																																final int pNumberOfWavelengths,
																																final int pNumberOfPlanes,
																																final int pSyncPlaneIndex,
																																final boolean[] pMissingObservations,
																																final double[] pMaximalCorrections)
	{
		final ConstrainGraph2D2I lConstrainGraph2D2I = new ConstrainGraph2D2I(false,
																																					pAnchorDetection,
																																					pSymmetricAnchor,
																																					pNumberOfWavelengths,
																																					pNumberOfPlanes,
																																					pNumberOfPlanes / 2,
																																					ConstrainGraph2D2I.cEqualityConstrainEqualityWeight,
																																					pMissingObservations,
																																					pMaximalCorrections);

		return lConstrainGraph2D2I;
	}

	public static final ConstrainGraph2D2I getConstrainGraph2D2IWithExtraDOF(	final boolean pAnchorDetection,
																																						final boolean pSymmetricAnchor,
																																						final int pNumberOfWavelengths,
																																						final int pNumberOfPlanes,
																																						final boolean[] pMissingObservations,
																																						final double[] pMaximalCorrections)
	{
		final ConstrainGraph2D2I lConstrainGraph2D2I = new ConstrainGraph2D2I(true,
																																					pAnchorDetection,
																																					pSymmetricAnchor,
																																					pNumberOfWavelengths,
																																					pNumberOfPlanes,
																																					pNumberOfPlanes / 2,
																																					ConstrainGraph2D2I.cEqualityConstrainEqualityWeight,
																																					pMissingObservations,
																																					pMaximalCorrections);

		return lConstrainGraph2D2I;
	}
}
