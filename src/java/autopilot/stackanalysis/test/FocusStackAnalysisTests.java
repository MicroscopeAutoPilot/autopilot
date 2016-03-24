package autopilot.stackanalysis.test;

import static java.lang.Math.abs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import autopilot.image.DoubleArrayImage;
import autopilot.image.readers.TiffReader;
import autopilot.stackanalysis.FocusStackAnalysis;
import rtlib.core.log.CompactFormatter;
import rtlib.core.log.gui.LogWindowHandler;

public class FocusStackAnalysisTests
{

	static final boolean cVisualize = true;

	@Before
	public void setUp()
	{
		final Logger lLogger = Logger.getLogger("autopilot");
		lLogger.setUseParentHandlers(false);
		/*final ConsoleHandler lConsoleHandler = new ConsoleHandler();
		lConsoleHandler.setFormatter(new CompactFormatter());
		lLogger.addHandler(lConsoleHandler);/**/

		//lLogger.addHandler(LogWindowHandler.getInstance("AutoPilot log"));
	}

	private static final double cAngleEqualityTolerance = 0.25;

	@Test
	public void testBasic()	throws IOException,
							InterruptedException,
							ExecutionException
	{

		{
			final String lFileName = "./stacks/TP=1171_Zi=0_LS=1.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100);
			if (cVisualize)
				Thread.sleep(1000000);

			assertEquals(-1.06, lResult[0], cAngleEqualityTolerance);
			assertEquals(-0.07, lResult[1], cAngleEqualityTolerance);
			assertEquals(1.82, lResult[2], cAngleEqualityTolerance);
		}

	}

	@Test
	public void testAngleAP()	throws IOException,
								InterruptedException,
								ExecutionException
	{
		{
			final String lFileName = "./stacks/A2B0P5.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100);
			if (cVisualize)
				Thread.sleep(1000000);

			assertEquals(-0.03, lResult[0], cAngleEqualityTolerance);
			assertEquals(-1.08, lResult[1], cAngleEqualityTolerance);
			assertEquals(-0.20, lResult[2], cAngleEqualityTolerance);
		}
	}

	@Test
	public void testAngleAM()	throws IOException,
								InterruptedException,
								ExecutionException
	{
		{
			final String lFileName = "./stacks/A-2B0P5.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100);
			if (cVisualize)
				Thread.sleep(1000000);

			assertEquals(-1.24, lResult[0], cAngleEqualityTolerance);
			assertEquals(2.5, lResult[1], cAngleEqualityTolerance);
			assertEquals(-0.49, lResult[2], cAngleEqualityTolerance);
		}
	}

	@Test
	public void testAngleBP()	throws IOException,
								InterruptedException,
								ExecutionException
	{
		{
			final String lFileName = "./stacks/A0B2P5.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100);
			if (cVisualize)
				Thread.sleep(1000000);

			assertEquals(-0.35, lResult[0], cAngleEqualityTolerance);
			assertEquals(0.60, lResult[1], cAngleEqualityTolerance);
			assertEquals(1.16, lResult[2], cAngleEqualityTolerance);
		}
	}

	@Test
	public void testAngleBM()	throws IOException,
								InterruptedException,
								ExecutionException
	{
		{
			final String lFileName = "./stacks/A0B-2P5.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100);
			if (cVisualize)
				Thread.sleep(1000000);

			assertEquals(-0.05, lResult[0], cAngleEqualityTolerance);
			assertEquals(0.6, lResult[1], cAngleEqualityTolerance);
			assertEquals(-2.81, lResult[2], cAngleEqualityTolerance);
		}
	}

	@Test
	public void testAngleADiff() throws IOException,
								InterruptedException,
								ExecutionException
	{

		final String lA2B0 = "./stacks/A2B0P5.tif";
		final String lAm2B0 = "./stacks/A-2B0P5.tif";

		final Double[] lA2B0Result = analyse(lA2B0, cVisualize, 100);
		final Double[] lAm2B0Result = analyse(lAm2B0, cVisualize, 100);

		final double u = lA2B0Result[1];
		final double v = lAm2B0Result[1];
		final double diff = abs(u - v);

		System.out.println("diff=" + diff);
		assertTrue(u * v < 0);
		assertTrue(diff > 2.8);

		if (cVisualize)
			Thread.sleep(1000000);
	}

