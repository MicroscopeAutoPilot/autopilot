package autopilot.measures.implementations.spectral;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;

/**
 * Normalized Discrete Cosine Transform Entropy (Shannon) focus measure.
 * 
 * @author royer
 */
public class NormDCTEntropyShannon implements FocusMeasureInterface
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
	 * Computes the Normalized Discrete Cosine Transform Entropy (Shannon) focus
	 * measure.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @return focus measure value
	 */
	public static final double compute(final DoubleArrayImage pDoubleArrayImage)
	{
		return compute(	pDoubleArrayImage,
										FocusMeasures.cPSFSupportDiameter);
	}

	/**
	 * Computes the the Normalized Discrete Cosine Transform Entropy (Shannon)
	 * focus measure.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pPSFSupportDiameter
	 *          PSF support diameter
	 * @return measure
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
																			final double pPSFSupportDiameter)
	{
		pDoubleArrayImage.dctforward();
		pDoubleArrayImage.normalizeNormL2();

		final int lWidth = pDoubleArrayImage.getWidth();
		final int lHeight = pDoubleArrayImage.getHeight();
		final int lOTFSupportX = (int) (lWidth / pPSFSupportDiameter);
		final int lOTFSupportY = (int) (lHeight / pPSFSupportDiameter);

		final double lEntropy = pDoubleArrayImage.entropyShannonSubTriangle(0,
																																				0,
																																				lOTFSupportX,
																																				lOTFSupportY,
																																				true);
		return lEntropy;
	}

}
