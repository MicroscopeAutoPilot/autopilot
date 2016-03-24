package autopilot.image.wavelets;

import autopilot.image.DoubleArrayImage;

public class HaarWavelet
{

	private static boolean sParallel = true;

	public static final boolean transform(final DoubleArrayImage pDoubleImage)
	{
		return transform(pDoubleImage, Integer.MAX_VALUE);
	}

	public static boolean transform(final DoubleArrayImage pDoubleImage,
																	final int pLevel)
	{
		final int lWidth = pDoubleImage.getWidth();
		final int lHeight = pDoubleImage.getHeight();
		// if (!(isPowerofTwo(pDoubleImage) && lWidth == lHeight))
		// return false;

		final double[] lTempArrayX = new double[lWidth];
		final double[] lTempArrayY = new double[lHeight];

		int lScaleX = lWidth;
		int lScaleY = lHeight;

		for (int level = 0; level < pLevel && lScaleX >= 2
												&& lScaleY >= 2; level++)
		{
			transform(pDoubleImage,
								lScaleX,
								lScaleY,
								lTempArrayX,
								lTempArrayY);
			if (lScaleX >= 2)
			{
				lScaleX /= 2;
			}
			if (lScaleY >= 2)
			{
				lScaleY /= 2;
			}
		}

		return true;
	}

	private static final void transform(final DoubleArrayImage pDoubleImage,
																			final int pWidth,
																			final int pHeight,
																			final double[] pTempArrayX,
																			final double[] pTempArrayY)
	{
		transformX(pDoubleImage, pWidth, pHeight, pTempArrayX);
		transformY(pDoubleImage, pWidth, pHeight, pTempArrayY);
	}

	private static final void transformX(	final DoubleArrayImage pDoubleImage,
																				final int pWidth,
																				final int pHeight,
																				final double[] pTempArray)
	{

		for (int y = 0; y < pHeight; y++)
		{
			transformXSingleLine(pDoubleImage, y, pWidth, pTempArray);
		}
	}

	private static final void transformY(	final DoubleArrayImage pDoubleImage,
																				final int pWidth,
																				final int pHeight,
																				final double[] pTempArray)
	{
		for (int x = 0; x < pWidth; x++)
		{
			transformYSingleLine(pDoubleImage, x, pHeight, pTempArray);
		}

	}

	private static final void transformXSingleLine(	final DoubleArrayImage pDoubleImage,
																									final int pY,
																									final int pWidth,
																									final double[] pTempArray)
	{
		final int halflength = pWidth / 2;
		for (int x = 0; x < halflength; x++)
		{
			final double a = pDoubleImage.getIntQuick(2 * x, pY);
			final double b = pDoubleImage.getIntQuick(2 * x + 1, pY);
			pTempArray[x] = 0.5 * (a + b);
			pTempArray[halflength + x] = 0.5 * (a - b);
		}
		for (int x = 0; x < pWidth; x++)
		{
			pDoubleImage.setIntQuick(x, pY, pTempArray[x]);
		}
	}

	private static final void transformYSingleLine(	final DoubleArrayImage pDoubleImage,
																									final int pX,
																									final int pHeight,
																									final double[] pTempArray)
	{
		final int halflength = pHeight / 2;
		for (int y = 0; y < halflength; y++)
		{
			final double a = pDoubleImage.getIntQuick(pX, 2 * y);
			final double b = pDoubleImage.getIntQuick(pX, 2 * y + 1);
			pTempArray[y] = 0.5 * (a + b);
			pTempArray[halflength + y] = 0.5 * (a - b);
		}
		for (int y = 0; y < pHeight; y++)
		{
			pDoubleImage.setIntQuick(pX, y, pTempArray[y]);
		}
	}

	public static final boolean itransform(final DoubleArrayImage pDoubleImage)
	{
		return itransform(pDoubleImage, 2, Integer.MAX_VALUE);
	}

	public static final boolean itransform(	final DoubleArrayImage pDoubleImage,
																					final int pScale,
																					final int pLevel)
	{
		final int lWidth = pDoubleImage.getWidth();
		final int lHeight = pDoubleImage.getHeight();

		int lScale = pScale;

		if (!(isPowerofTwo(pDoubleImage) && isPower2(lScale) && lWidth == lHeight))
		{
			return false;
		}

		final double[] lTempArray = new double[lWidth];

		for (int level = 0; level < pLevel && lScale <= lWidth; level++)
		{
			itransform(pDoubleImage, lScale, lScale, lTempArray);
			lScale *= 2;
		}

		return true;
	}

	private static final void itransform(	final DoubleArrayImage pDoubleImage,
																				final int pWidth,
																				final int pHeight,
																				final double[] pTempArray)
	{
		itransformX(pDoubleImage, pWidth, pHeight, pTempArray);
		itransformY(pDoubleImage, pWidth, pHeight, pTempArray);
	}

	private static final void itransformX(final DoubleArrayImage pDoubleImage,
																				final int pWidth,
																				final int pHeight,
																				final double[] pTempArray)
	{
		for (int y = 0; y < pHeight; y++)
		{
			itransformXSingleLine(pDoubleImage, y, pWidth, pTempArray);
		}
	}

	private static final void itransformY(final DoubleArrayImage pDoubleImage,
																				final int pWidth,
																				final int pHeight,
																				final double[] pTempArray)
	{
		for (int x = 0; x < pWidth; x++)
		{
			itransformYSingleLine(pDoubleImage, x, pHeight, pTempArray);
		}

	}

	private static final void itransformXSingleLine(final DoubleArrayImage pDoubleImage,
																									final int pY,
																									final int pWidth,
																									final double[] pTempArray)
	{
		final int halflength = pWidth / 2;
		for (int x = 0; x < halflength; x++)
		{
			final double a = pDoubleImage.getIntQuick(x, pY);
			final double b = pDoubleImage.getIntQuick(halflength + x, pY);
			pTempArray[2 * x] = a + b;
			pTempArray[2 * x + 1] = a - b;
		}
		for (int x = 0; x < pWidth; x++)
		{
			pDoubleImage.setIntQuick(x, pY, pTempArray[x]);
		}
	}

	private static final void itransformYSingleLine(final DoubleArrayImage pDoubleImage,
																									final int pX,
																									final int pHeight,
																									final double[] pTempArray)
	{
		final int halflength = pHeight / 2;
		for (int y = 0; y < halflength; y++)
		{
			final double a = pDoubleImage.getIntQuick(pX, y);
			final double b = pDoubleImage.getIntQuick(pX, halflength + y);
			pTempArray[2 * y] = a + b;
			pTempArray[2 * y + 1] = a - b;
		}
		for (int y = 0; y < pHeight; y++)
		{
			pDoubleImage.setIntQuick(pX, y, pTempArray[y]);
		}
	}

	private final static boolean isPowerofTwo(final DoubleArrayImage pSourceImage)
	{
		return isPower2(pSourceImage.getWidth()) && isPower2(pSourceImage.getHeight());
	}

	private final static boolean isPower2(final int x)
	{
		return x > 0 && (x & x - 1) == 0;
	}

}