	@Test
	public void testAngleBDiff() throws IOException,
								InterruptedException,
								ExecutionException
	{

		final String lA0B2 = "./stacks/A0B2P5.tif";
		final String lA0Bm2 = "./stacks/A0B-2P5.tif";

		final Double[] lA0B2Result = analyse(lA0B2, cVisualize, 100);
		final Double[] lA0Bm2Result = analyse(lA0Bm2, cVisualize, 100);

		final double u = lA0B2Result[2];
		final double v = lA0Bm2Result[2];
		final double diff = abs(u - v);

		System.out.println("diff=" + diff);
		assertTrue(u * v < 0);
		assertTrue(diff > 3.4);

		if (cVisualize)
			Thread.sleep(1000000);
	}

	@Test
	public void testExtremalPlaneAngleAP()	throws IOException,
											InterruptedException,
											ExecutionException
	{
		// for (int i = 0; i < 4; i++)
		{
			final String lFileName = "./stacks/A2B0P7.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100);
			if (cVisualize)
				Thread.sleep(1000000);

			assertEquals(-0.22, lResult[0], cAngleEqualityTolerance);
			assertEquals(-1.42, lResult[1], cAngleEqualityTolerance);
			assertEquals(-1, lResult[2], cAngleEqualityTolerance);
		}
	}

	@Test
	public void testExtremalPlaneAngleAN()	throws IOException,
											InterruptedException,
											ExecutionException
	{
		{
			final String lFileName = "./stacks/A-2B0P7.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100);
			if (cVisualize)
				Thread.sleep(1000000);

			assertNull(lResult[0]);
			assertEquals(2.25, lResult[1], cAngleEqualityTolerance);
			assertEquals(-1.62, lResult[2], cAngleEqualityTolerance);
		}
	}

	@Test
	public void testExtremalPlaneAngleB()	throws IOException,
											InterruptedException,
											ExecutionException
	{
		{
			final String lFileName = "./stacks/A0B-2P7.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100);
			if (cVisualize)
				Thread.sleep(1000000);

			assertEquals(-0.09, lResult[0], cAngleEqualityTolerance);
			assertEquals(	0.65,
							lResult[1],
							0.3 + cAngleEqualityTolerance);
			assertEquals(	-1.37,
							lResult[2],
							0.3 + cAngleEqualityTolerance);
		}
	}

	@Test
	public void testMiddlePlaneAngleAN() throws IOException,
										InterruptedException,
										ExecutionException
	{
		{
			final String lFileName = "./stacks/A-1B0P0.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100);
			if (cVisualize)
				Thread.sleep(1000000);

			assertEquals(-0.0, lResult[0], cAngleEqualityTolerance);
			assertEquals(-1.05, lResult[1], cAngleEqualityTolerance);
			assertEquals(0.75, lResult[2], cAngleEqualityTolerance);
		}
	}

