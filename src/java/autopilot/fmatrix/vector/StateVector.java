package autopilot.fmatrix.vector;

/**
 * State vectors describe the ~state~ of the microscope.
 * 
 * @author royer
 *
 */
public class StateVector extends Vector
{

	public StateVector(	final int pNumberOfWavelengths,
											final int pNumberOfPlanes,
											final int pNumberOfCameras,
											final int pNumberOfLightSheets,
											final int pDOFDetection,
											final int pDOFIllumination)
	{
		super(pNumberOfWavelengths,
					pNumberOfPlanes,
					pNumberOfCameras,
					pNumberOfLightSheets,
					pDOFDetection,
					pDOFIllumination);
	}

	public StateVector(	int pNumberOfWavelengths,
											int pNumberOfPlanes,
											int pNumberOfDOFs)
	{
		super(pNumberOfWavelengths, pNumberOfPlanes, pNumberOfDOFs);
	}

	@Override
	protected int computeSingleColorAndPlaneStateVectorLength(final int pNumberOfCameras,
																														final int pNumberOfLightSheets,
																														final int pDOFDetection,
																														final int pDOFIllumination)
	{
		return pDOFDetection * pNumberOfCameras
						+ pDOFIllumination
						* pNumberOfLightSheets;
	}

	private int getWavelengthAndPlaneOffset(final int pWavelengthIndex,
																					final int pPlaneIndex)
	{
		return mSingleColorAndPlaneStateVectorLength * (pWavelengthIndex * mNumberOfPlanes + pPlaneIndex);
	}

	public final void setDetectionZ(final int pWavelengthIndex,
																	final int pPlaneIndex,
																	final int pDetectionIndex,
																	final double pZ)
	{
		setValueAt(	getWavelengthAndPlaneOffset(pWavelengthIndex,
																						pPlaneIndex) + pDetectionIndex,
								pZ);
	}

	public final double getDetectionZ(final int pWavelengthIndex,
																		final int pPlaneIndex,
																		final int pDetectionIndex)
	{
		return getValueAt(getWavelengthAndPlaneOffset(pWavelengthIndex,
																									pPlaneIndex) + pDetectionIndex);
	}

	public final void setIlluminationZ(	final int pWavelengthIndex,
																			final int pPlaneIndex,
																			final int pIndex,
																			final double pZ)
	{
		setValueAt(	getWavelengthAndPlaneOffset(pWavelengthIndex,
																						pPlaneIndex) + mNumberOfCameras
										+ pIndex,
								pZ);
	}

	public final double getIlluminationZ(	final int pWavelengthIndex,
																				final int pPlaneIndex,
																				final int pIndex)
	{
		return getValueAt(getWavelengthAndPlaneOffset(pWavelengthIndex,
																									pPlaneIndex) + mNumberOfCameras
											+ pIndex);
	}

	public final void setIlluminationX(	final int pWavelengthIndex,
																			final int pPlaneIndex,
																			final int pIndex,
																			final double pZ)
	{
		setValueAt(	getWavelengthAndPlaneOffset(pWavelengthIndex,
																						pPlaneIndex) + mNumberOfCameras
										+ mNumberOfLightSheets
										+ pIndex,
								pZ);
	}

	public final double getIlluminationX(	final int pWavelengthIndex,
																				final int pPlaneIndex,
																				final int pIlluminationIndex)
	{
		return getValueAt(getWavelengthAndPlaneOffset(pWavelengthIndex,
																									pPlaneIndex) + mNumberOfCameras
											+ mNumberOfLightSheets
											+ pIlluminationIndex);
	}

	public final void setIlluminationAlpha(	final int pWavelengthIndex,
																					final int pPlaneIndex,
																					final int pIlluminationIndex,
																					final double pZ)
	{
		setValueAt(	getWavelengthAndPlaneOffset(pWavelengthIndex,
																						pPlaneIndex) + mNumberOfCameras
										+ 2
										* mNumberOfLightSheets
										+ pIlluminationIndex,
								pZ);
	}

	public final double getIlluminationAlpha(	final int pWavelengthIndex,
																						final int pPlaneIndex,
																						final int pIlluminationIndex)
	{
		return getValueAt(getWavelengthAndPlaneOffset(pWavelengthIndex,
																									pPlaneIndex) + mNumberOfCameras
											+ 2
											* mNumberOfLightSheets
											+ pIlluminationIndex);
	}

	public final void setIlluminationBeta(final int pWavelengthIndex,
																				final int pPlaneIndex,
																				final int pIlluminationIndex,
																				final double pZ)
	{
		setValueAt(	getWavelengthAndPlaneOffset(pWavelengthIndex,
																						pPlaneIndex) + mNumberOfCameras
										+ 3
										* mNumberOfLightSheets
										+ pIlluminationIndex,
								pZ);
	}

	public final double getIlluminationBeta(final int pWavelengthIndex,
																					final int pPlaneIndex,
																					final int pIlluminationIndex)
	{
		return getValueAt(getWavelengthAndPlaneOffset(pWavelengthIndex,
																									pPlaneIndex) + mNumberOfCameras
											+ 3
											* mNumberOfLightSheets
											+ pIlluminationIndex);
	}

	@Override
	public String toString()
	{
		return String.format(	"StateVector [mNumberOfCameras=%s, mNumberOfLightSheets=%s, getSimpleMatrix()=%s]",
													mNumberOfCameras,
													mNumberOfLightSheets,
													getSimpleMatrix());
	}

}
