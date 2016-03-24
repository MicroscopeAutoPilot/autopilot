package autopilot.measures.implementations.correlative;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.implementations.DownScalingFocusMeasure;

/**
 * Vollath F4 focus measure. Based on: "Vollath, D. The Influence of the Scene
 * Parameters and of Noise on the Behavior of Automatic Focusing Algorithms. J
 * Microsc Oxford 151, 133-146 (1988)".
 * 
 * @author royer
 */
public class VollathF4 extends DownScalingFocusMeasure implements
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
	 * @param pPSFSupportDiameter
	 *          PSF support diameter
	 * @return focus measure
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
																			final double pPSFSupportDiameter)
	{
		final DoubleArrayImage lDownscaledImage = getDownscaledImage(	pDoubleArrayImage,
																																	pPSFSupportDiameter);

		lDownscaledImage.normalizeNormL1();

		final double[] array = lDownscaledImage.getArray();
		final int length = lDownscaledImage.getLength();
		final int width = lDownscaledImage.getWidth();
		final int height = lDownscaledImage.getHeight();

		double accumulator = 0;
		for (int yi = 0; yi < height * width; yi += width)
		{
			for (int x = 0; x < width - 2; x++)
			{
				final int i = yi + x;
				final double value = array[i] * (array[i + 1] - array[i + 2]);
				accumulator += value;
			}
		}
		accumulator /= length;

		return accumulator;
	}

}
