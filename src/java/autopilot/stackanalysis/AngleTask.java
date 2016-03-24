package autopilot.stackanalysis;

import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import autopilot.stackanalysis.plane2d.FitResult;
import autopilot.stackanalysis.plane2d.Plane2DFitInterface;
import autopilot.stackanalysis.plane2d.theilsen.TheilSenPlane2DFit;
import autopilot.stackanalysis.plane2d.xi2reg.Xi2RegPlane2DFit;
import autopilot.stackanalysis.visualization.ScatterPlot3D;
import autopilot.stackanalysis.visualization.ScatterPlot3DData;

public class AngleTask implements Callable<AngleTaskResult>
{

	private static final ScatterPlot3D sScatterPlot3D = new ScatterPlot3D(	"AutoPilot focus stack analysis",
																			768,
																			768);

	private final ArrayList<FutureTask<ZPlaneAnalysisResult>> mZPlaneAnalysisFuturTaskList;

	private final int mImageWidth;
	private final int mImageHeight;
	private final double mProbabilityThreshold;
	private final double mPixelLateralResolutionInMicrons;
	private final double mScatteringLengthInPixels;
	private final double mMinProportionOfInliers;

	private final boolean mTakeAllPoints = false;

	private boolean mVisualizeFocalPlane = false;

	private final int mMinDataPoints;

	private final double mMinExtentMicrons;

	private final ScatterPlot3DData mScatterPlot3DData;

	public AngleTask(	int pImageWidth,
						int pImageHeight,
						ArrayList<FutureTask<ZPlaneAnalysisResult>> pZPlaneAnalysisFuturTaskList,
						double pProbabilityThreshold,
						double pPixelLateralResolutionInMicrons,
						double pScatteringLengthInMicrons,
						double pMinProportionOfInliers,
						int pMinDataPoints,
						double pMinExtentMicrons)
	{
		mImageWidth = pImageWidth;
		mImageHeight = pImageHeight;
		mZPlaneAnalysisFuturTaskList = pZPlaneAnalysisFuturTaskList;
		mProbabilityThreshold = pProbabilityThreshold;
		mPixelLateralResolutionInMicrons = pPixelLateralResolutionInMicrons;
		mMinProportionOfInliers = pMinProportionOfInliers;
		mMinDataPoints = pMinDataPoints;
		mMinExtentMicrons = pMinExtentMicrons;
		mScatteringLengthInPixels = pScatteringLengthInMicrons / pPixelLateralResolutionInMicrons;
		mScatterPlot3DData = new ScatterPlot3DData();
	}

