package autopilot.measures.implementations.resolution;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.log;
import static java.lang.Math.round;

import java.io.File;
import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.formats.FormatException;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import autopilot.image.DoubleArrayImage;
import autopilot.image.writers.TiffWriter;
import autopilot.measures.FocusMeasureInterface;
import autopilot.measures.FocusMeasures;

/**
 * ISORES - Measure of image mEstimateResolution isotropy.
 * 
 * @author royer
 */
public class DFTIsotropicResolutionMeasure implements
																					FocusMeasureInterface
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
	 * Computes the Discrete Fourier Transform High to Low frequency ratio focus
	 * measure.
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
		return compute(	pDoubleArrayImage,
										pPSFSupportDiameter,
										FocusMeasures.cNumberOfAngleBins);
	}

	/**
	 * Computes the Discrete Fourier Transform High to Low frequency ratio focus
	 * measure.
	 * 
	 * @param pDoubleArrayImage
	 *          image
	 * @param pPSFSupportDiameter
	 *          PSF support diameter
	 * 
	 * @param pNumberOfAngleBins
	 *          Number of bins for angles
	 * @return focus measure value
	 */
	public static final double compute(	final DoubleArrayImage pDoubleArrayImage,
																			final double pPSFSupportDiameter,
																			int pNumberOfAngleBins)
	{
		pDoubleArrayImage.normalizeNormL2();
		pDoubleArrayImage.fftLogPower();

		final double[] array = pDoubleArrayImage.getArray();

		final int length = pDoubleArrayImage.getLength();
		final int width = pDoubleArrayImage.getWidth();
		final int height = pDoubleArrayImage.getHeight();

		final int centerx = width / 2;
		final int centery = height / 2;

		final int otfx = (int) (0.5 * width / pPSFSupportDiameter);
		final int otfy = (int) (0.5 * height / pPSFSupportDiameter);

		final Median lMedian = new Median();

		final double lMedianValue = lMedian.evaluate(array);

		// System.out.println("lMedianValue=" + lMedianValue);

		/*debugtiff(pDoubleArrayImage,
							"/Users/royer/Desktop/beforediff.tif");/**/

		for (int i = 0; i < length; i++)
		{
			final double lDiff = array[i] - lMedianValue;
			final double lPosDiff = lDiff < 0 ? 0 : lDiff;
			array[i] = lPosDiff;
		}

		/*debugtiff(pDoubleArrayImage, "/Users/royer/Desktop/afterdiff.tif");/**/

		final double[] lAngleBins = new double[pNumberOfAngleBins];
		final double[] lAngleBinsCount = new double[pNumberOfAngleBins];

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				final int i = x + width * y;
				final double value = array[i];
				final int cx = x - centerx;
				final int cy = y - centery;

				if (abs(cx) <= otfx && abs(cy) <= otfy)
				{
					final double theta = (atan2(cy, cx) + PI) / (2 * PI);
					final int index = (int) round(theta * (pNumberOfAngleBins - 1));
					lAngleBins[index] += value;
					lAngleBinsCount[index]++;
				}
			}
		}

		for (int i = 0; i < pNumberOfAngleBins; i++)
			if (lAngleBinsCount[i] != 0)
				lAngleBins[i] /= lAngleBinsCount[i];

		/*for (int i = 0; i < lAngleBins.length; i++)
			System.out.println(lAngleBins[i]);/**/

		double lSum = 0;
		for (int i = 0; i < pNumberOfAngleBins; i++)
			lSum += lAngleBins[i];

		if (lSum == 0)
			return 1;

		for (int i = 0; i < pNumberOfAngleBins; i++)
			lAngleBins[i] /= lSum;

		double entropy = 0;
		double maxentropy = 0;

		for (int i = 0; i < pNumberOfAngleBins; i++)
		{
			entropy += -log(lAngleBins[i]) * lAngleBins[i];
			final double p = 1.0 / pNumberOfAngleBins;
			maxentropy += -log(p) * p;
		}

		final double relentropy = entropy / maxentropy;

		final double min = StatUtils.min(lAngleBins);
		final double max = StatUtils.max(lAngleBins);

		final double measureminmax = 1 - (max - min) / (max + min);

		return relentropy;
	}

	private static void debugtiff(final DoubleArrayImage pDoubleArrayImage,
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
