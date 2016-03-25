package autopilot.stackanalysis.demo;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.StatUtils;
import org.junit.Before;
import org.junit.Test;

import autopilot.utils.rtlib.core.log.gui.LogWindowHandler;
import autopilot.stackanalysis.FocusStackAnalysis;
import gnu.trove.list.array.TDoubleArrayList;

public class StabilityTests
{

	static final boolean cVisualize = false;

	@Before
	public void setUp()
	{
		final Logger lLogger = Logger.getLogger("autopilot");
		lLogger.setUseParentHandlers(false);
		/*final ConsoleHandler lConsoleHandler = new ConsoleHandler();
		lConsoleHandler.setFormatter(new CompactFormatter());
		lLogger.addHandler(lConsoleHandler);/**/

		lLogger.addHandler(LogWindowHandler.getInstance("AutoPilot log"));
	}

	private static final double cAngleEqualityTolerance = 0.25;

	@Test
	public void testStability()	throws IOException,
								InterruptedException,
								ExecutionException
	{
		int lNumberOfImages = 11;
		double lStepSize = 0.8;
		double lWaitTimeInSeconds = 20;
		boolean lVisualize = true;
		File lFolder = new File("/Users/royer/Projects/AutoPilot/datasets/StabilityTest");
		File[] lFileList = lFolder.listFiles();

		TDoubleArrayList lAlphaList = new TDoubleArrayList();
		TDoubleArrayList lBetaList = new TDoubleArrayList();
		TDoubleArrayList lAlphaOldList = new TDoubleArrayList();
		TDoubleArrayList lBetaOldList = new TDoubleArrayList();

		for (File lTiffFile : lFileList)
			if (lTiffFile.getName().endsWith(".tif"))
				if (lTiffFile.getName().contains("Sum"))
				{
					String lTiffFilePath = lTiffFile.getAbsolutePath();
					System.out.println(lTiffFilePath);

					final FocusStackAnalysis lFocusStackAnalysis = new FocusStackAnalysis();
					lFocusStackAnalysis.setBooleanParameter("visualize",
															lVisualize);

					final double[] lDefocusZInMicrons = new double[lNumberOfImages];

					for (int i = 1; i <= lNumberOfImages / 2; i++)
					{
						lDefocusZInMicrons[lNumberOfImages / 2 + i] = i	* lStepSize;
						lDefocusZInMicrons[lNumberOfImages / 2 - i] = -i * lStepSize;
					}

					lFocusStackAnalysis.loadPlanes(	lDefocusZInMicrons,
													lTiffFile);

					final Double lDeltaZ = lFocusStackAnalysis.getDeltaZ(lWaitTimeInSeconds);
					final Double lAlpha = lFocusStackAnalysis.getAlpha(lWaitTimeInSeconds);
					final Double lBeta = lFocusStackAnalysis.getBeta(lWaitTimeInSeconds);

					// System.out.println("lDeltaZ=" + lDeltaZ);
					// System.out.println("lAlpha=" + lAlpha);
					// System.out.println("lBeta=" + lBeta);

					String[] lSplit = lTiffFile.getName()
												.replace(".tif", "")
												.split("_");

					Double lOldAlpha = Double.parseDouble(lSplit[2]);
					Double lOldBeta = Double.parseDouble(lSplit[3]);

					if (lAlpha == null || lBeta == null)
					{
						System.out.println("PROBLEM");
					}
					else
					{

						lAlphaList.add(lAlpha);
						lBetaList.add(lBeta);
						lAlphaOldList.add(lOldAlpha);
						lBetaOldList.add(lOldBeta);

						System.out.format(	"%g\t%g\t%s\t%s\n",
											lOldAlpha==null?0:lOldAlpha,
											lAlpha,
											lOldBeta==null?0:lOldBeta,
											lBeta);
					}
				}

		System.out.format(	"old alpha variance=%g\n",
							sqrt(StatUtils.variance(lAlphaOldList.toArray())));
		System.out.format(	"alpha     variance=%g\n",
							sqrt(StatUtils.variance(lAlphaList.toArray())));

		System.out.format(	"old beta  variance=%g\n",
							sqrt(StatUtils.variance(lBetaOldList.toArray())));
		System.out.format(	"beta      variance=%g\n",
							sqrt(StatUtils.variance(lBetaList.toArray())));
		
		
		if(lVisualize)
			Thread.sleep(1000000);

	}

	/*_______________________________________________________________________________________*/

	// #####################################################################################

	private Double[] analyse(	final String lFileName,
								final int pNumberOfImages,
								final double pStepSize,
								final boolean lVisualize,
								final int pWaitTimeInSeconds,
								boolean pTurnCW90)	throws IOException,
													InterruptedException,
													ExecutionException
	{
		final FocusStackAnalysis lFocusStackAnalysis = new FocusStackAnalysis();
		lFocusStackAnalysis.setBooleanParameter("visualize",
												lVisualize);
		if (pTurnCW90)
			lFocusStackAnalysis.setDoubleParameter("orientation", 1);

		final double[] lDefocusZInMicrons = new double[pNumberOfImages];

		for (int i = 1; i <= pNumberOfImages / 2; i++)
		{
			lDefocusZInMicrons[pNumberOfImages / 2 + i] = i	* pStepSize;
			lDefocusZInMicrons[pNumberOfImages / 2 - i] = -i * pStepSize;
		}

		lFocusStackAnalysis.loadPlanes(	lDefocusZInMicrons,
										StabilityTests.class,
										lFileName);

		final Double lDeltaZ = lFocusStackAnalysis.getDeltaZ(pWaitTimeInSeconds);
		final Double lAlpha = lFocusStackAnalysis.getAlpha(pWaitTimeInSeconds);
		final Double lBeta = lFocusStackAnalysis.getBeta(pWaitTimeInSeconds);

		System.out.println("lDeltaZ=" + lDeltaZ);
		System.out.println("lAlpha=" + lAlpha);
		System.out.println("lBeta=" + lBeta);

		System.out.println("DONE!");
		if (lVisualize)
			Thread.sleep(4000);

		return new Double[]
		{ lDeltaZ, lAlpha, lBeta };
	}

}
