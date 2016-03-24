package autopilot.interfaces;

import java.nio.ByteBuffer;
import java.util.Arrays;

import rtlib.core.math.argmax.SmartArgMaxFinder;
import autopilot.fmatrix.constraingraph.templates.ConstrainGraph2D2I;
import autopilot.fmatrix.constraingraph.templates.ConstrainGraphExtraDOF;
import autopilot.fmatrix.solvers.SolverInterface;
import autopilot.fmatrix.solvers.l2.L2FocusMatrixSolver;
import autopilot.fmatrix.solvers.qp.QPFocusMatrixSolver;
import autopilot.fmatrix.vector.ObservationVector;
import autopilot.fmatrix.vector.StateVector;
import autopilot.image.DoubleArrayImage;
import autopilot.measures.dcts.stripes.DCTSStripes;
import autopilot.measures.implementations.differential.Tenengrad;
import autopilot.measures.implementations.resolution.DFTIsotropicResolutionMeasure;
import autopilot.measures.implementations.spectral.NormDCTEntropyShannon;

/**
 * AutoPilotC class
 *
 * This class contains the static methods exposed to the AutoPilot C/C++
 * interface. The methods signatures are adapted to be easy to call from C/C++.
 * 
 * There is basic functionality to retrieve error messages from exceptions.
 * 
 * @author royer
 *
 */
public class AutoPilotC
{

	private static final StackAnalysisAPIState sStackAnalysisAPIState = new StackAnalysisAPIState();

	private static final String cNoError = "No Error";
	private static Throwable sLastException = null;
	private static boolean sStdOut = false;
	private static boolean sLogFile = false;

	/**
	 * Clears the last exception message.
	 */
	private static void clearLastExceptionMessage()
	{
		sLastException = null;
	}

	/**
	 * Returns the last exception message.
	 * 
	 * @return last exception message.
	 */
	public static String getLastExceptionMessage()
	{
		if (sLastException == null)
		{
			return cNoError;
		}
		final String lLastException = sLastException.getMessage();
		return lLastException;
	}

	/**
	 * Sets the logging options.
	 * 
	 * @param pStdOut
	 *          true enables std out
	 * @param pLogFile
	 *          true enables log file
	 */
	public static void setLoggingOptions(	final boolean pStdOut,
																				final boolean pLogFile)
	{
		sStdOut = pStdOut;
		sLogFile = pLogFile;
	}

	/**
	 * Computes the DCTS of a 2D image.
	 * 
	 * According to the AutoPilot paper Benchmark, this is the most accurate focus
	 * measure among all 30 tested.
	 * 
	 * 
	 * @param p16BitImageByteBuffer
	 *          buffer of width*height 16 unsigned integers.
	 * @param pWidth
	 *          width of 2D image.
	 * @param pHeight
	 *          height of 2D image.
	 * @param pPSFSupportDiameter
	 *          estimated diameter of the PSF support. This is used to cut high
	 *          frequency noise components in the image.
	 * 
	 * @return DCTS value
	 */
	public static double dcts16bit(	final ByteBuffer p16BitImageByteBuffer,
																	final int pWidth,
																	final int pHeight,
																	final double pPSFSupportDiameter)
	{
		clearLastExceptionMessage();
		try
		{
			final DoubleArrayImage lDoubleArrayImage = Utils.getThreadLocalDoubleArrayImage(p16BitImageByteBuffer,
																																											pWidth,
																																											pHeight);
			final double dcts = NormDCTEntropyShannon.compute(lDoubleArrayImage,
																												pPSFSupportDiameter);

			return dcts;
		}
		catch (final Throwable e)
		{
			sLastException = e;
			e.printStackTrace();
		}
		return Double.NaN;
	}

