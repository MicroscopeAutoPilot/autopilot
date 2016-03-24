package autopilot.measures.implementations.statistic;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.implementations.DownScalingFocusMeasure;

/**
 * Entropy of image histogram focus measure.
 * 
 * @author royer
 */
public class HistogramEntropy extends DownScalingFocusMeasure	implements
																															FocusMeasureInterface
{
	static ThreadLocal<DoubleArrayImage> sHistogramThreadLocal = new ThreadLocal<DoubleArrayImage>();

	public int mNumberOfBins = 256;

	/**
	 * @see autopilot.measures.FocusMeasureInterface#computeFocusMeasure(autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double computeFocusMeasure(final DoubleArrayImage pDoubleArrayImage)
	{
		return compute(	pDoubleArrayImage,
										mPSFSupportDiameter,
										mNumberOfBins);
	}

	/**
	 * Computes the entropy of image histogram focus measure.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pPSFSupportDiameter
	 *          PSF support diameter
	 * @param pNumberOfBins
	 *          number of bins for histogram
	 * @return focus measure value
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
																			final double pPSFSupportDiameter,
																			final int pNumberOfBins)
	{
		final DoubleArrayImage lDownscaledImage = getDownscaledImage(	pDoubleArrayImage,
																																	pPSFSupportDiameter);

		lDownscaledImage.normalizeNormL1();

		DoubleArrayImage lHistogramImage = sHistogramThreadLocal.get();
		if (lHistogramImage == null || lHistogramImage.getLength() != pNumberOfBins)
		{
			lHistogramImage = new DoubleArrayImage(pNumberOfBins, 1);
			sHistogramThreadLocal.set(lHistogramImage);
		}

		final int length = lDownscaledImage.getLength();
		final double lHistogramUpperBound = 3.0 / length;
		lHistogramImage = lDownscaledImage.histogram(	lHistogramImage,
																									0,
																									lHistogramUpperBound);
		final double[] lHistogramArray = lHistogramImage.getArray();
		final int lHistogramLength = lHistogramImage.getLength();

		final double b = 1.0 / Math.log(2);
		double lEntropy = 0;
		for (int i = 0; i < lHistogramLength; i++)
		{
			final double value = lHistogramArray[i];
			if (value > 0)
			{
				lEntropy += -value * Math.log(value) * b;
			}
		}
		return lEntropy;
	}

}
