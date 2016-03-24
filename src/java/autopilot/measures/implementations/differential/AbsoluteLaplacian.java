package autopilot.measures.implementations.differential;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.implementations.DownScalingFocusMeasure;

/**
 * Absolute Laplacian focus measure. Based in part from:
 * "Nayar, S. K. & Nakagawa, Y. Shape from Focus. Ieee T Pattern Anal 16, 824-831, doi:Doi 10.1109/34.308479 (1994)."
 * 
 * @author royer
 */
public class AbsoluteLaplacian extends DownScalingFocusMeasure implements
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
	 * Computes the Absolute Laplacian focus measure
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
		for (int y = width; y < (height - 1) * width; y += width)
		{
			for (int x = 1; x < width - 1; x++)
			{
				final int i = x + y;
				final double d2x = Math.abs(-array[i - 1] + 2
																		* array[i]
																		- array[i + 1]);
				final double d2y = Math.abs(-array[i - width] + 2
																		* array[i]
																		- array[i + width]);

				final double s = d2x + d2y;
				sum += s;
			}
		}
		sum /= length;
		return sum;
	}

}
