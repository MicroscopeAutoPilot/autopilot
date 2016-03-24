package autopilot.stackanalysis;

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
import cern.colt.Arrays;

public class FocusStackAnalysis
{

	private static final double cDefaultLateralPixelSize = 0.406;
	private static final double cDefaultFitProbabilityThreshold = 0.98;

	protected static final ExecutorService sExecutor = Executors.newFixedThreadPool(Runtime.getRuntime()
																																													.availableProcessors());

	ArrayList<ZPlaneAnalysisTask> mZPlaneAnalysisTask = new ArrayList<>();

	ArrayList<FutureTask<ZPlaneAnalysisResult>> mZPlaneAnalysisFuturTaskList = new ArrayList<>();

	HashMap<String, Double> mParameters = new HashMap<>();

	private FutureTask<CropTaskResult> mCropFutureTask;
	private FutureTask<Double> mDeltaZFutureTask;
	private FutureTask<AngleTaskResult> mAngleFutureTask;

	public FocusStackAnalysis()
	{
		super();
		setBooleanParameter("computedz", true);
		setBooleanParameter("computealpha", true);
		setBooleanParameter("computebeta", true);
	}

	public boolean isComputeDz()
	{
		return getBooleanParameter("computedz");
	}

	public boolean isComputeAlpha()
	{
		return getBooleanParameter("computealpha");
	}

	public boolean isComputeBeta()
	{
		return getBooleanParameter("computebeta");
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
		final boolean lValue = getDoubleParameter(pParameterName,
																							pDefaultValue ? 1 : 0) > 0;
		return lValue;
	}

	public boolean getBooleanParameter(String pParameterName)
	{
		return mParameters.get(pParameterName.trim().toLowerCase()) > 0;
	}

