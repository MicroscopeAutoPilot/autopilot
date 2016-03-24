package autopilot.stackanalysis.generic;

import gnu.trove.list.array.TDoubleArrayList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import autopilot.image.DoubleArrayImage;
import autopilot.image.readers.TiffReader;
import autopilot.stackanalysis.CropTask;
import autopilot.stackanalysis.CropTaskResult;
import autopilot.stackanalysis.DeltaZTask;
import autopilot.stackanalysis.OrientationTransform;
import autopilot.stackanalysis.TileFocusTask;
import autopilot.stackanalysis.ZPlaneAnalysisResult;
import autopilot.stackanalysis.ZPlaneAnalysisTask;
import autopilot.utils.svg.SimpleSVGGenerator;

public class GenericFocusStackAnalysis
{

	private static final double cDefaultLateralPixelSize = 0.406;
	private static final double cDefaultFitProbabilityThreshold = 0.98;

	protected static final ExecutorService sExecutor = Executors.newFixedThreadPool(Runtime.getRuntime()
																																													.availableProcessors());

	private final ArrayList<ZPlaneAnalysisTask> mZPlaneAnalysisTask = new ArrayList<>();

	private final ArrayList<FutureTask<ZPlaneAnalysisResult>> mZPlaneAnalysisFuturTaskList = new ArrayList<>();

	private final HashMap<String, Double> mParameters = new HashMap<>();

	private FutureTask<CropTaskResult> mCropFutureTask;
	private FutureTask<Double> mDeltaZFutureTask;

	public GenericFocusStackAnalysis()
	{
		super();
		setBooleanParameter("computedz", true);
	}

	public boolean isComputeDz()
	{
		return getBooleanParameter("computedz");
	}

	public int getNumberOfImages()
	{
		return (int) getDoubleParameter("nbplanes");
	}

	public void setDoubleParameter(String pParameterName, double pValue)
	{
		mParameters.put(pParameterName, pValue);
	}

	public void setBooleanParameter(String pParameterName,
																	boolean pValue)
	{
		setDoubleParameter(pParameterName, pValue ? 1 : 0);
	}

	public double getDoubleParameter(String pParameterName)
	{
		return mParameters.get(pParameterName.trim().toLowerCase());
	}

	public double getDoubleParameter(	String pParameterName,
																		double pDefaultValue)
	{
		final Double lValue = mParameters.get(pParameterName.trim()
																												.toLowerCase());
		if (lValue == null)
			return pDefaultValue;
		return lValue;
	}

	public boolean getBooleanParameter(	String pParameterName,
																			boolean pDefaultValue)
	{
		final boolean lValue = getDoubleParameter(pParameterName, 0) > 0;
		return lValue;
	}

	public boolean getBooleanParameter(String pParameterName)
	{
		return mParameters.get(pParameterName.trim().toLowerCase()) > 0;
	}

	public final void reset()
	{
		for (final FutureTask<ZPlaneAnalysisResult> lFuturTask : getZPlaneAnalysisFuturTaskList())
		{
			lFuturTask.cancel(false);
		}
		getZPlaneAnalysisFuturTaskList().clear();
		mZPlaneAnalysisTask.clear();
	}

	public final void loadPlanes(double[] pZArray, File pTiffFile) throws IOException
	{

		final int lNumberOfPlanes = TiffReader.nbpages(pTiffFile);
		info("lNumberOfPlanes= %d \n", lNumberOfPlanes);

		setDoubleParameter("nbplanes", lNumberOfPlanes);
		for (int i = 0; i < lNumberOfPlanes; i++)
		{
			final DoubleArrayImage lDoubleArrayImage = TiffReader.read(	pTiffFile,
																																	i,
																																	null);

			loadPlane(pZArray[i], lDoubleArrayImage);
		}

	}

	public final void loadPlanes(	double[] pZArray,
																Class<?> pRootClass,
																String pTiffRessourceFileName) throws IOException
	{

		final int lNumberOfPlanes = TiffReader.nbpages(pRootClass.getResourceAsStream(pTiffRessourceFileName));
		info("lNumberOfPlanes=%d \n", lNumberOfPlanes);

		setDoubleParameter("nbplanes", lNumberOfPlanes);
		for (int i = 0; i < lNumberOfPlanes; i++)
		{
			final DoubleArrayImage lDoubleArrayImage = TiffReader.read(	pRootClass.getResourceAsStream(pTiffRessourceFileName),
																																	i,
																																	null);

			loadPlane(pZArray[i], lDoubleArrayImage);
		}

	}

