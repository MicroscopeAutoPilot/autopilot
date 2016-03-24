package autopilot.measures.implementations.spectral;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;

/**
 * Discrete Fourier Transform High to Low frequency ratio focus measure.
 * 
 * @author royer
 */
public class DFTHighLowFreqRatio implements FocusMeasureInterface
{

	public double mOTFFilterRatio = FocusMeasures.cOTFFilterRatio;
	public double mDCFilterRatio = FocusMeasures.cDCFilterRatio;
	public double mLowHighFreqRatio = FocusMeasures.cLowHighFreqRatio;

	/**
	 * @see autopilot.measures.FocusMeasureInterface#computeFocusMeasure(autopilot.image.DoubleArrayImage)
	 */
	@Override
	public double computeFocusMeasure(final DoubleArrayImage pDoubleArrayImage)
	{
		return compute(	pDoubleArrayImage,
										mOTFFilterRatio,
										mDCFilterRatio,
										mLowHighFreqRatio);
	}

	/**
	 * Coputes the Discrete Fourier Transform High to Low frequency ratio focus
	 * measure.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pOTFFilterRatio
	 *          OTF filter ratio
	 * @param pDCFilterRatio
	 *          DC filter ratio
	 * @param pLowHighFreqRatio
	 *          high/low frequency ratio
	 * @return focus measure value
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
																			final double pOTFFilterRatio,
																			final double pDCFilterRatio,
																			final double pLowHighFreqRatio)
	{
		pDoubleArrayImage.fftLogPower();

		final double[] array = pDoubleArrayImage.getArray();

		final int width = pDoubleArrayImage.getWidth();
		final int height = pDoubleArrayImage.getHeight();

		final int centerx = width / 2;
		final int centery = height / 2;

		final int ex = (int) (0.5 * pDCFilterRatio * width);
		final int ey = (int) (0.5 * pDCFilterRatio * height);

		final int abx = (int) (0.5 * pOTFFilterRatio * pLowHighFreqRatio * width);
		final int aby = (int) (0.5 * pOTFFilterRatio * pLowHighFreqRatio * height);

		final int otfx = (int) (0.5 * pOTFFilterRatio * width);
		final int otfy = (int) (0.5 * pOTFFilterRatio * height);

		double a = 0;
		int an = 0;
		double b = 0;
		int bn = 0;
		double c = 0;
		int cn = 0;

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				final int i = x + width * y;
				final double value = array[i];
				final int cx = Math.abs(x - centerx);
				final int cy = Math.abs(y - centery);

				final double squaredvalue = value * value;
				if (cx <= ex && cy <= ey)
				{
				}
				else if (cx <= abx && cy <= aby)
				{
					a += squaredvalue;
					an++;
				}
				else if (cx <= otfx && cy <= otfy)
				{
					b += squaredvalue;
					bn++;
				}
				else
				{
					c += squaredvalue;
					cn++;
				}

			}
		}

		a /= an;
		b /= bn;
		c /= cn;

		final double lFocusMesasure = (b - c) / (a - c);
		return lFocusMesasure;
	}

}
