package autopilot.measures.implementations;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasures;

/**
 * Superclass for focus measures that require the machinery to quickly downscale
 * images before focus measure computation.
 * 
 * @author royer
 */
public class DownScalingFocusMeasure
{

	public double mPSFSupportDiameter = FocusMeasures.cPSFSupportDiameter;
	static ThreadLocal<DoubleArrayImage> mDownScaledImageThreadLocal = new ThreadLocal<DoubleArrayImage>();

	/**
	 * Returns a downscaled image. For example, a PSF diameter of 3 means that the
	 * image is downscaled by a factor 3 (3x3 blocks are added to a single pixel)
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pPSFSupportDiameter
	 *          PSF support diamteter
	 * @return
	 */
	protected static DoubleArrayImage getDownscaledImage(	final DoubleArrayImage pDoubleArrayImage,
																												final double pPSFSupportDiameter)
	{
		final int width = pDoubleArrayImage.getWidth();
		final int height = pDoubleArrayImage.getHeight();

		final int lPSFSupportDiameter = (int) Math.round(pPSFSupportDiameter);

		final int dswidth = width / lPSFSupportDiameter;
		final int dsheight = height / lPSFSupportDiameter;

		DoubleArrayImage lDownscaleImage = mDownScaledImageThreadLocal.get();
		if (lDownscaleImage == null || lDownscaleImage.getWidth() != dswidth
				|| lDownscaleImage.getHeight() != dsheight)
		{
			lDownscaleImage = new DoubleArrayImage(dswidth, dsheight);
			mDownScaledImageThreadLocal.set(lDownscaleImage);
		}

		pDoubleArrayImage.downscale(lDownscaleImage);
		return lDownscaleImage;
	}

	/**
	 * Constructs a downscaling focus measure.
	 */
	public DownScalingFocusMeasure()
	{
		super();
	}

}