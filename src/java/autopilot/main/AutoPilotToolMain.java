package autopilot.main;

import static java.lang.Math.abs;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import rtlib.core.log.CompactFormatter;
import rtlib.core.log.gui.LogWindowHandler;
import autopilot.image.DoubleArrayImage;
import autopilot.image.readers.TiffReader;
import autopilot.image.readers.TiffReader.TiffStackCallBack;
import autopilot.image.writers.TiffWriter;
import autopilot.measures.benchmark.BenchmarkMeasures;
import autopilot.measures.implementations.resolution.DFTResolutionMeasure;
import autopilot.stackanalysis.FocusStackAnalysis;
import autopilot.stackanalysis.generic.GenericFocusStackAnalysis;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.formats.FormatException;

public class AutoPilotToolMain
{
	private static final int cWaitTimeInSeconds = 100;
	private static final boolean cVisualize = true;
	private static double mStep = 1.72;

	public static void main(String[] args)
	{

		try
		{
			final CommandLine lCommandLineValues = new CommandLine();
			final CmdLineParser lCmdLineParser = new CmdLineParser(lCommandLineValues);
			lCmdLineParser.getProperties().withUsageWidth(80);

			System.out.println("-~==============================~-");
			System.out.println("AutoPilot Command Line Tool       ");
			System.out.println("Loic Royer 2015 (royer@mpi-cbg.de)");
			try
			{
				lCmdLineParser.parseArgument(args);
			}
			catch (final CmdLineException e)
			{
				System.err.println(e.getMessage());
				lCmdLineParser.printUsage(System.err);
				System.err.println();
				System.exit(1);
			}

			final Logger lLogger = Logger.getLogger("autopilot");
			lLogger.setUseParentHandlers(false);

			if (lCommandLineValues.mVerboseLog)
			{
				final ConsoleHandler lConsoleHandler = new ConsoleHandler();
				lConsoleHandler.setFormatter(new CompactFormatter());
				lLogger.addHandler(lConsoleHandler);
			}

			if (lCommandLineValues.mOpenLogWindow)
			{
				lLogger.addHandler(LogWindowHandler.getInstance("AutoPilot log"));
			}

			if (lCommandLineValues.mComputeAngles)
			{
				computeangles(lCommandLineValues);
			}
			else if (lCommandLineValues.mXYStackFFT)
			{
				xystackfft(lCommandLineValues);
			}
			else if (lCommandLineValues.mComputeBenchmark && lCommandLineValues.isFolder())
			{
				benchmark(lCommandLineValues);
			}
			else if (lCommandLineValues.mEstimateResolution && !lCommandLineValues.isFolder())
			{
				estimateResolution(lCommandLineValues);
			}
			else if (!lCommandLineValues.isFolder())
			{
				analyseStack(lCommandLineValues);
			}

			if (!lCommandLineValues.mKeepAlive)
				System.exit(0);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}

	}

	private static void xystackfft(CommandLine pCommandLineValues)
	{
		final File lFileOrFolder = pCommandLineValues.getFile();
		if (!lFileOrFolder.exists())
		{
			System.out.format(	"Path %s does not exist!",
								lFileOrFolder.getAbsolutePath());
			return;
		}

		final File[] lListOfFiles;
		final File lResutFolder;

		if (lFileOrFolder.isDirectory())
		{
			lListOfFiles = lFileOrFolder.listFiles();
			lResutFolder = new File(lFileOrFolder, "fft");
			System.out.format(	"Loading and processing stacks in folder: '%s'...\n",
								lFileOrFolder.getName());
		}
		else
		{
			lListOfFiles = new File[]
			{ lFileOrFolder };
			lResutFolder = new File(lFileOrFolder.getParentFile(),
									"fft");
			System.out.format(	"Loading and processing stack in file: '%s'...\n",
								lFileOrFolder.getName());
		}

		lResutFolder.mkdirs();

		for (File lTiffFile : lListOfFiles)
			if (lTiffFile.getName().endsWith(".tif") || lTiffFile.getName()
																	.endsWith(".tiff"))
			{
				xystackfft(	pCommandLineValues,
							lResutFolder,
							lTiffFile);
			}

	}

