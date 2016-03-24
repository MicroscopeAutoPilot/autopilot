package autopilot.stackanalysis;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import rtlib.core.math.argmax.SmartArgMaxFinder;

public class DeltaZTask implements Callable<Double>
{

	private final ArrayList<FutureTask<ZPlaneAnalysisResult>> mZPlaneAnalysisFuturTaskList;
	private final double mProbabilityThreshold;

	public DeltaZTask(ArrayList<FutureTask<ZPlaneAnalysisResult>> pZPlaneAnalysisFuturTaskList,
										double pProbabilityThreshold)
	{
		mZPlaneAnalysisFuturTaskList = pZPlaneAnalysisFuturTaskList;
		mProbabilityThreshold = pProbabilityThreshold;

	}

	@Override
	public Double call() throws Exception
	{
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		info("starting deltaZ task\n");

		final TDoubleArrayList lZ = new TDoubleArrayList();
		final TDoubleArrayList lM = new TDoubleArrayList();
		for (final FutureTask<ZPlaneAnalysisResult> lTask : mZPlaneAnalysisFuturTaskList)
		{
			final ZPlaneAnalysisResult lZPlaneAnalysisResult = lTask.get();
			lZ.add(lZPlaneAnalysisResult.mZ);
			lM.add(lZPlaneAnalysisResult.mMetric);
			info(	"%g\t%g\n",
						lZPlaneAnalysisResult.mZ,
						lZPlaneAnalysisResult.mMetric);
		}

		final SmartArgMaxFinder lSmartArgMaxFinder = new SmartArgMaxFinder();

		final Double lArgMax = lSmartArgMaxFinder.argmax(	lZ.toArray(),
																											lM.toArray());
		final Double lFitProbability = lSmartArgMaxFinder.getLastFitProbability();

		if (lArgMax == null || lFitProbability == null)
			return null;
		if (lFitProbability < mProbabilityThreshold)
			return null;

		info("argmax: %g, fit prob: %g\n", lArgMax, lFitProbability);

		info("finished deltaZ task\n");
		return lArgMax;
	}

	private static final Logger mLogger = Logger.getLogger("autopilot");

	private static final void info(String format, Object... args)
	{
		mLogger.info(String.format(format, args));
		// System.out.format(format, args);
	}
}
