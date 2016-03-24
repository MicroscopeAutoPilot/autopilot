package autopilot.measures.implementations.differential;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;
import autopilot.measures.implementations.DownScalingFocusMeasure;

/**
 * Teengrad focus measure.
 * 
 * @author royer
 */
public class Tenengrad extends DownScalingFocusMeasure implements
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
	 * Computes the Tenengrad focus measure.
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
	 * Computes the Tenengrad focus measure.
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
		final DoubleArrayImage lDownscaledImage = getDownscaledImage(	pDoubleArrayImage,
																																	pPSFSupportDiameter);

		lDownscaledImage.normalizeNormL1();

		final double[] array = lDownscaledImage.getArray();
		final int length = lDownscaledImage.getLength();
		final int width = lDownscaledImage.getWidth();
		final int height = lDownscaledImage.getHeight();

		double sum = 0;
		for (int y = width * 1; y < (height - 1) * width; y += width)
		{
			for (int x = 1; x < width - 1; x++)
			{
				final int i = x + y;
				final double d1 = 0 + -1
													* array[i - 1 * width - 1]
													- 2
													* array[i - 1 * width]
													- 1
													* array[i - 1 * width + 1]
													+ 0
													* array[i - 1]
													+ 0
													* array[i]
													+ 0
													* array[i + 1]
													+ 1
													* array[i + 1 * width - 1]
													+ 2
													* array[i + 1 * width]
													+ 1
													* array[i + 1 * width + 1];
				final double d2 = 0 + 1
													* array[i - width - 1]
													+ 0
													* array[i - width]
													- 1
													* array[i - width + 1]
													+ 2
													* array[i - 1]
													+ 0
													* array[i]
													- 2
													* array[i + 1]
													+ 1
													* array[i + width - 1]
													+ 0
													* array[i + width]
													- 1
													* array[i + width + 1];

				final double s = d1 * d1 + d2 * d2;
				sum += s;
			}
		}
		sum /= length;
		return sum;
	}
}