	/**
	 * Computes the Tenengrad focus measure from a 2D image.
	 * 
	 * According to the AutoPilot paper Benchmark, this is the second best focus
	 * measure for its trade-off between speed and accuracy.
	 * 
	 * @param p16BitImageByteBuffer
	 *          buffer of width*height 16 unsigned integers.
	 * @param pWidth
	 *          width of 2D image.
	 * @param pHeight
	 *          height of 2D image.
	 * @param pPSFSupportDiameter
	 *          estimated diameter of the PSF support. This is used to cut high
	 *          frequency noise components in the image.
	 * 
	 * @return Tenengrad value.
	 */
	public static double tenengrad16bit(final ByteBuffer p16BitImageByteBuffer,
																			final int pWidth,
																			final int pHeight,
																			final double pPSFSupportDiameter)
	{
		clearLastExceptionMessage();
		try
		{
			final DoubleArrayImage lDoubleArrayImage = Utils.getThreadLocalDoubleArrayImage(p16BitImageByteBuffer,
																																											pWidth,
																																											pHeight);

			final double tenengrad = Tenengrad.compute(	lDoubleArrayImage,
																									pPSFSupportDiameter);

			return tenengrad;
		}
		catch (final Throwable e)
		{
			sLastException = e;
			e.printStackTrace();
		}
		return Double.NaN;
	}

	/**
	 * Computes the ISORES of a 2D image.
	 * 
	 * This measure estimates how isotropic is the image mEstimateResolution. A value of 1
	 * corresponds to a maximally isotropic image and 0 to the least possible
	 * isotropic image.
	 * 
	 * 
	 * @param p16BitImageByteBuffer
	 *          buffer of width*height 16 unsigned integers.
	 * @param pWidth
	 *          width of 2D image.
	 * @param pHeight
	 *          height of 2D image.
	 * @param pPSFSupportDiameter
	 *          estimated diameter of the PSF support. This is used to cut high
	 *          frequency noise components in the image.
	 * 
	 * @return DCTS value
	 */
	public static double isores16bit(	final ByteBuffer p16BitImageByteBuffer,
																		final int pWidth,
																		final int pHeight,
																		final double pPSFSupportDiameter)
	{
		clearLastExceptionMessage();
		try
		{
			final DoubleArrayImage lDoubleArrayImage = Utils.getThreadLocalDoubleArrayImage(p16BitImageByteBuffer,
																																											pWidth,
																																											pHeight);
			final double isores = DFTIsotropicResolutionMeasure.compute(lDoubleArrayImage,
																																	pPSFSupportDiameter);

			return isores;
		}
		catch (final Throwable e)
		{
			sLastException = e;
			e.printStackTrace();
		}
		return Double.NaN;
	}