	@Override
	public AngleTaskResult call() throws Exception
	{
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		info("starting angle task\n");

		mScatterPlot3DData.clear();

		final AngleTaskResult lAngleTaskResult = new AngleTaskResult();

		final ZPlaneAnalysisResult lFirstZPlaneAnalysisResult = mZPlaneAnalysisFuturTaskList.get(0)
																							.get();

		final int lNumberOfTiles = lFirstZPlaneAnalysisResult.mXList.size();

		final HashMap<FutureTask<Double>, TileFocusTask> lTileFocusFutureTaskMap = new HashMap<>();

		for (int i = 0; i < lNumberOfTiles; i++)
		{
			// format("i=%d \n", i);
			final double lX = lFirstZPlaneAnalysisResult.mXList.get(i);
			final double lY = lFirstZPlaneAnalysisResult.mYList.get(i);
			final TileFocusTask lTileFocusTask = new TileFocusTask(	lX,
																	lY,
																	mProbabilityThreshold);

			for (final FutureTask<ZPlaneAnalysisResult> lZPlaneAnalysisFuturTask : mZPlaneAnalysisFuturTaskList)
			{
				final ZPlaneAnalysisResult lZPlaneAnalysisResult = lZPlaneAnalysisFuturTask.get();

				final double lZ = lZPlaneAnalysisResult.mZ;
				final double lV = lZPlaneAnalysisResult.mVList.get(i);
				lTileFocusTask.add(lZ, lV);
			}

			final FutureTask<Double> lTileFocusFutureTask = new FutureTask<>(lTileFocusTask);
			FocusStackAnalysis.sExecutor.execute(lTileFocusFutureTask);
			lTileFocusFutureTaskMap.put(lTileFocusFutureTask,
										lTileFocusTask);
		}

		info(	"lTileFocusFutureTaskMap.size()= %d \n",
				lTileFocusFutureTaskMap.size());

		final TDoubleArrayList lListX = new TDoubleArrayList();
		final TDoubleArrayList lListY = new TDoubleArrayList();
		final TDoubleArrayList lListZ = new TDoubleArrayList();
		final TDoubleArrayList lMaxFocusValueList = new TDoubleArrayList();
		final TDoubleArrayList lFitProbabilityList = new TDoubleArrayList();

		for (final HashMap.Entry<FutureTask<Double>, TileFocusTask> lEntry : lTileFocusFutureTaskMap.entrySet())
		{
			final TileFocusTask lTileFocusTask = lEntry.getValue();
			if (lTileFocusTask.isUnprobable() || lTileFocusTask.isSaturated())
				continue;

			final double lX = lTileFocusTask.getX();
			final double lY = lTileFocusTask.getY();
			final Double lZ = lEntry.getKey().get();
			if (lZ == null)
				continue;
			final double lMaxFocusValue = lTileFocusTask.getMaxFocusValue();
			final double lFitProbability = lTileFocusTask.getFitProbability();

			lListX.add(lX);
			lListY.add(lY);
			lListZ.add(lZ);
			lMaxFocusValueList.add(lMaxFocusValue);
			lFitProbabilityList.add(lFitProbability);
		}

		if (lListX.size() == 0 || lListY.size() == 0)
		{
			warn("There are no data pointsto work with (not a single tile had content)\n");
			return null;
		}

		filterOutIsolatedOutliers(	lListX,
									lListY,
									lListZ,
									lMaxFocusValueList,
									lFitProbabilityList,
									mScatterPlot3DData,
									4,
									mImageWidth,
									mImageHeight);/**/

		final double lMinX = robustMin(lListX);
		final double lMaxX = robustMax(lListX);
		final double lWidth = lMaxX - lMinX;
		final double lCenterX = 0.5 * (lMinX + lMaxX);
		final double lMinY = robustMin(lListY);
		final double lMaxY = robustMax(lListY);
		final double lHeight = lMaxY - lMinY;
		final double lCenterY = 0.5 * (lMinY + lMaxY);

		final double lWidthInMicrons = lWidth * mPixelLateralResolutionInMicrons;
		final double lHeightInMicrons = lHeight * mPixelLateralResolutionInMicrons;

		final double lAreaInPixels = lWidth * lHeight;
		final double lAreaInMicrons = lWidthInMicrons * lHeightInMicrons;

		final double lMaximalGoodRegionWidth = min(	mScatteringLengthInPixels,
													0.55 * lWidth);
		/*if (lWidth < mScatteringLengthInPixels)
			lMaximalGoodRegionWidth = mScatteringLengthInPixels;/**/

		double lSumFocusValuePositiveSide = 0;
		double lSumFocusValueNegativeSide = 0;

		for (int i = 0; i < lListX.size(); i++)
		{

			final double lX = lListX.get(i);
			final double lY = lListY.get(i);
			final double lMaxFocusValue = lMaxFocusValueList.get(i);

			if (lX > lCenterX)
				lSumFocusValuePositiveSide += lMaxFocusValue;
			else
				lSumFocusValueNegativeSide += lMaxFocusValue;

		}

		final boolean lPositiveSideBetter = lSumFocusValuePositiveSide > lSumFocusValueNegativeSide;

		// (((lPositiveSideBetter && lX > (lMaxX - lMaximalGoodRegionWidth)) ||
		// (!lPositiveSideBetter && lX < lMinX + lMaximalGoodRegionWidth))

		filterOutBadSide(	lListX,
							lListY,
							lListZ,
							lMaxFocusValueList,
							lFitProbabilityList,
							mScatterPlot3DData,
							lPositiveSideBetter,
							lMinX,
							lMaxX,
							lMaximalGoodRegionWidth);/**/

		final double lMaxFocusValueThreshold = findThreshold(lMaxFocusValueList);

		final double lFitProbabilityThreshold = StatUtils.percentile(	lFitProbabilityList.toArray(),
																		10);

		final Plane2DFitInterface lPlane2DFit = new TheilSenPlane2DFit(FocusStackAnalysis.sExecutor);

		for (int i = 0; i < lListX.size(); i++)
		{
			final double lX = lListX.get(i);
			final double lY = lListY.get(i);
			final double lZ = lListZ.get(i);
			final double lMaxFocusValue = lMaxFocusValueList.get(i);
			final double lFitProbability = lFitProbabilityList.get(i);

			if (lMaxFocusValue >= lMaxFocusValueThreshold && lFitProbability >= lFitProbabilityThreshold)
				lPlane2DFit.addPoint(lX, lY, lZ);
			else
				mScatterPlot3DData.addPoint(lX, lY, lZ, 1, 0, 1, 0.6);

		}

		final int lMinNumberOfInliers = (int) (mMinProportionOfInliers * lPlane2DFit.getNumberOfDataPoints());
		lPlane2DFit.setMinimumNumberOfInliers(lMinNumberOfInliers);
		final FitResult lFitResult = lPlane2DFit.fit();
		/*(	16,	0.25,3,lMinNumberOfInliers);/**/

		info("fit: %s \n", lFitResult);

		for (int i = 0; i < lPlane2DFit.getListX().size(); i++)
		{
			final double lX = lPlane2DFit.getListX().get(i);
			final double lY = lPlane2DFit.getListY().get(i);
			final double lZ = lPlane2DFit.getListZ().get(i);

			if (mVisualizeFocalPlane && lFitResult.inliers[i])
				mScatterPlot3DData.addPoint(lX,
											lY,
											lZ,
											0,
											0.6,
											0,
											0.6);
			else
				mScatterPlot3DData.addPoint(lX,
											lY,
											lZ,
											0,
											0,
											0.8,
											0.6);
		}

		if (lFitResult == null)
			return null;

		final double lIntersect = lFitResult.regparams[0];
		final double slopex = lFitResult.regparams[1];
		final double slopey = lFitResult.regparams[2];

		if (mVisualizeFocalPlane)
		{
			for (double x = lMinX; x < lMaxX; x += 10)
				for (double y = lMinY; y < lMaxY; y += 10)
				{
					final double z = x	* slopex
										+ y
										* slopey
										+ lIntersect;
					mScatterPlot3DData.addPoint(x,
												y,
												z,
												0,
												0.6,
												1,
												0.05);
				}
			sScatterPlot3D.set(mScatterPlot3DData);
			sScatterPlot3D.ensureOpened();
		}

		info("slope along x = %g \n", slopex);
		info("slope along y = %g \n", slopey);

		final double anglex = toDegrees(atan(slopex / mPixelLateralResolutionInMicrons));
		final double angley = toDegrees(atan(slopey / mPixelLateralResolutionInMicrons));

		info("angle x = %g \n", anglex);
		info("angle y = %g \n", angley);

		lAngleTaskResult.alpha = angley;
		lAngleTaskResult.beta = anglex;
		lAngleTaskResult.pvalue = lFitResult.pvalue;

		info("finished angle task\n");

		final int lNumberOfInliers = lFitResult.countInliers();

		if (lNumberOfInliers < mMinDataPoints)
		{
			warn(	"fit has only %d data points supporting it, this is not enough! (>%d)\n",
					lNumberOfInliers,
					mMinDataPoints);
			return null;
		}

		info("extent along X : %g um\n", lWidthInMicrons);

		if (lWidthInMicrons < mMinExtentMicrons)
		{
			warn(	"extent along X of %g um is too small to provide accurate fit (>%g um)\n",
					lWidthInMicrons,
					mMinExtentMicrons);
			return null;
		}

		info("extent along Y : %g um\n", lHeightInMicrons);

		if (lHeightInMicrons < mMinExtentMicrons)
		{
			warn(	"extent along Y of %g um is too small to provide accurate fit (>%g um)\n",
					lHeightInMicrons,
					mMinExtentMicrons);
			return null;
		}

		return lAngleTaskResult;
	}

