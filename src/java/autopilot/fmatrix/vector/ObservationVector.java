package autopilot.fmatrix.vector;

/**
 * Observation vectors represent observables of the microscope's state, such as
 * for example defocus deltas.
 * 
 * @author royer
 */
public class ObservationVector extends Vector
{

	public ObservationVector(	int pNumberOfWavelengths,
														int pNumberOfPlanes,
														int pNumberOfDOFs)
	{
		super(pNumberOfWavelengths, pNumberOfPlanes, pNumberOfDOFs);
	}

	public ObservationVector(	final int pNumberOfWavelengths,
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

	@Override
	protected int computeSingleColorAndPlaneStateVectorLength(final int pNumberOfCameras,
																														final int pNumberOfLightSheets,
																														final int pDOFDetection,
																														final int pDOFIllumination)
	{
		return pNumberOfCameras * pNumberOfLightSheets
						+ (pDOFIllumination - 1)
						* pNumberOfLightSheets;
	}

	private int getWavelengthAndPlaneOffset(final int pWavelengthIndex,
																					final int pPlaneIndex)
	{
		return mSingleColorAndPlaneStateVectorLength * (pWavelengthIndex * mNumberOfPlanes + pPlaneIndex);
	}

	public final void setObservationZ(final int pWavelengthIndex,
																		final int pPlaneIndex,
																		final int pDetectionIndex,
																		final int pIlluminationIndex,
																		final double pValue)
	{
		setValueAt(	getObservationZIndex(	pWavelengthIndex,
																			pPlaneIndex,
																			pDetectionIndex,
																			pIlluminationIndex),
								pValue);
	}

	public final double getObservationZ(final int pWavelengthIndex,
																			final int pPlaneIndex,
																			final int pDetectionIndex,
																			final int pIlluminationIndex)
	{
		return getValueAt(getObservationZIndex(	pWavelengthIndex,
																						pPlaneIndex,
																						pDetectionIndex,
																						pIlluminationIndex));
	}

	private int getObservationZIndex(	final int pWavelengthIndex,
																		final int pPlaneIndex,
																		final int pDetectionIndex,
																		final int pIlluminationIndex)
	{
		return getWavelengthAndPlaneOffset(pWavelengthIndex, pPlaneIndex) + mNumberOfLightSheets
						* pDetectionIndex
						+ pIlluminationIndex;
	}

	public final void setObservationIlluminationX(final int pWavelengthIndex,
																								final int pPlaneIndex,
																								final int pIlluminationIndex,
																								final double pValue)
	{
		setValueAt(	getObservationIlluminationXIndex(	pWavelengthIndex,
																									pPlaneIndex,
																									pIlluminationIndex),
								pValue);
	}

	public final double getObservationIlluminationX(final int pWavelengthIndex,
																									final int pPlaneIndex,
																									final int pIlluminationIndex)
	{
		return getValueAt(getObservationIlluminationXIndex(	pWavelengthIndex,
																												pPlaneIndex,
																												pIlluminationIndex));
	}

	private int getObservationIlluminationXIndex(	final int pWavelengthIndex,
																								final int pPlaneIndex,
																								final int pIlluminationIndex)
	{
		return getWavelengthAndPlaneOffset(pWavelengthIndex, pPlaneIndex) + mNumberOfCameras
						* mNumberOfLightSheets
						+ pIlluminationIndex;
	}

	public final void setObservationIlluminationAlpha(final int pWavelengthIndex,
																										final int pPlaneIndex,
																										final int pIlluminationIndex,
																										final double pValue)
	{
		setValueAt(	getObservationIlluminationAlphaIndex(	pWavelengthIndex,
																											pPlaneIndex,
																											pIlluminationIndex),
								pValue);
	}

	public final double getObservationIlluminationAlpha(final int pWavelengthIndex,
																											final int pPlaneIndex,
																											final int pIlluminationIndex)
	{
		return getValueAt(getObservationIlluminationAlphaIndex(	pWavelengthIndex,
																														pPlaneIndex,
																														pIlluminationIndex));
	}

	public int getObservationIlluminationAlphaIndex(final int pWavelengthIndex,
																									final int pPlaneIndex,
																									final int pIlluminationIndex)
	{
		return getWavelengthAndPlaneOffset(pWavelengthIndex, pPlaneIndex) + mNumberOfCameras
						* mNumberOfLightSheets
						+ mNumberOfLightSheets
						+ pIlluminationIndex;
	}

	public final void setObservationIlluminationBeta(	final int pWavelengthIndex,
																										final int pPlaneIndex,
																										final int pIlluminationIndex,
																										final double pValue)
	{
		setValueAt(	getObservationIlluminationBetaIndex(pWavelengthIndex,
																										pPlaneIndex,
																										pIlluminationIndex),
								pValue);
	}

	public final double getObservationIlluminationBeta(	final int pWavelengthIndex,
																											final int pPlaneIndex,
																											final int pIlluminationIndex)
	{
		return getValueAt(getObservationIlluminationBetaIndex(pWavelengthIndex,
																													pPlaneIndex,
																													pIlluminationIndex));
	}

	public int getObservationIlluminationBetaIndex(	final int pWavelengthIndex,
																									final int pPlaneIndex,
																									final int pIlluminationIndex)
	{
		return getWavelengthAndPlaneOffset(pWavelengthIndex, pPlaneIndex) + mNumberOfCameras
						* mNumberOfLightSheets
						+ 2
						* mNumberOfLightSheets
						+ pIlluminationIndex;
	}

	@Override
	public String toString()
	{
		return String.format(	"ObservationVector [mNumberOfCameras=%s, mNumberOfLightSheets=%s, getSimpleMatrix()=%s]",
													mNumberOfCameras,
													mNumberOfLightSheets,
													getSimpleMatrix());
	}

}
