package autopilot.measures.implementations.spectral;

import autopilot.image.DoubleArrayImage;

/**
 * Normalized Discrete Cosine Transform Entropy (Bayesian) to the 4th power
 * focus measure.
 * 
 * @author royer
 */
public class NormDCTEntropyBayesianPower4	extends
																					NormDCTEntropyBayesianGeneralized
{

	/**
	 * @see autopilot.measures.implementations.spectral.NormDCTEntropyBayesianGeneralized#computeFocusMeasure(autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double computeFocusMeasure(final DoubleArrayImage pDoubleArrayImage)
	{
		return compute(pDoubleArrayImage, mPSFSupportDiameter, 4);
	}

	/**
	 * Computes the Normalized Discrete Cosine Transform Entropy (Bayesian) to the
	 * 4th power focus measure.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pPSFSupportDiameter
	 *          PSF support diameter
	 * @return focus measure value
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
																			final double pPSFSupportDiameter)
	{
		return NormDCTEntropyBayesianGeneralized.compute(	pDoubleArrayImage,
																											pPSFSupportDiameter,
																											4);
	}

}