	private double findThreshold(TDoubleArrayList pList)
	{
		double[] lArray = pList.toArray();

		/*int[] lCalcHistogram = calcHistogram(lArray,pList.min(),pList.max(),256);
		
		System.out.println("______________________________");
		for(int i=0; i<lCalcHistogram.length; i++)
			System.out.println(lCalcHistogram[i]);/**/

		double lMedian = StatUtils.percentile(lArray, 50);

		TDoubleArrayList lDeviations = new TDoubleArrayList(lArray.length);
		for (int i = 0; i < lArray.length; i++)
		{
			double lValue = lArray[i];

			double lRightDeviation = abs(lValue - lMedian);
			lDeviations.add(lRightDeviation);

		}

		double lRobustDeviation = StatUtils.percentile(lDeviations.toArray(),50);
		double lThreshold = lMedian - lRobustDeviation;

		return lThreshold;
	}

	public static int[] calcHistogram(	double[] data,
										double min,
										double max,
										int numBins)
	{
		final int[] result = new int[numBins];
		final double binSize = (max - min) / numBins;

		for (double d : data)
		{
			int bin = (int) ((d - min) / binSize);
			if (bin < 0)
			{ /* this data is smaller than min */
			}
			else if (bin >= numBins)
			{ /* this data point is bigger than max */
			}
			else
			{
				result[bin] += 1;
			}
		}
		return result;
	}

