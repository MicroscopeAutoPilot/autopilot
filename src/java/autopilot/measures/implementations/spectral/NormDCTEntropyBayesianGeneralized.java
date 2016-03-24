package autopilot.measures.implementations.spectral;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;

/**
 * Normalized Discrete Focus Measure Entropy (Bayesian) generalized focus
 * measure.
 * 
 * @author royer
 */
public class NormDCTEntropyBayesianGeneralized implements
																							FocusMeasureInterface
{

	public double mPSFSupportDiameter = FocusMeasures.cPSFSupportDiameter;
	public int mExponent = 4;

	/**
	 * @see autopilot.measures.FocusMeasureInterface#computeFocusMeasure(autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double computeFocusMeasure(final DoubleArrayImage pDoubleArrayImage)
	{
		return compute(pDoubleArrayImage, mPSFSupportDiameter, mExponent);
	}

	/**
	 * Computes the Normalized Discrete Focus Measure Entropy (Bayesian)
	 * generalized focus measure.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pPSFSupportDiameter
	 *          PSF support diameter
	 * @param pExponent
	 *          exponent
	 * @return focus value
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
																			final double pPSFSupportDiameter,
																			final int pExponent)
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
																																			true,
																																			pExponent);

		return lEntropy;
	}

}
