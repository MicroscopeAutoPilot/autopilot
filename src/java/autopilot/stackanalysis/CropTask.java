package autopilot.stackanalysis;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.StatUtils;

import autopilot.image.DoubleArrayImage;

public class CropTask implements Callable<CropTaskResult>
{

	private final DoubleArrayImage mDoubleArrayImage;

	public CropTask(DoubleArrayImage pDoubleArrayImage)
	{
		mDoubleArrayImage = pDoubleArrayImage.copy();
	}

	@Override
	public CropTaskResult call() throws Exception
	{
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		info("started crop task\n");

		final CropTaskResult lCropTaskResult = new CropTaskResult();

		final DoubleArrayImage lHorizontalProjection = mDoubleArrayImage.subImageHorizontallyCollapsed(	0,
																																																		0,
																																																		mDoubleArrayImage.getWidth(),
																																																		mDoubleArrayImage.getHeight(),
																																																		null);

		final DoubleArrayImage lVerticalProjection = mDoubleArrayImage.subImageVerticallyCollapsed(	0,
																																																0,
																																																mDoubleArrayImage.getWidth(),
																																																mDoubleArrayImage.getHeight(),
																																																null);

		lHorizontalProjection.mult(1.0 / mDoubleArrayImage.getWidth());
		lVerticalProjection.mult(1.0 / mDoubleArrayImage.getHeight());

		final double lThreshold = computeThreshold(mDoubleArrayImage);

		final int[] lCrop1DY = crop1D(lHorizontalProjection, lThreshold);
		final int[] lCrop1DX = crop1D(lVerticalProjection, lThreshold);

		lCropTaskResult.xmin = lCrop1DX[0];
		lCropTaskResult.ymin = lCrop1DY[0];
		lCropTaskResult.xmax = lCrop1DX[1];
		lCropTaskResult.ymax = lCrop1DY[1];

		info("x: [%d,%d] \n", lCropTaskResult.xmin, lCropTaskResult.xmax);
		info("y: [%d,%d] \n", lCropTaskResult.ymin, lCropTaskResult.ymax);

		info("finished crop task\n");

		return lCropTaskResult;
	}

	private double computeThreshold(final DoubleArrayImage lHorizontalProjection)
	{
		final double[] lArray = lHorizontalProjection.getArray();
		final double lRobustMax = StatUtils.percentile(lArray, 90);
		final double lRobustMin = StatUtils.mode(lArray)[0] + 10;
		final double lAmplitude = lRobustMax - lRobustMin;
		final double lThreshold = lRobustMin + 0.05 * (lAmplitude);
		return lThreshold;
	}

	private int[] crop1D(	final DoubleArrayImage lProjection,
												double pThreshold)
	{
		final double[] lArray = lProjection.getArray();

		// final Percentile lPercentile = new Percentile();
		// final double lRobustMin = lPercentile.evaluate(lArray, 10);

		info("threshold : %g \n", pThreshold);

		for (int i = 2; i < lArray.length - 2; i++)
			lArray[i] = (lArray[i - 2] + lArray[i - 1]
										+ lArray[i]
										+ lArray[i + 1] + lArray[i + 2]) / 5.0;
		lArray[0] = 0;
		lArray[1] = 0;
		lArray[lArray.length - 1] = 0;
		lArray[lArray.length - 2] = 0;

		int lMin = lArray.length;
		int lMax = 0;
		for (int i = 2; i < lArray.length - 2; i++)
			if (lArray[i] > pThreshold)
			{
				lMin = min(lMin, i);
				lMax = max(lMax, i);
			}

		return new int[]
		{ lMin, lMax };
	}

	private static final Logger mLogger = Logger.getLogger("autopilot");

	private static final void info(String format, Object... args)
	{
		mLogger.info(String.format(format, args));
		// System.out.format(format, args);
	}
}