	private void filterOutIsolatedOutliers(	TDoubleArrayList pListX,
											TDoubleArrayList pListY,
											TDoubleArrayList pListZ,
											TDoubleArrayList pFocusValueList,
											TDoubleArrayList pFitProbabilityList,
											ScatterPlot3DData pScatterPlot3DData,
											int pMaxNumberOfIsolatedPoints,
											int pNumberOfBinsX,
											int pNumberOfBinsY)
	{

		final double lXMin = pListX.min();
		final double lXMax = pListX.max();
		final double lYMin = pListY.min();
		final double lYMax = pListY.max();

		final int[] lBinsX = new int[pNumberOfBinsX];
		final int[] lBinsY = new int[pNumberOfBinsY];

		for (int i = 0; i < pListX.size(); i++)
		{
			final double lX = pListX.get(i);
			final double lY = pListY.get(i);
			final double lNormalizedX = (lX - lXMin) / (lXMax - lXMin);
			final double lNormalizedY = (lY - lYMin) / (lYMax - lYMin);
			final int lIndexX = (int) (lNormalizedX * (lBinsX.length - 1));
			final int lIndexY = (int) (lNormalizedY * (lBinsY.length - 1));
			lBinsX[lIndexX]++;
			lBinsY[lIndexY]++;
		}

		for (int i = pListX.size() - 1; i >= 0; i--)
		{
			final double lX = pListX.get(i);
			final double lY = pListY.get(i);
			final double lZ = pListZ.get(i);
			final double lNormalizedX = (lX - lXMin) / (lXMax - lXMin);
			final double lNormalizedY = (lY - lYMin) / (lYMax - lYMin);
			final int lIndexX = (int) (lNormalizedX * (lBinsX.length - 1));
			final int lIndexY = (int) (lNormalizedY * (lBinsY.length - 1));

			final int lNumberOfPointsInBinX = lBinsX[lIndexX];
			final int lNumberOfPointsInBinY = lBinsY[lIndexY];
			if (lNumberOfPointsInBinX <= pMaxNumberOfIsolatedPoints && lNumberOfPointsInBinY <= pMaxNumberOfIsolatedPoints)
			{
				pListX.remove(i, 1);
				pListY.remove(i, 1);
				pListZ.remove(i, 1);
				pFocusValueList.remove(i, 1);
				pFitProbabilityList.remove(i, 1);

				mScatterPlot3DData.addPoint(lX,
											lY,
											lZ,
											1,
											0.5,
											0.5,
											0.6);
			}
		}

	}

	private void filterOutBadSide(	TDoubleArrayList pListX,
									TDoubleArrayList pListY,
									TDoubleArrayList pListZ,
									TDoubleArrayList pFocusValueList,
									TDoubleArrayList pFitProbabilityList,
									ScatterPlot3DData pScatterPlot3DData,
									boolean pPositiveSideBetter,
									double pMinX,
									double pMaxX,
									double pMaximalGoodRegionWidth)
	{

		for (int i = pListX.size() - 1; i >= 0; i--)
		{
			final double lX = pListX.get(i);
			final double lY = pListY.get(i);
			final double lZ = pListZ.get(i);

			final boolean keep = ((pPositiveSideBetter && lX > (pMaxX - pMaximalGoodRegionWidth)) || (!pPositiveSideBetter && lX < pMinX + pMaximalGoodRegionWidth));

			if (!keep)
			{
				pListX.remove(i, 1);
				pListY.remove(i, 1);
				pListZ.remove(i, 1);
				pFocusValueList.remove(i, 1);
				pFitProbabilityList.remove(i, 1);

				mScatterPlot3DData.addPoint(lX, lY, lZ, 1, 0, 0, 0.6);
			}
		}

	}

	private double robustMax(TDoubleArrayList pList)
	{
		final Percentile lPercentile = new Percentile(99);
		final double lRobustMax = lPercentile.evaluate(pList.toArray());
		return lRobustMax;
	}

	private double robustMin(TDoubleArrayList pList)
	{
		final Percentile lPercentile = new Percentile(1);
		final double lRobustMin = lPercentile.evaluate(pList.toArray());
		return lRobustMin;
	}

	public boolean isVisualizeFocalPlane()
	{
		return mVisualizeFocalPlane;
	}

	public void setVisualizeFocalPlane(boolean pVisualizeFocalPlane)
	{
		mVisualizeFocalPlane = pVisualizeFocalPlane;
	}

	private static final Logger mLogger = Logger.getLogger("autopilot");

	private static final void info(String format, Object... args)
	{
		mLogger.info(String.format(format, args));
		// System.out.format(format, args);
	}

	private static final void warn(String format, Object... args)
	{
		mLogger.warning(String.format(format, args));
		// System.out.format(format, args);
	}
}
