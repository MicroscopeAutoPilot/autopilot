package autopilot.stackanalysis;

import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.implementations.differential.Tenengrad;
import autopilot.measures.implementations.spectral.NormDCTEntropyShannon;

public class ZPlaneAnalysisTask	implements
																Callable<ZPlaneAnalysisResult>
{

	private final double mZ;
	private final DoubleArrayImage mDoubleArrayImage;
	private final boolean mComputeTiles;
	private final boolean mComputeMetric;
	private final int mMinTileStride;
	private final int mMaxNumberOfTiles;

	private final double mPSFSupportDiameter;
	private final boolean mFastMeasure;
	private final OrientationTransform mOrientationTransform;

	private final FutureTask<CropTaskResult> mCropFutureTask;

	public ZPlaneAnalysisTask(double pZ,
														DoubleArrayImage pDoubleArrayImage,
														boolean pComputeTiles,
														boolean pComputeMetric,
														int pMinTileStride,
														int pMaxNumberOfTiles,
														int pPSFSupportDiameter,
														boolean pFastMeasure,
														OrientationTransform pOrientationTransform,
														FutureTask<CropTaskResult> pCropFutureTask)
	{
		super();

		mZ = pZ;
		mDoubleArrayImage = pDoubleArrayImage;
		mComputeTiles = pComputeTiles;
		mComputeMetric = pComputeMetric;
		mMinTileStride = pMinTileStride;
		mMaxNumberOfTiles = pMaxNumberOfTiles;
		mPSFSupportDiameter = pPSFSupportDiameter;
		mFastMeasure = pFastMeasure;
		mOrientationTransform = pOrientationTransform;
		mCropFutureTask = pCropFutureTask;
	}

	@Override
	public ZPlaneAnalysisResult call() throws Exception
	{
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		info("started z plane analysis task\n");

		final ZPlaneAnalysisResult lZPlaneAnalysisResult = new ZPlaneAnalysisResult();

		if (mComputeTiles)
			computeMetricPerTiles(lZPlaneAnalysisResult);
		if (mComputeMetric)
			computeMetric(lZPlaneAnalysisResult);

		info("finished z plane analysis task\n");

		return lZPlaneAnalysisResult;
	}

	private void computeMetric(ZPlaneAnalysisResult pZPlaneAnalysisResult)
	{
		final double metric;

		if (mFastMeasure)
			metric = Tenengrad.compute(	mDoubleArrayImage,
																	mPSFSupportDiameter);
		else
			metric = NormDCTEntropyShannon.compute(	mDoubleArrayImage,
																							mPSFSupportDiameter);

		info("full image metric: %g\n", metric);
		pZPlaneAnalysisResult.mMetric = metric;
	}

	private void computeMetricPerTiles(ZPlaneAnalysisResult pZPlaneAnalysisResult)
	{
		DoubleArrayImage lTileImage = null;

		final int lImageWidth = mDoubleArrayImage.getWidth();
		final int lImageHeight = mDoubleArrayImage.getHeight();
		info("image (%d,%d) \n", lImageWidth, lImageHeight);

		int lMinX = 0;
		int lMaxX = mDoubleArrayImage.getWidth();
		int lMinY = 0;
		int lMaxY = mDoubleArrayImage.getHeight();

		final int lMargin = 2 * mMinTileStride;
		try
		{
			final CropTaskResult lCropTaskResult = mCropFutureTask.get();
			if (lCropTaskResult != null)
			{
				lMinX = max(lCropTaskResult.xmin - lMargin, 0);
				lMaxX = min(lCropTaskResult.xmax + lMargin, lImageWidth);
				lMinY = max(lCropTaskResult.ymin - lMargin, 0);
				lMaxY = min(lCropTaskResult.ymax + lMargin, lImageHeight);
			}
		}
		catch (InterruptedException | ExecutionException e)
		{
			e.printStackTrace();
		}

		final int lCroppedImageWidth = lMaxX - lMinX;
		final int lCroppedImageHeight = lMaxY - lMinY;
		info(	"cropped image (%d,%d) \n",
					lCroppedImageWidth,
					lCroppedImageHeight);

		final int lTileGridSize = (int) sqrt(mMaxNumberOfTiles);
		info("tile grid size: %d \n", lTileGridSize);

		int lTilesStrideX = (int) round((1.0 * lCroppedImageWidth) / lTileGridSize);
		int lTilesStrideY = (int) round((1.0 * lCroppedImageHeight) / lTileGridSize);
		info("tiles stride (%d,%d) \n", lTilesStrideX, lTilesStrideY);

		lTilesStrideX = max(lTilesStrideX, mMinTileStride);
		lTilesStrideY = max(lTilesStrideY, mMinTileStride);
		info(	"tiles stride (after min) (%d,%d) \n",
					lTilesStrideX,
					lTilesStrideY);

		final int lTileWidth = 2 * lTilesStrideX;
		final int lTileHeight = 2 * lTilesStrideY;
		info(	"tiles width and height: (%d,%d) \n",
					lTileWidth,
					lTileHeight);

		pZPlaneAnalysisResult.mTileWidth = lTileWidth;
		pZPlaneAnalysisResult.mTileHeight = lTileHeight;
		pZPlaneAnalysisResult.mTileStrideX = lTilesStrideX;
		pZPlaneAnalysisResult.mTileStrideY = lTilesStrideY;


		int lTileCount = 0;
		for (int lY = lMinY; lY + lTileHeight < lMaxY; lY += lTilesStrideY)
			for (int lX = lMinX; lX + lTileWidth < lMaxX; lX += lTilesStrideX)
			{

				lTileImage = mDoubleArrayImage.subImage(lX,
																								lY,
																								lTileWidth,
																								lTileHeight,
																								lTileImage);

				double metric;

				if (mFastMeasure)
					metric = Tenengrad.compute(lTileImage, mPSFSupportDiameter);
				else
					metric = NormDCTEntropyShannon.compute(	lTileImage,
																									mPSFSupportDiameter);

				final double lTileCenterX = lX + lTileWidth / 2;
				final double lTileCenterY = lY + lTileHeight / 2;

				final double lTransformedTileCenterX = mOrientationTransform.transformX(lCroppedImageWidth,
																																								lCroppedImageHeight,
																																								lTileCenterX,
																																								lTileCenterY);
				final double lTransformedTileCenterY = mOrientationTransform.transformY(lCroppedImageWidth,
																																								lCroppedImageHeight,
																																								lTileCenterX,
																																								lTileCenterY);

				// format("%g\t%g\t%g\n", lTransformedTileCenterX,
				// lTransformedTileCenterY, metric);
				pZPlaneAnalysisResult.add(lTransformedTileCenterX,
																	lTransformedTileCenterY,
																	metric);

				lTileCount++;
			}

		info("done computing dcts per tile\n");
		info("effective number of tiles: %d\n", lTileCount);

		pZPlaneAnalysisResult.mZ = mZ;
	}

	int po2(double pX)
	{
		return (int) pow(2, round(log(pX) / log(2)));
	}

	private static final Logger mLogger = Logger.getLogger("autopilot");

	private static final void info(String format, Object... args)
	{
		mLogger.info(String.format(format, args));
		// System.out.format(format, args);
	}

}
