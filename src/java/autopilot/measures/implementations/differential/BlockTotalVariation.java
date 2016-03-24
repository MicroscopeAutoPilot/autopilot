package autopilot.measures.implementations.differential;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.implementations.DownScalingFocusMeasure;

/**
 * Block total variation.
 * 
 * @author royer
 */
public class BlockTotalVariation extends DownScalingFocusMeasure implements
																																FocusMeasureInterface
{

	public int mBlockSize = 7;

	/**
	 * @see autopilot.measures.FocusMeasureInterface#computeFocusMeasure(autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double computeFocusMeasure(final DoubleArrayImage pDoubleArrayImage)
	{
		return compute(pDoubleArrayImage, mBlockSize, mPSFSupportDiameter);
	}

	/**
	 * Computes the Block Total Variation focus measure.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pBlockSize
	 *          block size
	 * @param pPSFSupportDiameter
	 *          PSF support diameter
	 * @return focus measure value
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
																			final int pBlockSize,
																			final double pPSFSupportDiameter)
	{
		final DoubleArrayImage lDownscaledImage = getDownscaledImage(	pDoubleArrayImage,
																																	pPSFSupportDiameter);

		lDownscaledImage.normalizeNormL1();

		final double[] array = lDownscaledImage.getArray();
		final int length = lDownscaledImage.getLength();
		final int width = lDownscaledImage.getWidth();
		final int height = lDownscaledImage.getHeight();

		final int lHalfBlockSize = (int) Math.floor(pBlockSize / 2);

		double tv = 0;
		for (int y = lHalfBlockSize; y < height - lHalfBlockSize; y++)
		{
			for (int x = lHalfBlockSize; x < width - lHalfBlockSize; x++)
			{
				tv += blockTotalVariation(array,
																	width,
																	height,
																	x,
																	y,
																	lHalfBlockSize);
			}
		}

		tv /= length;
		return tv;
	}

	/**
	 * Computes the total variation of a single image block.
	 * 
	 * @param pArray
	 * @param pWidth
	 * @param pHeight
	 * @param pX
	 * @param pY
	 * @param pHalfBlockSize
	 * @return
	 */
	private static double blockTotalVariation(final double[] pArray,
																						final int pWidth,
																						final int pHeight,
																						final int pX,
																						final int pY,
																						final int pHalfBlockSize)
	{
		final int i = pX + pWidth * pY;
		final double v = pArray[i];
		double tvp = 0;
		for (int yp = -pHalfBlockSize; yp < pHalfBlockSize; yp++)
		{
			for (int xp = -pHalfBlockSize; xp < pHalfBlockSize; xp++)
			{
				final int ip = pX + xp + pWidth * (pY + yp);
				final double vp = pArray[ip];
				tvp += (v - vp) * (v - vp);
			}
		}
		final double tv = Math.sqrt(tvp);

		return tv;
	}

}
