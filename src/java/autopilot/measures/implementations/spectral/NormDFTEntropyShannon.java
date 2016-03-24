package autopilot.measures.implementations.spectral;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;

/**
 * Normalized Discrete Fourier Transform Entropy (Shannon) focus measure.
 * 
 * @author royer
 */
public class NormDFTEntropyShannon implements FocusMeasureInterface
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
	 * Computes the Normalized Discrete Fourier Transform Entropy (Shannon) focus
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
		pDoubleArrayImage.fftAbsSum();
		pDoubleArrayImage.normalizeNormL2();

		final int lWidth = pDoubleArrayImage.getWidth();
		final int lHeight = pDoubleArrayImage.getHeight();

		final int lLowFreqWidth = (int) (lWidth / pPSFSupportDiameter);
		final int lLowFreqHeight = (int) (lHeight / pPSFSupportDiameter);
		final double lEntropy = pDoubleArrayImage.entropyShannonSubRectangle(	(lWidth - lLowFreqWidth) / 2,
																																					(lHeight - lLowFreqHeight) / 2,
																																					(lWidth + lLowFreqWidth) / 2,
																																					(lHeight + lLowFreqHeight) / 2,
																																					true);
		return lEntropy;
	}
}
