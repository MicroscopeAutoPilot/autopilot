package autopilot.measures.implementations.spectral;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;

/**
 * Log-moment Spectral power focus measure.
 * 
 * @author royer
 */
public class LogMomentSpectralPower implements FocusMeasureInterface
{

	public double mPSFSupportDiameter = FocusMeasures.cPSFSupportDiameter;

	/**
	 * @see autopilot.measures.FocusMeasureInterface#computeFocusMeasure(autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double computeFocusMeasure(final DoubleArrayImage pDoubleArrayImage)
	{
		return compute(pDoubleArrayImage, mPSFSupportDiameter);
	}

	/**
	 * Computes the Log-moment Spectral power focus measure.
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
		pDoubleArrayImage.normalizeNormL1();
		pDoubleArrayImage.normalizeDC(1);
		pDoubleArrayImage.fftLogPower();
		final double[] array = pDoubleArrayImage.getArray();
		pDoubleArrayImage.getLength();
		final int width = pDoubleArrayImage.getWidth();
		final int height = pDoubleArrayImage.getHeight();
		final double halfwidth = 0.5 * width;
		final double halfheight = 0.5 * height;

		final int otflimitx = (int) (halfwidth / pPSFSupportDiameter);
		final int otflimity = (int) (halfheight / pPSFSupportDiameter);

		Math.pow(Math.min(halfwidth, halfheight) / pPSFSupportDiameter, 2);

		int count = 0;
		double sum = 0;
		for (int y = 0; y < height; y++)
		{
			final int yi = y * width;
			for (int x = 0; x < width; x++)
			{
				final int i = yi + x;

				final double dx = x - halfwidth;
				final double dy = y - halfheight;
				final double distancesquared = dx * dx + dy * dy;
				// if (distancesquared < lOTFlimitSquared)
				if (Math.abs(dx) < otflimitx && Math.abs(dy) < otflimity)
				{
					final double distance = Math.sqrt(distancesquared);
					final double log = Math.log(1 + distance);
					final double value = log * array[i];
					count++;
					sum += value;
				}
			}
		}
		sum /= count;

		return sum;
	}

}
