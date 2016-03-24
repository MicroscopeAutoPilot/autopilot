package autopilot.measures.implementations.spectral;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;

/**
 * Discrete Cosine Transform High to Low frequency ratio focus measure.
 * 
 * @author royer
 */
public class DCTHighLowFreqRatio implements FocusMeasureInterface
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
	 * Computes the Discrete Cosine Transform High to Low frequency ratio focus
	 * measure.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pOTFFilterRatio
	 *          OTF filter ratio
	 * @param pDCFilterRatio
	 *          Direct Component fiter ratio
	 * @param pLowHighFreqRatio
	 *          high/low frequency ratio
	 * @return focus measure value
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
																			final double pOTFFilterRatio,
																			final double pDCFilterRatio,
																			final double pLowHighFreqRatio)
	{
		pDoubleArrayImage.dctforward();
		pDoubleArrayImage.square();

		final double[] array = pDoubleArrayImage.getArray();

		final int width = pDoubleArrayImage.getWidth();
		final int height = pDoubleArrayImage.getHeight();

		final int ex = (int) (pDCFilterRatio * width);
		final int ey = (int) (pDCFilterRatio * height);

		final int abx = (int) (pOTFFilterRatio * pLowHighFreqRatio * width);
		final int aby = (int) (pOTFFilterRatio * pLowHighFreqRatio * height);

		final int otfx = (int) (pOTFFilterRatio * width);
		final int otfy = (int) (pOTFFilterRatio * height);

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
				final double squaredvalue = value * value;
				if (x <= ex && y <= ey)
				{
				}
				else if (x <= abx && y <= aby)
				{
					a += squaredvalue;
					an++;
				}
				else if (x <= otfx && y <= otfy)
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
