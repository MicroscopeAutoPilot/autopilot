package autopilot.measures.implementations.correlative;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.implementations.DownScalingFocusMeasure;

/**
 * Symmetric version of the Vollath F4 focus measure. Based on: "Vollath, D. The
 * Influence of the Scene Parameters and of Noise on the Behavior of Automatic
 * Focusing Algorithms. J Microsc Oxford 151, 133-146 (1988)".
 * 
 * @author royer
 */
public class SymmetricVollathF4 extends DownScalingFocusMeasure	implements
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
	 * Computes the symmetric Vollath F4 focus measure from an image and PSF
	 * support diameter.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pPSFSupportDiameter
	 *          PSF support diameter.
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

		double rightsum = 0;
		double leftsum = 0;
		double upsum = 0;
		double downsum = 0;
		for (int yi = 2 * width; yi < (height - 2) * width; yi += width)
		{
			for (int x = 2; x < width - 2; x++)
			{
				final int i = yi + x;
				final double right = Math.abs(array[i] * (array[i + 1] - array[i + 2]));
				final double left = array[i] * (array[i - 1] - array[i - 2]);
				final double up = array[i] * (array[i - width] - array[i - 2
																																* width]);
				final double down = array[i] * (array[i + width] - array[i + 2
																																	* width]);
				rightsum += right;
				leftsum += left;
				upsum += up;
				downsum += down;
			}
		}
		final double measure = (Math.abs(rightsum) + Math.abs(leftsum)
														+ Math.abs(upsum) + Math.abs(downsum)) / length;

		return measure;
	}

}
