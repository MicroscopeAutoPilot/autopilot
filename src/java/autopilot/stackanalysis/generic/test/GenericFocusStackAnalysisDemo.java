package autopilot.stackanalysis.generic.test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.junit.Test;

import autopilot.stackanalysis.ZPlaneAnalysisResult;
import autopilot.stackanalysis.generic.GenericFocusStackAnalysis;

public class GenericFocusStackAnalysisDemo
{

	private static final boolean cVisualize = true;

	@Test
	public void testBock() throws IOException,
												InterruptedException,
												ExecutionException
	{

		final String lFileName = "/Users/royer/Projects/AutoPilot/datasets/bock/36070_n=40_s=2.000_f=21.aligned.cropped.tif"; // .aligned.cropped.TEST
		final File lFile = new File(lFileName);

		if (lFile.exists())
		{
			final GenericFocusStackAnalysis lAnalyse = analyse(	lFile,
																													40,
																													cVisualize,
																													100);

			for (final FutureTask<ZPlaneAnalysisResult> lFutureTask : lAnalyse.getZPlaneAnalysisFuturTaskList())
			{
				final ZPlaneAnalysisResult lZPlaneAnalysisResult = lFutureTask.get();

				System.out.println(lZPlaneAnalysisResult.mVList);
			}

			final File lSVGFile = new File("/Users/royer/Projects/AutoPilot/datasets/bock/demo.svg");
			lAnalyse.getTileFocusSVG(lSVGFile, 0.5);

			final double[] lFocusCurve = lAnalyse.getFocusCurve();

			for (final double lFocusMeasure : lFocusCurve)
			{
				System.out.println(lFocusMeasure);
			}

		}
		else
			System.err.println("PROBLEM!!");

	}

	// #####################################################################################

	private GenericFocusStackAnalysis analyse(final File pFile,
														final int pNumberOfPlanes,
														final boolean lVisualize,
														final int pWaitTimeInSeconds)	throws IOException,
																													InterruptedException,
																													ExecutionException
	{

		final GenericFocusStackAnalysis lGenericFocusStackAnalysis = new GenericFocusStackAnalysis();
		lGenericFocusStackAnalysis.setBooleanParameter(	"visualize",
																										lVisualize);

		final double[] lDefocusZInMicrons = new double[pNumberOfPlanes];
		for (int i = 0; i < pNumberOfPlanes; i++)
			lDefocusZInMicrons[i] = i;

		lGenericFocusStackAnalysis.loadPlanes(lDefocusZInMicrons, pFile);

		final Double lDeltaZ = lGenericFocusStackAnalysis.getDeltaZ(pWaitTimeInSeconds);

		System.out.println("lDeltaZ=" + lDeltaZ);

		System.out.println("DONE!");
		if (lVisualize)
			Thread.sleep(4000);

		return lGenericFocusStackAnalysis;
	}

}
