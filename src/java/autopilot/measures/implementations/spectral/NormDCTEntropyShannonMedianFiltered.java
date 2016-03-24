package autopilot.measures.implementations.spectral;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;

/**
 * Normalized Discrete Cosine Transform Entropy (Shannon) median filtered focus
 * measure.
 * 
 * @author royer
 */
public class NormDCTEntropyShannonMedianFiltered implements
																								FocusMeasureInterface
{

	public double mPSFSupportDiameter = FocusMeasures.cPSFSupportDiameter;

	public static ThreadLocal<DoubleArrayImage> mMedianFilteredImageThreadLocal = new ThreadLocal<DoubleArrayImage>();

	/**
	 * @see autopilot.measures.FocusMeasureInterface#computeFocusMeasure(autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double computeFocusMeasure(final DoubleArrayImage pDoubleArrayImage)
	{
		return compute(pDoubleArrayImage, mPSFSupportDiameter);
	}

	/**
	 * Computes the Normalized Discrete Cosine Transform Entropy (Shannon) median
	 * filtered focus measure.
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
		pDoubleArrayImage.fastInPlaceMedian();
		pDoubleArrayImage.dctforward();
		pDoubleArrayImage.normalizeNormL2();

		final int lWidth = pDoubleArrayImage.getWidth();
		final int lHeight = pDoubleArrayImage.getHeight();
		final int lLowFreqWidth = (int) (lWidth / pPSFSupportDiameter);
		final int lLowFreqHeight = (int) (lHeight / pPSFSupportDiameter);
		final double lEntropy = pDoubleArrayImage.entropyShannonSubTriangle(0,
																																				0,
																																				lLowFreqWidth,
																																				lLowFreqHeight,
																																				true);
		return lEntropy;
	}
}
