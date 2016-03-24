package autopilot.measures.implementations.spectral;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;

/**
 * Normalized Discrete Cosine Transform Entropy (Bayesian) focus measure.
 * 
 * @author royer
 */
public class NormDCTEntropyBayesian implements FocusMeasureInterface
{

	public double mPSFSupportDiameter = FocusMeasures.cPSFSupportDiameter;

	/**
	 * @see autopilot.measures.FocusMeasureInterface#computeFocusMeasure(autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double computeFocusMeasure(final DoubleArrayImage pDoubleArrayImage)
	{
		return compute(pDoubleArrayImage, mPSFSupportDiameter);
	}

	/**
	 * Computes the Normalized Discrete Cosine Transform Entropy (Bayesian) focus
	 * measure.
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
		pDoubleArrayImage.dctforward();

		pDoubleArrayImage.getArray();
		pDoubleArrayImage.getLength();
		final int width = pDoubleArrayImage.getWidth();
		final int height = pDoubleArrayImage.getHeight();

		final int lOTFSupportX = (int) (width / pPSFSupportDiameter);
		final int lOTFSupportY = (int) (height / pPSFSupportDiameter);

		final double lEntropy = pDoubleArrayImage.entropyBayesSubTriangle(0,
																																			0,
																																			lOTFSupportX,
																																			lOTFSupportY,
																																			true);
		return lEntropy;
	}
}