	public final void reset()
	{
		for (final FutureTask<ZPlaneAnalysisResult> lFuturTask : mZPlaneAnalysisFuturTaskList)
		{
			lFuturTask.cancel(false);
		}
		mZPlaneAnalysisFuturTaskList.clear();
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
		info(	"Loading image %d z=%g (%d,%d) \n",
					mZPlaneAnalysisTask.size(),
					pZ,
					pDoubleArrayImage.getWidth(),
					pDoubleArrayImage.getHeight());

		final boolean lComputeTiles = isComputeAlpha() || isComputeBeta();
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

		if (getBooleanParameter("debuginputimages", false))
		{
			info("Debug: saving input images \n");
			// ViewImage.view(pDoubleArrayImage);
			final String lDesktopPath = System.getProperty("user.home") + "/Desktop";
			final File lFile = new File(lDesktopPath,
																	String.format("input/input[%d].tiff",
																								mZPlaneAnalysisTask.size()));
			lFile.getParentFile().mkdirs();
			pDoubleArrayImage.writeTiff16bit(lFile);
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

		mZPlaneAnalysisFuturTaskList.add(lZPlaneAnalysisFuturTask);

		if (mZPlaneAnalysisTask.size() == getNumberOfImages())
		{
			finishLoadingPlanes(pDoubleArrayImage.getWidth(),
													pDoubleArrayImage.getHeight());

		}
	}

	private final void finishLoadingPlanes(	int pImageWidth,
																					int pImageHeight)
	{
		try
		{
			info("finishLoadingPlanes(%d,%d) \n", pImageWidth, pImageHeight);

			if (isComputeDz())
			{
				info("isComputeDz() == true \n");
				final double lFitProbabilityThreshold = getDoubleParameter(	"fitprob",
																																		cDefaultFitProbabilityThreshold);
				mDeltaZFutureTask = new FutureTask<>(new DeltaZTask(mZPlaneAnalysisFuturTaskList,
																														lFitProbabilityThreshold));
				sExecutor.execute(mDeltaZFutureTask);
			}
			if (isComputeAlpha() || isComputeBeta())
			{
				info("isComputeAlpha() || isComputeBeta() == true \n");
				final double lFitProbabilityThreshold = getDoubleParameter(	"fitprob",
																																		cDefaultFitProbabilityThreshold);

				final double lPixelLateralResolutionInMicrons = getDoubleParameter(	"pixelsize[um]",
																																						cDefaultLateralPixelSize);
				final double lScatteringLength = getDoubleParameter("scatteringlength[um]",
																														250);

				final boolean lVisualizeFocalPlane = getBooleanParameter(	"visualize",
																																	false);

				final double lMinProportionOfInliers = getDoubleParameter("minratioinliers",
																																	0.50);

				final int lMinDataPoints = (int) getDoubleParameter("mindatapoints",
																														30);

				final double lMinExtentMicrons = getDoubleParameter("minextent[um]",
																														80);

				final OrientationTransform lOrientationTransform = OrientationTransform.getFromInt((int) getDoubleParameter("orientation",
																																																										0));
				info("lOrientationTransform = %s \n", lOrientationTransform);

				final int lTransformedImageWidth = lOrientationTransform.transformWidth(pImageWidth,
																																								pImageHeight);
				final int lTransformedImageHeight = lOrientationTransform.transformHeight(pImageWidth,
																																									pImageHeight);

				info("Creating angle task \n");
				final AngleTask lAngleTask = new AngleTask(	lTransformedImageWidth,
																										lTransformedImageHeight,
																										mZPlaneAnalysisFuturTaskList,
																										lFitProbabilityThreshold,
																										lPixelLateralResolutionInMicrons,
																										lScatteringLength,
																										lMinProportionOfInliers,
																										lMinDataPoints,
																										lMinExtentMicrons);

				info(	"Setting visualisation option to %s \n",
							lVisualizeFocalPlane ? "true" : "false");
				lAngleTask.setVisualizeFocalPlane(lVisualizeFocalPlane);

				info("Creating angle task's futur \n");
				mAngleFutureTask = new FutureTask<>(lAngleTask);

				info("executing angle task now! \n");
				sExecutor.execute(mAngleFutureTask);
			}
		}
		catch (final Throwable e)
		{
			info("Exception in finishLoadingPlanes: '%s'\n", e.getMessage());
			reportException(e);

			throw e;
		}
	}

	private void reportException(final Throwable e)
	{
		for (final StackTraceElement lElement : e.getStackTrace())
			info("\t%s\n", lElement.toString());
		if (e.getCause() != null)
		{
			info("Caused by:\n");
			reportException(e.getCause());
		}
	}

	public boolean isDone()
	{
		boolean lIsDone = true;
		if (mAngleFutureTask != null)
			lIsDone &= mAngleFutureTask.isDone();
		if (mDeltaZFutureTask != null)
			lIsDone &= mDeltaZFutureTask.isDone();
		return lIsDone;
	}

	public double[] getDeltaZAlphaBeta(double pMaxWaitTimeInSeconds) throws InterruptedException,
																																	ExecutionException
	{
		double dz = Double.NaN;
		double alpha = Double.NaN;
		double beta = Double.NaN;

		final Double lDZ = getDeltaZ(pMaxWaitTimeInSeconds);
		if (lDZ != null)
			dz = lDZ;

		final Double lAlpha = getAlpha(pMaxWaitTimeInSeconds);
		if (lAlpha != null)
			alpha = lAlpha;

		final Double lBeta = getAlpha(pMaxWaitTimeInSeconds);
		if (lBeta != null)
			beta = lBeta;

		final double[] lResult = new double[]
		{ dz, alpha, beta };

		info("getDeltaZAlphaBeta returns: %s\n", Arrays.toString(lResult));

		return lResult;
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

	public Double getAlpha(double pMaxWaitTimeInSeconds) throws InterruptedException,
																											ExecutionException
	{
		try
		{
			if (mAngleFutureTask == null)
				return null;

			final AngleTaskResult lAngleTaskResult = mAngleFutureTask.get((long) (1000 * pMaxWaitTimeInSeconds),
																																		TimeUnit.MILLISECONDS);
			if (lAngleTaskResult == null)
				return null;
			return lAngleTaskResult.alpha;
		}
		catch (final TimeoutException e)
		{
			return null;
		}
	}

	public Double getBeta(double pMaxWaitTimeInSeconds)	throws InterruptedException,
																											ExecutionException
	{
		try
		{
			if (mAngleFutureTask == null)
				return null;

			final AngleTaskResult lAngleTaskResult = mAngleFutureTask.get((long) (1000 * pMaxWaitTimeInSeconds),
																																		TimeUnit.MILLISECONDS);
			if (lAngleTaskResult == null)
				return null;
			return lAngleTaskResult.beta;
		}
		catch (final TimeoutException e)
		{
			return null;
		}
	}

	public Double getPValue(double pMaxWaitTimeInSeconds)	throws InterruptedException,
																												ExecutionException
	{
		try
		{
			final AngleTaskResult lAngleTaskResult = mAngleFutureTask.get((long) (1000 * pMaxWaitTimeInSeconds),
																																		TimeUnit.MILLISECONDS);
			if (lAngleTaskResult == null)
				return null;
			return lAngleTaskResult.pvalue;
		}
		catch (final TimeoutException e)
		{
			return null;
		}
	}

	private static final Logger mLogger = Logger.getLogger("autopilot");

	private static final void info(String format, Object... args)
	{
		mLogger.info(String.format(format, args));
		// System.out.format(format, args);
	}

}
