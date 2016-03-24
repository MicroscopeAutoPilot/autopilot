package autopilot.measures.implementations.resolution;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.round;
import gnu.trove.list.array.TDoubleArrayList;

import java.io.File;
import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.formats.FormatException;

import org.apache.commons.math3.stat.StatUtils;

import autopilot.image.DoubleArrayImage;
import autopilot.image.writers.TiffWriter;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;

/**
 * Estimates the effective mEstimateResolution of the image.
 * 
 * @author royer
 */
public class DFTResolutionMeasure implements FocusMeasureInterface
{
	private static int sDebugImageCounter = 0;
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
	 * Computes the Discrete Fourier Transform High to Low frequency ratio focus
	 * measure.
	 * 
	 * @param pDoubleArrayImage
	 *            image
	 * @param pPSFSupportDiameter
	 *            PSF support diameter
	 * @return focus measure value
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
										final double pPSFSupportDiameter)
	{
		return compute(	pDoubleArrayImage,
						pPSFSupportDiameter,
						FocusMeasures.cNumberOfAngleBins);
	}

	/**
	 * Computes the Discrete Fourier Transform High to Low frequency ratio focus
	 * measure.
	 * 
	 * @param pDoubleArrayImage
	 *            image
	 * @param pPSFSupportDiameter
	 *            PSF support diameter
	 * 
	 * @param pNumberOfBins
	 *            Number of bins for angles
	 * @return focus measure value
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
										final double pPSFSupportDiameter,
										int pNumberOfBins)
	{
		// pDoubleArrayImage.normalizeNormL2();
		pDoubleArrayImage.fftLogPower();

		final double[] array = pDoubleArrayImage.getArray();

		final int length = pDoubleArrayImage.getLength();
		final int width = pDoubleArrayImage.getWidth();
		final int height = pDoubleArrayImage.getHeight();

		final int centerx = width / 2;
		final int centery = height / 2;

		final double[] lBaseAndMaxLevels = computeBaseLevel(pDoubleArrayImage,
															pPSFSupportDiameter);
		final double lBaseLevel = lBaseAndMaxLevels[0];
		final double lMaxLevel = lBaseAndMaxLevels[1];

		// System.out.println("lThreshold=" + lThreshold);

		/*debugtiff(pDoubleArrayImage,
							"/Users/royer/Desktop/beforediff.tif");/**/

		for (int i = 0; i < length; i++)
		{
			final double lDiff = array[i] - lBaseLevel;
			final double lPosDiff = lDiff < 0 ? 0 : lDiff;
			array[i] = lPosDiff;
		}

		pDoubleArrayImage.normalizeNormL2();

		/*debugtiff(pDoubleArrayImage,
							"/Users/royer/Desktop/afterdiff" + (sDebugImageCounter++)
									+ ".tif");/**/

		final double lMaxDistance = max(width / 2, height / 2);

		// pNumberOfBins = (int) lMaxDistance;

		return computeSupport(pDoubleArrayImage, pNumberOfBins, 0.01);

		/*final double[] lRadiusBins = new double[pNumberOfBins];
		final double[] lRadiusBinsCount = new double[pNumberOfBins];

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				final int i = x + width * y;
				final double value = array[i];
				final int cx = x - centerx;
				final int cy = y - centery;

				if (value > 0)
				{
					final double normdistance = sqrt(hypot(	2.0 * cx / width,
																									2.0 * cy / height)) / sqrt(2);
					final int lIndex = (int) round(normdistance * (pNumberOfBins - 1));
					lRadiusBins[lIndex] += value;
					lRadiusBinsCount[lIndex]++;
				}

			}
		}

		final double lMax = StatUtils.max(lRadiusBins);

		int lSupport = pNumberOfBins - 1;
		for (; lSupport >= 0; lSupport--)
		{
			if (lRadiusBins[lSupport] > 0.01 * lMax)
				break;
		}

		return sqrt(2) * lSupport / pNumberOfBins;/**/

	}

	public static double computeSupport(DoubleArrayImage pDoubleArrayImage,
										int pNumberOfBins,
										double pThreshold)
	{
		final int width = pDoubleArrayImage.getWidth();
		final int height = pDoubleArrayImage.getHeight();
		final double[] array = pDoubleArrayImage.getArray();

		final int centerx = width / 2;
		final int centery = height / 2;

		final double[] lXProjection = new double[pNumberOfBins];
		final double[] lYProjection = new double[pNumberOfBins];

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				final int i = x + width * y;
				final double value = array[i];
				final int cx = x - centerx;
				final int cy = y - centery;

				if (value > 0)
				{
					final double normx = 2.0 * abs(cx) / width;
					final double normy = 2.0 * abs(cy) / height;
					final int lIndexX = (int) round(normx * (pNumberOfBins - 1));
					final int lIndexY = (int) round(normy * (pNumberOfBins - 1));
					lXProjection[lIndexX] += value;
					lYProjection[lIndexY] += value;
				}

			}
		}

		// for (int i = 0; i < pNumberOfBins; i++)
		// System.out.println(lYProjection[i]);

		final double lSupportX = computeSupport(lXProjection,
												pThreshold);
		final double lSupportY = computeSupport(lYProjection,
												pThreshold);

		return max(lSupportX, lSupportY);
	}

	public static double[] computeBaseLevel(DoubleArrayImage pDoubleArrayImage,
											double pPSFSupportDiameter)
	{
		final double[] array = pDoubleArrayImage.getArray();

		final int width = pDoubleArrayImage.getWidth();
		final int height = pDoubleArrayImage.getHeight();

		final int centerx = width / 2;
		final int centery = height / 2;

		final int otfx = (int) (0.5 * width / pPSFSupportDiameter);
		final int otfy = (int) (0.5 * height / pPSFSupportDiameter);

		final TDoubleArrayList lOutsideOTFValueList = new TDoubleArrayList();
		final TDoubleArrayList lInsideOTFValueList = new TDoubleArrayList();
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				final int i = x + width * y;
				final double value = array[i];
				final int cx = x - centerx;
				final int cy = y - centery;

				if (abs(cx) >= otfx || abs(cy) >= otfy)
				{
					lOutsideOTFValueList.add(value);
				}
				else
				{
					lInsideOTFValueList.add(value);
				}
			}
		}

		double lBaseLevel = StatUtils.percentile(	lOutsideOTFValueList.toArray(),
													99.99);
		double lMaxLevel = StatUtils.percentile(lInsideOTFValueList.toArray(),
												99.99);

		return new double[]
		{ lBaseLevel, lMaxLevel };
	}

	public static void removeArtifacts(	DoubleArrayImage pDoubleArrayImage,
										int pMaskWidth,
										double pReplacementValue)
	{
		final double lWidth = pDoubleArrayImage.getWidth();
		final double lHeight = pDoubleArrayImage.getHeight();

		final int centerx = (int) (lWidth / 2);
		final int centery = (int) (lHeight / 2);

		for (int i = -pMaskWidth; i <= pMaskWidth; i++)
		{
			for (int y = 0; y < lHeight; y++)
				if (y < centery - pMaskWidth || y > centery + pMaskWidth)
					pDoubleArrayImage.setInt(	centerx + i,
												y,
												pDoubleArrayImage.getInt(	centerx	+ i
																					+ (i > 0 ? 1
																							: -1),
																			y));

			for (int x = 0; x < lWidth; x++)
				if (x < centerx - pMaskWidth || x > centerx + pMaskWidth)
					pDoubleArrayImage.setInt(	x,
												centery + i,
												pDoubleArrayImage.getInt(	x,
																			centery	+ i
																					+ (i > 0 ? 1
																							: -1)));

		}

	}

	private static double computeSupport(	final double[] lProjection,
											double pThreshold)
	{

		smooth(1, lProjection);

		final double lMaxValue = lProjection[0];

		int i;
		for (i = lProjection.length - 1; i >= 0; i--)
			if (lProjection[i] > pThreshold * lMaxValue)
				break;

		final double lSupport = (1.0 * i) / (lProjection.length - 1);
		return lSupport;
	}

	private static void smooth(int pRepeats, double[] pArray)
	{
		for (int j = 0; j < pRepeats; j++)
		{
			pArray[0] = 0.5 * (pArray[0] + pArray[1]);

			for (int i = 1; i < pArray.length - 1; i++)
				pArray[i] = (pArray[i - 1] + pArray[i] + pArray[i + 1]) / 3;

			pArray[pArray.length - 1] = 0.5 * (pArray[pArray.length - 1] + pArray[pArray.length - 2]);

			for (int i = pArray.length - 2; i >= 1; i--)
				pArray[i] = (pArray[i - 1] + pArray[i] + pArray[i + 1]) / 3;
		}

	}

	private static void debugtiff(	final DoubleArrayImage pDoubleArrayImage,
									String lFileName)
	{
		try
		{
			TiffWriter.write(pDoubleArrayImage, new File(lFileName));
		}
		catch (final DependencyException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final ServiceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final FormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