	public final void loadPlanes(	double[] pZArray,
																DoubleArrayImage[] pDoubleArrayImages) throws IOException
	{

		final int lNumberOfPlanes = pDoubleArrayImages.length;
		info("lNumberOfPlanes=%d \n", lNumberOfPlanes);

		setDoubleParameter("nbplanes", lNumberOfPlanes);
		for (int i = 0; i < lNumberOfPlanes; i++)
		{
			loadPlane(pZArray[i], pDoubleArrayImages[i]);
		}

	}

	public final void loadPlane(double pZ,
															DoubleArrayImage pDoubleArrayImage)
	{
		final boolean lComputeTiles = true;
		final boolean lComputeMetric = isComputeDz();

		final int lMinTileStride = (int) getDoubleParameter("mintilestride[px]",
																												8);
		final int lMaxNumberOfTiles = (int) getDoubleParameter(	"maxtiles",
																														500);

		final int lPSFSupportDiameter = (int) getDoubleParameter(	"psfsupportdiameter[px]",
																															3);
		final boolean lFastMeasure = getBooleanParameter(	"fastmeasure",
																											false);

		final OrientationTransform lOrientationTransform = OrientationTransform.getFromInt((int) getDoubleParameter("orientation",
																																																								0));

		if (mZPlaneAnalysisTask.size() == 0)
		{
			final CropTask lCropTask = new CropTask(pDoubleArrayImage);
			mCropFutureTask = new FutureTask<CropTaskResult>(lCropTask);
			sExecutor.execute(mCropFutureTask);
		}

		final ZPlaneAnalysisTask lZPlaneAnalysisTask = new ZPlaneAnalysisTask(pZ,
																																					pDoubleArrayImage,
																																					lComputeTiles,
																																					lComputeMetric,
																																					lMinTileStride,
																																					lMaxNumberOfTiles,
																																					lPSFSupportDiameter,
																																					lFastMeasure,
																																					lOrientationTransform,
																																					mCropFutureTask);
		mZPlaneAnalysisTask.add(lZPlaneAnalysisTask);

		final FutureTask<ZPlaneAnalysisResult> lZPlaneAnalysisFuturTask = new FutureTask<ZPlaneAnalysisResult>(lZPlaneAnalysisTask);

		sExecutor.execute(lZPlaneAnalysisFuturTask);

		getZPlaneAnalysisFuturTaskList().add(lZPlaneAnalysisFuturTask);

		if (mZPlaneAnalysisTask.size() == getNumberOfImages())
			finishLoadingPlanes(pDoubleArrayImage.getWidth(),
													pDoubleArrayImage.getHeight());
	}

	private final void finishLoadingPlanes(	int pImageWidth,
																					int pImageHeight)
	{
		if (isComputeDz())
		{
			final double lFitProbabilityThreshold = getDoubleParameter(	"mFitProbability",
																																	cDefaultFitProbabilityThreshold);
			mDeltaZFutureTask = new FutureTask<>(new DeltaZTask(getZPlaneAnalysisFuturTaskList(),
																													lFitProbabilityThreshold));
			sExecutor.execute(mDeltaZFutureTask);
		}

	}

	public boolean isDone()
	{
		boolean lIsDone = true;

		if (mDeltaZFutureTask != null)
			lIsDone &= mDeltaZFutureTask.isDone();
		return lIsDone;
	}

	public Double getDeltaZ(double pMaxWaitTimeInSeconds)	throws InterruptedException,
																												ExecutionException
	{
		try
		{
			if (mDeltaZFutureTask == null)
				return null;

			return mDeltaZFutureTask.get(	(long) (1000 * pMaxWaitTimeInSeconds),
																		TimeUnit.MILLISECONDS);
		}
		catch (final TimeoutException e)
		{
			return null;
		}

	}

	public void getTileFocusSVG(File pSVGFile,
															double pProbabilityThreshold)	throws InterruptedException,
																														ExecutionException,
																														IOException
	{

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
																															pProbabilityThreshold);