	private static void xystackfft(	CommandLine pCommandLineValues,
									final File pResutFolder,
									File lTiffFile)
	{
		try
		{
			System.out.format(	"processing file: %s \n",
								lTiffFile.getName());
			System.out.format("plane index\tOTF support radius\n");

			ArrayList<DoubleArrayImage> lImageList = new ArrayList<DoubleArrayImage>();

			TiffReader.readTiffStack(	lTiffFile,
										new TiffStackCallBack()
										{
											@Override
											public boolean image(	final int pImageIndex,
																	final DoubleArrayImage pDoubleArrayImage)
											{

												pDoubleArrayImage.fftLogPower();

												final double[] lBaseAndMaxLevels = DFTResolutionMeasure.computeBaseLevel(	pDoubleArrayImage,
																															pCommandLineValues.mPSFSupportDiameter);
												final double lBaseLevel = lBaseAndMaxLevels[0];
												final double lMaxLevel = lBaseAndMaxLevels[1];

												DFTResolutionMeasure.removeArtifacts(	pDoubleArrayImage,
																						2,
																						lBaseLevel);

												if (abs(lMaxLevel - lBaseLevel) <= 0)
													pDoubleArrayImage.set(0);
												else
													pDoubleArrayImage.map(	lBaseLevel,
																			lMaxLevel,
																			0,
																			1);

												double lSupport = DFTResolutionMeasure.computeSupport(	pDoubleArrayImage,
																										128,
																										0.05);

												System.out.format(	"%d\t%f\n",
																	pImageIndex,
																	lSupport * 0.5
																			* pDoubleArrayImage.getWidth());

												pDoubleArrayImage.round(6);

												// System.out.println("min:"+pDoubleArrayImage.min());
												// System.out.println("max:"+pDoubleArrayImage.max());

												lImageList.add(pDoubleArrayImage.copy());

												return true;
											}
										},
										null);

			String lName = lTiffFile.getName();
			File lFile = new File(pResutFolder, lName);

			TiffWriter.write(lImageList, lFile);
		}
		catch (IOException | DependencyException | ServiceException
				| FormatException e)
		{
			e.printStackTrace();
		}
	}

	private static void estimateResolution(CommandLine pCommandLineValues) throws IOException
	{
		final File lFile = pCommandLineValues.getFile();
		System.out.format(	"Loading and processing stack '%s'...\n",
							lFile.getName());

		System.out.format("image index\tk space resolution estimation\tk space resolution estimate(2x)\tresolution\n");
		TiffReader.readTiffStack(lFile, new TiffStackCallBack()
		{
			@Override
			public boolean image(	final int pImageIndex,
									final DoubleArrayImage pDoubleArrayImage)
			{
				// System.out.format("ImageIndex=%d \n", pImageIndex);
				/*System.out.format("estimating resolution of image plane %d \n",
													pImageIndex);/**/

				final double lResolutionMeasure = DFTResolutionMeasure.compute(	pDoubleArrayImage,
																				pCommandLineValues.mPSFSupportDiameter,
																				pCommandLineValues.mNumberOfbins);

				System.out.format(	"%d\t%g\t%g\t%g\n",
									pImageIndex,
									lResolutionMeasure,
									lResolutionMeasure * 2,
									pCommandLineValues.mLateralResolutionUm / lResolutionMeasure);

				return true;
			}
		},
									null);

	}

