package autopilot.measures.implementations.statistic;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.implementations.DownScalingFocusMeasure;

/**
 * Kurtosis of difference image focus measure.
 * 
 * @author royer
 */
public class DifferenceKurtosis extends DownScalingFocusMeasure	implements
																																FocusMeasureInterface
{
	public static ThreadLocal<DoubleArrayImage> sDifferenceImageThreadLocal = new ThreadLocal<DoubleArrayImage>();

	/**
	 * @see autopilot.measures.FocusMeasureInterface#computeFocusMeasure(autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double computeFocusMeasure(final DoubleArrayImage pDoubleArrayImage)
	{
		return compute(pDoubleArrayImage, mPSFSupportDiameter);
	}

	/**
	 * Computes the Kurtosis of difference image focus measure..
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

		final double[] marray = lDownscaledImage.getArray();
		final int width = lDownscaledImage.getWidth();
		final int height = lDownscaledImage.getHeight();

		DoubleArrayImage mDifferenceImage = sDifferenceImageThreadLocal.get();
		if (mDifferenceImage == null || mDifferenceImage.getLength() != marray.length)
		{
			mDifferenceImage = new DoubleArrayImage(lDownscaledImage.getWidth(),
																							lDownscaledImage.getHeight());
			sDifferenceImageThreadLocal.set(mDifferenceImage);
		}

		final double[] mdiffarray = mDifferenceImage.getArray();

		for (int y = width; y < (height - 1) * width; y += width)
		{
			for (int x = 1; x < width - 1; x++)
			{
				final int i = x + y;
				mdiffarray[i] = marray[i + 1 + width] - marray[i - 1 - width];
			}
		}

		final double lKurthosis = Math.abs(mDifferenceImage.kurthosis());

		return lKurthosis;
	}

}