			for (final FutureTask<ZPlaneAnalysisResult> lZPlaneAnalysisFuturTask : mZPlaneAnalysisFuturTaskList)
			{
				final ZPlaneAnalysisResult lZPlaneAnalysisResult = lZPlaneAnalysisFuturTask.get();

				final double lZ = lZPlaneAnalysisResult.mZ;
				final double lV = lZPlaneAnalysisResult.mVList.get(i);
				lTileFocusTask.add(lZ, lV);
			}

			final FutureTask<Double> lTileFocusFutureTask = new FutureTask<>(lTileFocusTask);
			sExecutor.execute(lTileFocusFutureTask);
			lTileFocusFutureTaskMap.put(lTileFocusFutureTask,
																	lTileFocusTask);
		}

		final int lMinX = (int) lFirstZPlaneAnalysisResult.mXList.min();
		final int lMinY = (int) lFirstZPlaneAnalysisResult.mYList.min();
		final int lMaxX = (int) lFirstZPlaneAnalysisResult.mXList.max();
		final int lMaxY = (int) lFirstZPlaneAnalysisResult.mYList.max();
		final SimpleSVGGenerator lSimpleSVGGenerator = new SimpleSVGGenerator(pSVGFile,
																																					lMaxX,
																																					lMaxY);

		final TDoubleArrayList lZList = new TDoubleArrayList();

		for (final HashMap.Entry<FutureTask<Double>, TileFocusTask> lEntry : lTileFocusFutureTaskMap.entrySet())
		{
			final TileFocusTask lTileFocusTask = lEntry.getValue();
			if (lTileFocusTask.isUnprobable() || lTileFocusTask.isSaturated())
				continue;
			final Double lZ = lEntry.getKey().get();
			if (lZ == null)
				continue;
			lZList.add(lZ);
		}

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

			final double lNormalizedZ = (lZ - lZList.min()) / (lZList.max() - lZList.min());

			final int gray = (int) (256f * lNormalizedZ);
			lSimpleSVGGenerator.setCurrentLayer(0);
			lSimpleSVGGenerator.addRectangle(	(int) (lX - lFirstZPlaneAnalysisResult.mTileStrideX),
																				(int) (lY - lFirstZPlaneAnalysisResult.mTileStrideY),
																				lFirstZPlaneAnalysisResult.mTileStrideX,
																				lFirstZPlaneAnalysisResult.mTileStrideY,
																				SimpleSVGGenerator.getColorString(gray,
																																					gray,
																																					gray),
																				SimpleSVGGenerator.getColorString(gray,
																																					gray,
																																					gray),
																				0,
																				1);

			lSimpleSVGGenerator.setCurrentLayer(1);
			lSimpleSVGGenerator.addText(String.format("f=%.1f", lZ),
																	(int) (lX - lFirstZPlaneAnalysisResult.mTileStrideX
																					/ 2 + 1),
																	(int) lY + 10 + 1,
																	SimpleSVGGenerator.getColorString(0,
																																		0,
																																		0),
																	30);
			lSimpleSVGGenerator.addText(String.format("f=%.1f", lZ),
																	(int) (lX - lFirstZPlaneAnalysisResult.mTileStrideX / 2),
																	(int) lY + 10,
																	SimpleSVGGenerator.getColorString(1,
																																		1,
																																		1),
																	30);

		}

		lSimpleSVGGenerator.close();

	}

	public double[] getFocusCurve()	throws InterruptedException,
																	ExecutionException
	{
		final double[] lFocusCurve = new double[mZPlaneAnalysisFuturTaskList.size()];

		int i = 0;
		for (final FutureTask<ZPlaneAnalysisResult> lFutureTask : mZPlaneAnalysisFuturTaskList)
		{
			lFocusCurve[i++] = lFutureTask.get().mMetric;
		}
		return lFocusCurve;
	}

	private static final Logger mLogger = Logger.getLogger("autopilot");

	private static final void info(String format, Object... args)
	{
		mLogger.info(String.format(format, args));
		// System.out.format(format, args);
	}

	public ArrayList<FutureTask<ZPlaneAnalysisResult>> getZPlaneAnalysisFuturTaskList()
	{
		return mZPlaneAnalysisFuturTaskList;
	}

}
