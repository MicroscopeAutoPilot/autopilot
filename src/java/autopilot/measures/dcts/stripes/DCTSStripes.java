package autopilot.measures.dcts.stripes;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.implementations.spectral.NormDCTEntropyShannon;

public class DCTSStripes
{

	public static int nbstripes(final int pWidth,
															final int pHeight,
															final boolean pVerticalStripes,
															final int pStripeWidth,
															final int pStripeOverlap)

	{
		if (pVerticalStripes)
			return DCTSStripes.nbstripes(	pWidth,
																		pStripeWidth,
																		pStripeOverlap);
		else
			return DCTSStripes.nbstripes(	pHeight,
																		pStripeWidth,
																		pStripeOverlap);
	}

	public static int nbstripes(final int pLength,
															final int pStripeWidth,
															final int pStripeOverlap)
	{

		int lNumberOfStripes;
		final int lStripeStride = pStripeWidth - pStripeOverlap;

		lNumberOfStripes = (int) Math.ceil((1.0 * pLength - pStripeOverlap) / lStripeStride);
		for (int i = 0; i < lNumberOfStripes; i++)
		{
			final int lX = i * lStripeStride;

			int lStripeWidth = pStripeWidth;
			if (lStripeWidth + lX >= pLength)
				lStripeWidth = pLength - lX;
			if (lStripeWidth <= pStripeOverlap)
			{
				lNumberOfStripes--;
				continue;
			}
		}

		return lNumberOfStripes;
	}

	public static int dcts16bitStripes(	final DoubleArrayImage pDoubleArrayImage,
																			final double pPSFSupportDiameter,
																			final boolean pVerticalStripes,
																			final int pStripeWidth,
																			final int pStripeOverlap,
																			final double[] pResult)
	{

		for (int i = 0; i < pResult.length; i++)
			pResult[i] = -1;

		final int lWidth = pDoubleArrayImage.getWidth();
		final int lHeight = pDoubleArrayImage.getHeight();

		DoubleArrayImage lStripeImage = null;
		int lNumberOfStripes = 0;

		final int lStripeStride = pStripeWidth - pStripeOverlap;

		if (pVerticalStripes)
		{
			lNumberOfStripes = (int) Math.ceil((1.0 * lWidth - pStripeOverlap) / lStripeStride);
			if (lNumberOfStripes != nbstripes(lWidth,
																				pStripeWidth,
																				pStripeOverlap))
				return -2;
			for (int i = 0; i < lNumberOfStripes; i++)
			{
				final int lX = i * lStripeStride;
				final int lY = 0;

				int lStripeWidth = pStripeWidth;
				if (lStripeWidth + lX >= lWidth)
					lStripeWidth = lWidth - lX;
				if (lStripeWidth <= pStripeOverlap)
				{
					lNumberOfStripes--;
					continue;
				}

				lStripeImage = pDoubleArrayImage.subImage(lX,
																									lY,
																									lStripeWidth,
																									lHeight,
																									lStripeImage);

				final double dcts = NormDCTEntropyShannon.compute(lStripeImage,
																													pPSFSupportDiameter);
				pResult[i] = dcts;
			}
		}
		else
		{
			lNumberOfStripes = (int) Math.ceil((1.0 * lHeight - pStripeOverlap) / lStripeStride);
			if (lNumberOfStripes != nbstripes(lHeight,
																				pStripeWidth,
																				pStripeOverlap))
				return -2;
			for (int i = 0; i < lNumberOfStripes; i++)
			{
				final int lX = 0;
				final int lY = i * lStripeStride;

				int lStripeHeight = pStripeWidth;
				if (lStripeHeight + lY >= lHeight)
					lStripeHeight = lHeight - lY;
				if (lStripeHeight <= pStripeOverlap)
				{
					lNumberOfStripes--;
					break;
				}

				lStripeImage = pDoubleArrayImage.subImage(lX,
																									lY,
																									lWidth,
																									lStripeHeight,
																									lStripeImage);

				final double dcts = NormDCTEntropyShannon.compute(lStripeImage,
																													pPSFSupportDiameter);
				pResult[i] = dcts;
			}
		}

		return lNumberOfStripes;

	}

}
