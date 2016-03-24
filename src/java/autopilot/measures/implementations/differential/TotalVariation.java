package autopilot.measures.implementations.differential;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.implementations.DownScalingFocusMeasure;

/**
 * Total Variation focus measure.
 * 
 * @author royer
 */
public class TotalVariation extends DownScalingFocusMeasure	implements
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
	 * Computes the Total Variation focus measure.
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
				final double dx = array[i + 1] - array[i - 1];
				final double dy = array[i + width] - array[i - width];
				final double dx2 = dx * dx;
				final double dy2 = dy * dy;
				sum += Math.sqrt(dx2 + dy2);
			}
		}
		sum /= length;
		return sum;
	}

}