	/**
	 * Computes the argmax of a 1D discrete curve. This function use the consensus
	 * of a large number of ensemble estimators for finding the argmax, but also
	 * uses empirical statistical testing to determine the significance.
	 * 
	 * @param pX
	 *          array of x values
	 * @param pY
	 *          array of y values (same length as pX)
	 * @param pFittedY
	 *          this array is filled with fitted values. The argmax of the fitted
	 *          curve might differ from the actual returned argmax since not all
	 *          estimators are fit-based.
	 * @param pResult
	 *          array of length 2 containing at position 0: the argmax, and at
	 *          position 1 the significance (1 - p value). The significance can be
	 *          intuitively interpreted as a probability. Formally 1-p is the
	 *          probability that the fit is due to chance alone.
	 * @return 0 if everything went fine, -2 if no argmax could be found, or -1 if
	 *         exception was raised.
	 */
	public static final int argmax(	final double[] pX,
																	final double[] pY,
																	final double[] pFittedY,
																	final double[] pResult)
	{
		clearLastExceptionMessage();
		try
		{
			// System.out.println("pX=" + Arrays.toString(pX));
			// System.out.println("pY=" + Arrays.toString(pY));
			// System.out.println("pFittedY=" + Arrays.toString(pFittedY));
			// System.out.println("pResult=" + Arrays.toString(pResult));

			final SmartArgMaxFinder lSmartArgMaxFinder = new SmartArgMaxFinder();

			final Double lArgmax = lSmartArgMaxFinder.argmax(pX, pY);
			final Double lFitProbability = lSmartArgMaxFinder.getLastFitProbability();

			pResult[0] = lArgmax == null ? Double.NaN : lArgmax;
			pResult[1] = lFitProbability == null ? Double.NaN
																					: lFitProbability;

			Arrays.fill(pFittedY, 0);
			final double[] lFittedY = lSmartArgMaxFinder.fit(pX, pY);
			System.arraycopy(lFittedY, 0, pFittedY, 0, pFittedY.length);

			if (lArgmax == null || Double.isNaN(lArgmax)
					|| lFitProbability == null
					|| Double.isNaN(lFitProbability))
				return -2;

			return 0;
		}
		catch (final Throwable e)
		{
			sLastException = e;
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Sets Parameter.
	 * 
	 * @param pName
	 *          parameter name
	 * @param pValue
	 *          value
	 * @return 0 if everything went fine, -1 if exception was raised.
	 */
	public static int setParameter(final String pName, double pValue)
	{
		clearLastExceptionMessage();
		try
		{
			sStackAnalysisAPIState.setParameter(pName, pValue);
			return 0;
		}
		catch (final Throwable e)
		{
			sLastException = e;
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Initializes a new stack.
	 * 
	 * @param pArray
	 *          array of z values
	 * @return 0 if everything went fine, -1 if exception was raised.
	 */
	public static int newStack(double[] pArray)
	{
		clearLastExceptionMessage();
		try
		{
			sStackAnalysisAPIState.newStack(pArray);
			return 0;
		}
		catch (final Throwable e)
		{
			sLastException = e;
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Loads image plane for stack analysis. This function must be called after a
	 * call to newStack, and cannot be called more times than the number of
	 * declared planes.
	 * 
	 * @param p16BitImageByteBuffer
	 *          Unsigned integer 16bit image buffer
	 * @param pWidth
	 *          image width
	 * @param pHeight
	 *          image height
	 * @return 0 if everything went fine, -1 if exception was raised.
	 */
	public static int loadPlane(final ByteBuffer p16BitImageByteBuffer,
															final int pWidth,
															final int pHeight)
	{
		clearLastExceptionMessage();
		try
		{
			sStackAnalysisAPIState.loadPlane(	p16BitImageByteBuffer,
																				pWidth,
																				pHeight);
			return 0;
		}
		catch (final Throwable e)
		{
			sLastException = e;
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Returns an analysis result. Currently supported: '{dz}' -> focus, '{alpha}'
	 * -> angle alpha, '{beta}' -> angle beta, and '{dz,alpha,beta}' -> dz, alpha,
	 * and beta all in the same array.
	 * 
	 * @param pMaxWaitTimeInSeconds
	 *          maximal time in seconds to wait for calculation completion.
	 * @param pName
	 *          result name
	 * @param pResultArray
	 *          result array.
	 * 
	 * @return 0 if everything went fine, -1 if exception was raised.
	 */
	public static int getResult(double pMaxWaitTimeInSeconds,
															final String pName,
															double[] pResultArray)
	{
		clearLastExceptionMessage();
		try
		{
			sStackAnalysisAPIState.getResult(	pMaxWaitTimeInSeconds,
																				pName,
																				pResultArray);
			return 0;
		}
		catch (final Throwable e)
		{
			sLastException = e;
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Computes the DCTS of a 2D image per vertical or horizontal stripes.
	 * 
	 * @param p16BitImageByteBuffer
	 *          buffer of width*height 16 unsigned integers.
	 * @param pWidth
	 *          image width.
	 * @param pHeight
	 *          image height.
	 * @param pPSFSupportDiameter
	 *          estimated diameter of the PSF support. This is used to cut high
	 *          frequency noise components in the image.
	 * @param pVerticalStripes
	 *          true for vertical stripes, false for horizontal stripes.
	 * @param pStripeWidth
	 *          stripes width
	 * @param pStripeOverlap
	 *          stripes overlap
	 * @param pDCTSArray
	 *          array of DCTS for each stripe.
	 * @return 0 if all went fine -1 if an exception was raised.
	 */
	public static int dcts16bitStripes(	final ByteBuffer p16BitImageByteBuffer,
																			final int pWidth,
																			final int pHeight,
																			final double pPSFSupportDiameter,
																			final boolean pVerticalStripes,
																			final int pStripeWidth,
																			final int pStripeOverlap,
																			final double[] pDCTSArray)
	{
		clearLastExceptionMessage();
		try
		{
			final DoubleArrayImage lDoubleArrayImage = Utils.getThreadLocalDoubleArrayImage(p16BitImageByteBuffer,
																																											pWidth,
																																											pHeight);

			DCTSStripes.dcts16bitStripes(	lDoubleArrayImage,
																		pPSFSupportDiameter,
																		pVerticalStripes,
																		pStripeWidth,
																		pStripeOverlap,
																		pDCTSArray);

			return 0;
		}
		catch (final Throwable e)
		{
			sLastException = e;
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Computes a new microscope state from the current state, observations,
	 * missing info flags, and other parameters describing the microscopes
	 * configuration (such as symmetries, number of colors, planes and DOFs).
	 * 
	 * This function uses a L2 "least-square" solver for finding the new state
	 * vector.
	 * 
	 * @param pAnchorDetection
	 *          true if detection DOFS must be anchored, false otherwise.
	 * @param pSymmetricAnchor
	 *          true if anchoring must be done symmetrically (center of mass is
	 *          anchored), false if only the first detection DOF must be anchored.
	 * @param pNumberOfWavelengths
	 *          number of colors.
	 * @param pNumberOfPlanes
	 *          number of planes.
	 * @param pSyncPlaneIndex
	 *          sync plane index.
	 * @param pCurrentStateVector
	 *          current microscope state vector.
	 * @param pObservationsVector
	 *          observations vector.
	 * @param pMissingObservations
	 *          missing information flag vector.
	 * @param pNewStateVector
	 *          new state vector.
	 * @return 0 if everything went fine, -1 is exception was raised.
	 */
	public static final int l2solve(final boolean pAnchorDetection,
																	final boolean pSymmetricAnchor,
																	final int pNumberOfWavelengths,
																	final int pNumberOfPlanes,
																	final int pSyncPlaneIndex,
																	final double[] pCurrentStateVector,
																	final double[] pObservationsVector,
																	final boolean[] pMissingObservations,
																	final double[] pNewStateVector)
	{
		return l2solve(	pAnchorDetection,
										pSymmetricAnchor,
										pNumberOfWavelengths,
										pNumberOfPlanes,
										ConstrainGraph2D2I.generateSyncPlanesIndicesFromSingleSyncPlane(pNumberOfWavelengths,
																																										pNumberOfPlanes,
																																										pSyncPlaneIndex),
										pCurrentStateVector,
										pObservationsVector,
										pMissingObservations,
										pNewStateVector);

	}

	/**
	 * Computes a new microscope state from the current state, observations,
	 * missing info flags, and other parameters describing the microscopes
	 * configuration (such as symmetries, number of colors, planes and DOFs).
	 * 
	 * This function uses a L2 "least-square" solver for finding the new state
	 * vector.
	 * 
	 * This function allows for advanced configuration of sync planes flags.
	 * 
	 * @param pAnchorDetection
	 *          true if detection DOFS must be anchored, false otherwise.
	 * @param pSymmetricAnchor
	 *          true if anchoring must be done symmetrically (center of mass is
	 *          anchored), false if only the first detection DOF must be anchored.
	 * @param pNumberOfWavelengths
	 *          number of colors.
	 * @param pNumberOfPlanes
	 *          number of planes.
	 * @param pSyncPlanesIndices
	 *          matrix of sync plane indices. Each row corresponds to a color, and
	 *          each column correspond to a plane. The matrix is flattened in
	 *          row-major order.
	 * @param pCurrentStateVector
	 *          current microscope state vector.
	 * @param pObservationsVector
	 *          observations vector.
	 * @param pMissingObservations
	 *          missing information flag vector.
	 * @param pNewStateVector
	 *          new state vector.
	 * @return 0 if everything went fine, -1 is exception was raised.
	 */
	public static final int l2solve(final boolean pAnchorDetection,
																	final boolean pSymmetricAnchor,
																	final int pNumberOfWavelengths,
																	final int pNumberOfPlanes,
																	final boolean[] pSyncPlanesIndices,
																	final double[] pCurrentStateVector,
																	final double[] pObservationsVector,
																	final boolean[] pMissingObservations,
																	final double[] pNewStateVector)
	{
		clearLastExceptionMessage();
		try
		{
			final boolean lAddExtraDOF = true;
			final ConstrainGraph2D2I lConstrainGraph2D2I = new ConstrainGraph2D2I(lAddExtraDOF,
																																						pAnchorDetection,
																																						pSymmetricAnchor,
																																						pNumberOfWavelengths,
																																						pNumberOfPlanes,
																																						pSyncPlanesIndices,
																																						ConstrainGraph2D2I.cEqualityConstrainEqualityWeight,
																																						pMissingObservations,
																																						null);

			final StateVector lCurrentStateVector = new StateVector(lConstrainGraph2D2I.getNumberOfWavelengths(),
																															lConstrainGraph2D2I.getNumberOfPlanes(),
																															2,
																															2,
																															1,
																															lAddExtraDOF ? 4
																																					: 1);

			final StateVector lNewStateVector = new StateVector(lConstrainGraph2D2I.getNumberOfWavelengths(),
																													lConstrainGraph2D2I.getNumberOfPlanes(),
																													2,
																													2,
																													1,
																													lAddExtraDOF ? 4
																																			: 1);

			// System.out.println("pCurrentStateVector arrayLength=" +
			// pCurrentStateVector.length);
			// System.out.println("lCurrentStateVector=" +
			// lCurrentStateVector.getNumElements());

			lCurrentStateVector.getFrom(pCurrentStateVector);

			final ObservationVector lObservationVector = new ObservationVector(	lConstrainGraph2D2I.getNumberOfWavelengths(),
																																					lConstrainGraph2D2I.getNumberOfPlanes(),
																																					2,
																																					2,
																																					1,
																																					lAddExtraDOF ? 4
																																											: 1);
			lObservationVector.getFrom(pObservationsVector);

			final SolverInterface lFocusMatrixSolver = new L2FocusMatrixSolver();

			lFocusMatrixSolver.setLogging(sStdOut, sLogFile);

			lFocusMatrixSolver.solve(	lConstrainGraph2D2I,
																lCurrentStateVector,
																lObservationVector,
																lNewStateVector);

			lNewStateVector.copyTo(pNewStateVector);

			return 0;
		}
		catch (final Throwable e)
		{
			sLastException = e;
			e.printStackTrace();
			return -1;
		}

	}

	/**
	 * Computes a new microscope state from the current state, observations,
	 * missing info flags, and other parameters describing the microscopes
	 * configuration (such as symmetries, number of colors, planes and DOFs).
	 * 
	 * This function uses a QP (Quadratic programming)" solver for finding the new
	 * state vector. This provides a more accurate solution when constraining
	 * corrections.
	 * 
	 * This function allows for advanced configuration of sync planes flags.
	 * 
	 * @param pAnchorDetection
	 *          true if detection DOFS must be anchored, false otherwise.
	 * @param pSymmetricAnchor
	 *          true if anchoring must be done symmetrically (center of mass is
	 *          anchored), false if only the first detection DOF must be anchored.
	 * @param pNumberOfWavelengths
	 *          number of colors.
	 * @param pNumberOfPlanes
	 *          number of planes.
	 * @param pSyncPlanesIndices
	 *          matrix of sync plane indices. Each row corresponds to a color, and
	 *          each column correspond to a plane. The matrix is flattened in
	 *          row-major order.
	 * @param pCurrentStateVector
	 *          current microscope state vector.
	 * @param pObservationsVector
	 *          observations vector.
	 * @param pMissingObservations
	 *          missing information flag vector.
	 * @param pMaximalCorrections
	 *          maximal correction allowed per DOF.
	 * @param pNewStateVector
	 *          new state vector.
	 * @return 0 if everything went fine, -1 is exception was raised.
	 */
	public static final int qpsolve(final boolean pAnchorDetection,
																	final boolean pSymmetricAnchor,
																	final int pNumberOfWavelengths,
																	final int pNumberOfPlanes,
																	final boolean[] pSyncPlanesIndices,
																	final double[] pCurrentStateVector,
																	final double[] pObservationsVector,
																	final boolean[] pMissingObservations,
																	final double[] pMaximalCorrections,
																	final double[] pNewStateVector)
	{
		clearLastExceptionMessage();
		try
		{
			final boolean lAddExtraDOF = true;
			final ConstrainGraph2D2I lConstrainGraph2D2I = new ConstrainGraph2D2I(lAddExtraDOF,
																																						pAnchorDetection,
																																						pSymmetricAnchor,
																																						pNumberOfWavelengths,
																																						pNumberOfPlanes,
																																						pSyncPlanesIndices,
																																						ConstrainGraph2D2I.cEqualityConstrainEqualityWeight,
																																						pMissingObservations,
																																						pMaximalCorrections);

			final StateVector lCurrentStateVector = new StateVector(lConstrainGraph2D2I.getNumberOfWavelengths(),
																															lConstrainGraph2D2I.getNumberOfPlanes(),
																															2,
																															2,
																															1,
																															lAddExtraDOF ? 4
																																					: 1);

			final StateVector lNewStateVector = new StateVector(lConstrainGraph2D2I.getNumberOfWavelengths(),
																													lConstrainGraph2D2I.getNumberOfPlanes(),
																													2,
																													2,
																													1,
																													lAddExtraDOF ? 4
																																			: 1);

			// System.out.println("pCurrentStateVector arrayLength=" +
			// pCurrentStateVector.length);
			// System.out.println("lCurrentStateVector=" +
			// lCurrentStateVector.getNumElements());

			lCurrentStateVector.getFrom(pCurrentStateVector);

			final ObservationVector lObservationVector = new ObservationVector(	lConstrainGraph2D2I.getNumberOfWavelengths(),
																																					lConstrainGraph2D2I.getNumberOfPlanes(),
																																					2,
																																					2,
																																					1,
																																					lAddExtraDOF ? 4
																																											: 1);
			lObservationVector.getFrom(pObservationsVector);
			
			SmoothObservations.betaAngleSmoothing(lObservationVector,pMissingObservations);

			final SolverInterface lFocusMatrixSolver = new QPFocusMatrixSolver();

			lFocusMatrixSolver.setLogging(sStdOut, sLogFile);

			lFocusMatrixSolver.solve(	lConstrainGraph2D2I,
																lCurrentStateVector,
																lObservationVector,
																lNewStateVector);

			lNewStateVector.copyTo(pNewStateVector);

			return lFocusMatrixSolver.getReturnCode();
		}
		catch (final Throwable e)
		{
			sLastException = e;
			e.printStackTrace();
			return -1;
		}

	}

	/**
	 * This function provides standard solving capabilities of extra orthogonal
	 * DOFs. In many cases extra DOFs independent of all other DOFs need to be
	 * considered. This function provides the basic machinery for this.
	 * 
	 * @param pNumberOfWavelengths
	 *          number of colors.
	 * @param pNumberOfPlanes
	 *          number of planes.
	 * @param pNumberOfDOFs
	 *          number of extra DOFs.
	 * @param pCurrentStateVector
	 *          current state vector.
	 * @param pObservationsVector
	 *          observations vector.
	 * @param pMissingObservations
	 *          missing observations vector.
	 * @param pMaximalCorrections
	 *          maximal corrections allowed per DOF.
	 * @param pNewStateVector
	 *          new state vector.
	 * @return 0 if everything went fine, -1 if exception was raised.
	 */
	public static final int extrasolve(	final int pNumberOfWavelengths,
																			final int pNumberOfPlanes,
																			final int pNumberOfDOFs,
																			final double[] pCurrentStateVector,
																			final double[] pObservationsVector,
																			final boolean[] pMissingObservations,
																			final double[] pMaximalCorrections,
																			final double[] pNewStateVector)
	{
		clearLastExceptionMessage();
		try
		{
			final ConstrainGraphExtraDOF lConstrainGraphExtraDOF = new ConstrainGraphExtraDOF(pNumberOfWavelengths,
																																												pNumberOfPlanes,
																																												pNumberOfDOFs,
																																												pMissingObservations,
																																												pMaximalCorrections);

			final StateVector lCurrentStateVector = new StateVector(pNumberOfWavelengths,
																															pNumberOfPlanes,
																															pNumberOfDOFs);

			final StateVector lNewStateVector = new StateVector(pNumberOfWavelengths,
																													pNumberOfPlanes,
																													pNumberOfDOFs);

			lCurrentStateVector.getFrom(pCurrentStateVector);

			final ObservationVector lObservationVector = new ObservationVector(	pNumberOfWavelengths,
																																					pNumberOfPlanes,
																																					pNumberOfDOFs);
			lObservationVector.getFrom(pObservationsVector);

			final SolverInterface lFocusMatrixSolver = new QPFocusMatrixSolver();

			lFocusMatrixSolver.setLogging(sStdOut, sLogFile);

			lFocusMatrixSolver.solve(	lConstrainGraphExtraDOF,
																lCurrentStateVector,
																lObservationVector,
																lNewStateVector);

			lNewStateVector.copyTo(pNewStateVector);

			return 0;
		}
		catch (final Throwable e)
		{
			sLastException = e;
			e.printStackTrace();
			return -1;
		}

	}

}
