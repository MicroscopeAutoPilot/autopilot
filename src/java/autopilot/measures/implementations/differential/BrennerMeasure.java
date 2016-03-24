package autopilot.measures.implementations.differential;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.implementations.DownScalingFocusMeasure;

/**
 * Brenner's focus measure. Based upon: "Brenner, J. F. et al. An automated
 * microscope for cytologic research a preliminary evaluation. J Histochem
 * Cytochem 24, 100-111 (1976)."
 * 
 * @author royer
 */
public class BrennerMeasure extends DownScalingFocusMeasure	implements
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
	 * Computes Brenner's focus measure
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pPSFSupportDiameter
	 *          PSF support radius
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

		double accumulator = 0;
		for (int yi = 0; yi < height * width; yi += width)
		{
			for (int x = 1; x < width - 1; x++)
			{
				final int i = yi + x;
				final double value = array[i - 1] - array[i + 1];
				accumulator += value * value;
			}
		}
		accumulator /= length;

		return accumulator;
	}

}