	private static void analyseStack(CommandLine pCommandLineValues) throws InterruptedException,
																	ExecutionException,
																	IOException
	{
		final GenericFocusStackAnalysis lGenericFocusStackAnalysis = new GenericFocusStackAnalysis();

		lGenericFocusStackAnalysis.setDoubleParameter(	"psfsupportdiameter[px]",
														pCommandLineValues.mPSFSupportDiameter);
		lGenericFocusStackAnalysis.setDoubleParameter(	"mFitProbability",
														pCommandLineValues.mFitProbability);

		final File lFile = pCommandLineValues.getFile();

		final int lNumberOfPlanes = TiffReader.nbpages(lFile);
		final double[] lDefocusZInMicrons = new double[lNumberOfPlanes];
		for (int i = 0; i < lNumberOfPlanes; i++)
			lDefocusZInMicrons[i] = i * pCommandLineValues.mFocusZStep;

		System.out.format(	"Loading and processing stack '%s'...\n",
							lFile.getName());
		lGenericFocusStackAnalysis.loadPlanes(	lDefocusZInMicrons,
												lFile);
		System.out.println("done!");

		final Double lDeltaZ = lGenericFocusStackAnalysis.getDeltaZ(100);
		System.out.println("focus= " + lDeltaZ);

		if (pCommandLineValues.mComputeFocusCurve)
		{
			System.out.println("Focus curve:");
			final double[] lFocusCurve = lGenericFocusStackAnalysis.getFocusCurve();

			System.out.format("____________________________\n");
			System.out.format("z\tdcts\n");
			int i = 0;
			for (final double lFocusMeasure : lFocusCurve)
			{
				System.out.format(	"%g\t%g\n",
									lDefocusZInMicrons[i++],
									lFocusMeasure);
			}
			System.out.println("done!");
		}

		if (pCommandLineValues.mComputeFocusMap)
		{
			System.out.println("Generating focus map...");
			final File lSVGFile = new File(lFile.getAbsolutePath() + ".svg");
			lGenericFocusStackAnalysis.getTileFocusSVG(lSVGFile, 0.5);
			System.out.format("File '%s' saved!", lSVGFile.getName());
		}

	}

	private static void benchmark(CommandLine pCommandLineValues) throws IOException
	{
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		final File lFolder = pCommandLineValues.getFile();
		System.out.format(	"Running mComputeBenchmark on folder: '%s'\n",
							lFolder);
		final File lResultFolder = new File(lFolder, "result");
		lResultFolder.mkdirs();

		final String lFileFilter = pCommandLineValues.mFileFilter;
		final String lMeasureFilter = pCommandLineValues.mMeasureFilter;

		final BenchmarkMeasures lBenchmarkMeasures = new BenchmarkMeasures(	lFolder,
																			lFileFilter,
																			lMeasureFilter,
																			lResultFolder,
																			false);
		lBenchmarkMeasures.setPSFSupportDiameter((int) pCommandLineValues.mPSFSupportDiameter);
		lBenchmarkMeasures.run();
		System.out.format("Done!\n", lFolder);
	}

	private static void computeangles(CommandLine pCommandLineValues)	throws IOException,
																		InterruptedException,
																		ExecutionException
	{
		final File lFileOrFolder = pCommandLineValues.getFile();
		if (!lFileOrFolder.exists())
		{
			System.out.format(	"Path %s does not exist!",
								lFileOrFolder.getAbsolutePath());
			return;
		}

		final File[] lListOfFiles;

		if (lFileOrFolder.isDirectory())
		{
			lListOfFiles = lFileOrFolder.listFiles();
		}
		else
		{
			lListOfFiles = new File[]
			{ lFileOrFolder };
		}

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
			final FocusStackAnalysis lFocusStackAnalysis = new FocusStackAnalysis();
			lFocusStackAnalysis.setDoubleParameter(	"psfsupportdiameter[px]",
													pCommandLineValues.mPSFSupportDiameter);
			lFocusStackAnalysis.setBooleanParameter("visualize",
													pCommandLineValues.mVisualise);
			lFocusStackAnalysis.setDoubleParameter(	"orientation",
													pCommandLineValues.mOrientation);

			final int lNumberOfPlanes = TiffReader.nbpages(lFile);
			final double[] lDefocusZInMicrons = new double[lNumberOfPlanes];
			for (int i = 0; i < lNumberOfPlanes; i++)
				lDefocusZInMicrons[i] = i * pCommandLineValues.mFocusZStep;

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
			System.out.println(lLine);
			lStringBuilder.append(lLine);
		}

		final String lReport = lStringBuilder.toString();

		System.out.println(lReport);
	}

}