	@Test
	public void testOrientationCW90()	throws IOException,
										InterruptedException,
										ExecutionException
	{
		{
			final String lFileName = "./stacks/A0B-1P2.cw90.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100,
												true);
			if (cVisualize)
				Thread.sleep(1000000);

			assertEquals(-0.47, lResult[0], cAngleEqualityTolerance);
			assertEquals(0.30, lResult[1], cAngleEqualityTolerance);
			assertEquals(0.1, lResult[2], cAngleEqualityTolerance);
		}
	}

	@Test
	public void testProblem1()	throws IOException,
								InterruptedException,
								ExecutionException
	{
		{
			final String lFileName = "./stacks/A-0.5B0P0.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100);
			if (cVisualize)
				Thread.sleep(1000000);

			assertEquals(-0.20, lResult[0], cAngleEqualityTolerance);
			assertEquals(-0.86, lResult[1], cAngleEqualityTolerance);
			assertEquals(0.78, lResult[2], cAngleEqualityTolerance);
		}
	}

	@Test
	public void testProblemFindsWrongSubPlane()	throws IOException,
												InterruptedException,
												ExecutionException
	{
		{
			final String lFileName = "./stacks/A0B-1P5.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100,
												true);
			if (cVisualize)
				Thread.sleep(1000000);

			assertEquals(-0.18, lResult[0], cAngleEqualityTolerance);
			assertEquals(-0.56, lResult[1], cAngleEqualityTolerance);
			assertEquals(1.79, lResult[2], cAngleEqualityTolerance);
		}
	}

	@Test
	public void testProblemTooManyPointsFilteredOut()	throws IOException,
														InterruptedException,
														ExecutionException
	{
		// for (int i = 0; i < 100; i++)
		{
			final String lFileName = "./stacks/A1B0P5.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100,
												true);
			if (cVisualize)
				Thread.sleep(1000000);

			// assertNull(lResult);
			// assertEquals(-0.20, lResult[0], cAngleEqualityTolerance);
			if (lResult != null)
			{
				assertEquals(null, lResult[1]);
				assertEquals(null, lResult[2]);
			}
		}
	}

	@Test
	public void testProblemExtremalPlaneCompletelyWrongAngles()	throws IOException,
																InterruptedException,
																ExecutionException
	{
		for (int i = 0; i < 3; i++)
		{
			final String lFileName = "./stacks/A0B-0.5P7.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100,
												true);
			if (cVisualize)
				Thread.sleep(1000000);

			// assertEquals(-0.20, lResult[0], cAngleEqualityTolerance);
			assertEquals(-0.23, lResult[1], cAngleEqualityTolerance);
			assertEquals(1.20, lResult[2], cAngleEqualityTolerance);
		}
	}

	@Test
	public void testProblemExtremalPlaneStrongBetaAngle()	throws IOException,
															InterruptedException,
															ExecutionException
	{
		// for (int i = 0; i < 100; i++)
		{
			final String lFileName = "./stacks/A0B0.5P7.tif";

			final Double[] lResult = analyse(	lFileName,
												cVisualize,
												100,
												true);
			if (cVisualize)
				Thread.sleep(1000000);

			// assertEquals(-0.20, lResult[0], cAngleEqualityTolerance);
			assertEquals(-0.14, lResult[1], cAngleEqualityTolerance);
			assertEquals(4.15, lResult[2], cAngleEqualityTolerance);
		}
	}

	@Test
	public void testInternalAngle()	throws IOException,
									InterruptedException,
									ExecutionException
	{

		final String lFileName = "./stacks/Plane3outof18.tif";

		final Double[] lResult = analyse(	lFileName,
											21,
											0.8,
											cVisualize,
											100);

		if (true)
			Thread.sleep(1000000);
	}

	

	/*_______________________________________________________________________________________*/

	@Test
	public void testZbenchmark() throws IOException,
								InterruptedException,
								ExecutionException
	{
		final File lFolder = new File("/Users/royer/Projects/AutoPilot/datasets/angles2");
		if (!lFolder.exists())
			return;

		final File[] lListOfFiles = lFolder.listFiles();

		final ArrayList<File> lSelectedFileList = new ArrayList<File>();

		for (final File lFile : lListOfFiles)
			if (lFile.isFile() && lFile.getName()
										.toLowerCase()
										.contains(".tif"))
			{
				lSelectedFileList.add(lFile);
				System.out.println(lFile);
			}

		final StringBuilder lStringBuilder = new StringBuilder();

		for (final File lFile : lSelectedFileList)
		{
			final boolean lVisualize = false;

			final FocusStackAnalysis lFocusStackAnalysis = new FocusStackAnalysis();
			lFocusStackAnalysis.setBooleanParameter("visualize",
													lVisualize);
			lFocusStackAnalysis.setDoubleParameter("orientation", 1);

			final double[] lDefocusZInMicrons = new double[]
			{	-3 * 1.33,
				-2 * 1.33,
				-1.33,
				0,
				1.33,
				2 * 1.33,
				3 * 1.33 };

			lFocusStackAnalysis.loadPlanes(lDefocusZInMicrons, lFile);

			final int lWaitTimeInSeconds = 100;
			final Double lDeltaZ = lFocusStackAnalysis.getDeltaZ(lWaitTimeInSeconds);
			final Double lAlpha = lFocusStackAnalysis.getAlpha(lWaitTimeInSeconds);
			final Double lBeta = lFocusStackAnalysis.getBeta(lWaitTimeInSeconds);
			final Double lPValue = lFocusStackAnalysis.getPValue(lWaitTimeInSeconds);

			final String lLine = String.format(	"%s\t%g\t%g\t%g\t%g\n",
												lFile.getName(),
												lDeltaZ,
												lAlpha,
												lBeta,
												lPValue);
			lStringBuilder.append(lLine);
		}

		String lReport = lStringBuilder.toString();

		lReport = lReport.replaceAll("A", "\t")
							.replaceAll("B", "\t")
							.replaceAll("P", "\t")
							.replaceAll(".tif", "");

		System.out.println(lReport);

	}

	@Test
	public void testSpeed()	throws IOException,
							InterruptedException,
							ExecutionException
	{
		final Logger lLogger = Logger.getLogger("autopilot");
		lLogger.setUseParentHandlers(false);

		final String lFileName = "./stacks/TP=1171_Zi=0_LS=1.tif";
		final int lWaitTimeInSeconds = 100;

		final int lNumberOfPlanes = TiffReader.nbpages(FocusStackAnalysisTests.class.getResourceAsStream(lFileName));
		System.out.println("Number of planes in stack: " + lNumberOfPlanes);

		final DoubleArrayImage[] lDoubleArrayImages = new DoubleArrayImage[lNumberOfPlanes];
		for (int i = 0; i < lNumberOfPlanes; i++)
		{
			lDoubleArrayImages[i] = TiffReader.read(FocusStackAnalysisTests.class.getResourceAsStream(lFileName),
													i,
													null);
		}

		double lLastElapsedTimeInSeconds = Double.POSITIVE_INFINITY;
		for (int r = 0; r < 100; r++)
		{
			final DoubleArrayImage[] lDoubleArrayImagesCopy = new DoubleArrayImage[lNumberOfPlanes];
			for (int i = 0; i < lNumberOfPlanes; i++)
				lDoubleArrayImagesCopy[i] = lDoubleArrayImages[i].copy();

			final long lStartTimeNs = System.nanoTime();

			final FocusStackAnalysis lFocusStackAnalysis = new FocusStackAnalysis();
			lFocusStackAnalysis.setBooleanParameter("visualize",
													false);

			final double[] lDefocusZInMicrons = new double[]
			{	-3 * 1.33,
				-2 * 1.33,
				-1.33,
				0,
				1.33,
				2 * 1.33,
				3 * 1.33 };

			lFocusStackAnalysis.loadPlanes(	lDefocusZInMicrons,
											lDoubleArrayImagesCopy);

			final Double lDeltaZ = lFocusStackAnalysis.getDeltaZ(lWaitTimeInSeconds);
			final Double lAlpha = lFocusStackAnalysis.getAlpha(lWaitTimeInSeconds);
			final Double lBeta = lFocusStackAnalysis.getBeta(lWaitTimeInSeconds);

			final long lStopTimeNs = System.nanoTime();
			lLastElapsedTimeInSeconds = 0.001 * 0.001 * 0.001 * (lStopTimeNs - lStartTimeNs);
			System.out.format(	"Elapsed time in seconds: %g \n",
								lLastElapsedTimeInSeconds);
			assertEquals(-1.06, lDeltaZ, cAngleEqualityTolerance);
			assertEquals(-0.08, lAlpha, cAngleEqualityTolerance);
			assertEquals(1.70, lBeta, 0.1 + cAngleEqualityTolerance);
		}

		System.out.format(	"Elapsed time in seconds: %g \n",
							lLastElapsedTimeInSeconds);

		assertTrue(lLastElapsedTimeInSeconds < 1.5);

	}

	// #####################################################################################

	private Double[] analyse(	final String lFileName,
								final boolean lVisualize,
								final int pWaitTimeInSeconds)	throws IOException,
																InterruptedException,
																ExecutionException
	{
		return analyse(	lFileName,
						7,
						1.33,
						lVisualize,
						pWaitTimeInSeconds,
						false);
	}

	private Double[] analyse(	final String lFileName,
								final int pNumberOfImages,
								final double pStepSize,
								final boolean lVisualize,
								final int pWaitTimeInSeconds)	throws IOException,
																InterruptedException,
																ExecutionException
	{
		return analyse(	lFileName,
						pNumberOfImages,
						pStepSize,
						lVisualize,
						pWaitTimeInSeconds,
						false);
	}

	private Double[] analyse(	final String lFileName,
								final boolean lVisualize,
								final int pWaitTimeInSeconds,
								boolean pTurnCW90)	throws IOException,
													InterruptedException,
													ExecutionException
	{
		return analyse(	lFileName,
						7,
						1.33,
						lVisualize,
						pWaitTimeInSeconds,
						pTurnCW90);
	}

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
										FocusStackAnalysisTests.class,
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
