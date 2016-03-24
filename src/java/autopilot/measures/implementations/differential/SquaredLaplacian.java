package autopilot.measures.implementations.differential;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.implementations.DownScalingFocusMeasure;

/**
 * Squared Laplacian focus measure.
 * 
 * @author royer
 */
public class SquaredLaplacian extends DownScalingFocusMeasure	implements
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
	 * Computes the squared Laplacian-like focus measure
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
				final double c = 8 * array[i];
				final double p = array[i - width - 1] + array[i - width]
													+ array[i - width + 1]
													+ array[i - 1]
													+ array[i + 1]
													+ array[i + width - 1]
													+ array[i + width]
													+ array[i + width + 1];

				final double s = (c - p) * (c - p);
				sum += s;
			}
		}
		sum /= length;
		return sum;
	}

}
