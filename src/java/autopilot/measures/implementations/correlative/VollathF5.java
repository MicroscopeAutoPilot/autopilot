package autopilot.measures.implementations.correlative;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;
import autopilot.measures.implementations.DownScalingFocusMeasure;

/**
 * Vollath F5 focus measure. Based on: "Vollath, D. The Influence of the Scene
 * Parameters and of Noise on the Behavior of Automatic Focusing Algorithms. J
 * Microsc Oxford 151, 133-146 (1988)".
 * 
 * @author royer
 */
public class VollathF5 extends DownScalingFocusMeasure implements
																											FocusMeasureInterface
{
	/**
	 * @see autopilot.measures.FocusMeasureInterface#computeFocusMeasure(autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double computeFocusMeasure(final DoubleArrayImage pDoubleArrayImage)
	{
		return compute(pDoubleArrayImage, mPSFSupportDiameter);
	}

	/**
	 * Computes the Vollath F4 focus measure.
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
	 * Computes the Vollath F4 focus measure.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pPSFSupportDiameter
	 *          PDF support diameter
	 * @return measure
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
																			final double pPSFSupportDiameter)
	{

		final DoubleArrayImage lDownscaledImage = getDownscaledImage(	pDoubleArrayImage,
																																	pPSFSupportDiameter);

		lDownscaledImage.getWidth();
		lDownscaledImage.getHeight();
		final int length = lDownscaledImage.getLength();
		lDownscaledImage.normalizeNormL1();

		final double a = lDownscaledImage.autocorr(1, 0);
		return a - length * lDownscaledImage.average();
	}

}
