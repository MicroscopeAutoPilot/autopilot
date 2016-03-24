package autopilot.measures.implementations.statistic;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;
import autopilot.measures.implementations.DownScalingFocusMeasure;

/**
 * Lp Sparsity focus measure.
 * 
 * @author royer
 */
public class LpSparsity extends DownScalingFocusMeasure	implements
																												FocusMeasureInterface
{

	public double mExponent = 2;

	/**
	 * @see autopilot.measures.FocusMeasureInterface#computeFocusMeasure(autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double computeFocusMeasure(final DoubleArrayImage pDoubleArrayImage)
	{
		return compute(pDoubleArrayImage, mPSFSupportDiameter, mExponent);
	}

	/**
	 * Computes the Lp Sparsity focus measure.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pExponent
	 *          exponent p (in Lp)
	 * @return focus measure value
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
																			final double pExponent)
	{
		return compute(	pDoubleArrayImage,
										FocusMeasures.cPSFSupportDiameter,
										pExponent);
	}

	/**
	 * Computes the Lp Sparsity focus measure.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pPSFSupportDiameter
	 *          PSF support diameter
	 * @param pExponent
	 *          exponent p (in Lp)
	 * @return focus measure value
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
																			final double pPSFSupportDiameter,
																			final double pExponent)
	{
		final DoubleArrayImage lDownscaledImage = getDownscaledImage(	pDoubleArrayImage,
																																	pPSFSupportDiameter);
		final double measure = computeWithoutDownscaling(	pExponent,
																											lDownscaledImage);
		return measure;
	}

	public static double computeWithoutDownscaling(	final double pExponent,
																									final DoubleArrayImage lDownscaledImage)
	{
		final int length = lDownscaledImage.getLength();
		final double lp = lDownscaledImage.normLp(pExponent);
		final double lip = lDownscaledImage.normLp(1 / pExponent);
		final double lImageSizeNormalizationCorrectionTerm = Math.pow(length,
																																	pExponent - 1
																																			/ pExponent);
		final double measure = lImageSizeNormalizationCorrectionTerm * (lp / lip);
		return measure;
	}
}
