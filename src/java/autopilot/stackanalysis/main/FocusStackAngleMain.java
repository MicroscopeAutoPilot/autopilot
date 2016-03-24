package autopilot.stackanalysis.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import autopilot.image.DoubleArrayImage;
import autopilot.image.readers.TiffReader;
import autopilot.stackanalysis.FocusStackAnalysis;

public class FocusStackAngleMain
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

			System.out.println("AutoPilot - Focus Stack angle computation");
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

			if (lCommandLineValues.isFolder())
			{
				processFolder(lCommandLineValues);
			}
			else
			{
				// for (int r = 0; r < 1000; r++)
				processFile(lCommandLineValues);
			}

		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}

	}

	private static void processFile(CommandLine pCommandLineValues)	throws IOException,
																																	InterruptedException,
																																	ExecutionException
	{
		System.out.println("File path: " + pCommandLineValues.mFolderOrFilePath);
		final File lTIFFFile = pCommandLineValues.getFile();
		processFile(pCommandLineValues, lTIFFFile);
	}

	private static String processFile(CommandLine pCommandLineValues,
																		File lTIFFFile)	throws IOException,
																										InterruptedException,
																										ExecutionException
	{

		final FocusStackAnalysis lFocusStackAnalysis = new FocusStackAnalysis();
		lFocusStackAnalysis.setBooleanParameter("visualize", cVisualize);

		final int lNumberOfPlanes = TiffReader.nbpages(lTIFFFile);

		final double[] lDefocusZInMicrons = new double[lNumberOfPlanes];

		for (int i = 0; i < lNumberOfPlanes; i++)
			lDefocusZInMicrons[i] = (i - ((lNumberOfPlanes - 1) / 2)) * mStep;

		final DoubleArrayImage[] lDoubleArrayImages = new DoubleArrayImage[lNumberOfPlanes];
		lFocusStackAnalysis.setDoubleParameter(	"nbplanes",
																						lNumberOfPlanes);
		for (int i = 0; i < lNumberOfPlanes; i++)
			lDoubleArrayImages[i] = TiffReader.read(lTIFFFile, i, null);

		// lFocusStackAnalysis.loadPlanes(lDefocusZInMicrons, lTIFFFile);

		final long lStartTimeNs = System.nanoTime();

		for (int i = 0; i < lNumberOfPlanes; i++)
			lFocusStackAnalysis.loadPlane(lDefocusZInMicrons[i],
																		lDoubleArrayImages[i]);

		final Double lDeltaZ = lFocusStackAnalysis.getDeltaZ(cWaitTimeInSeconds);
		//System.out.println("lDeltaZ=" + lDeltaZ);

		final Double lAlpha = lFocusStackAnalysis.getAlpha(cWaitTimeInSeconds);
		//System.out.println("lAlpha=" + lAlpha);

		final Double lBeta = lFocusStackAnalysis.getBeta(cWaitTimeInSeconds);
		//System.out.println("lBeta=" + lBeta);

		final String lReport = String.format(	"%s\t%g\t%g\t%g\n",
																					lTIFFFile.getName(),
																					lDeltaZ == null	? Double.NaN
																													: lDeltaZ,
																					lAlpha == null ? Double.NaN
																												: lAlpha,
																					lBeta == null	? Double.NaN
																												: lBeta);

		//System.out.println(lReport);

		final long lStopTimeNs = System.nanoTime();
		final double lElapsedTimeInSeconds = 0.001 * 0.001 * 0.001 * (lStopTimeNs - lStartTimeNs);
		//System.out.format("elapsed time: %g s \n", lElapsedTimeInSeconds);

		return lReport;
	}

	private static double[] buildFocusStackArray(	int pSize,
																								double pFocusZStep)
	{
		final double[] lFocusStackArray = new double[pSize];
		for (int i = 0; i < pSize; i++)
			lFocusStackArray[i] = (i - (pSize / 2)) * pFocusZStep;
		return lFocusStackArray;
	}

	private static void processFolder(CommandLine pCommandLineValues)
	{
		final File[] lListOfFiles = pCommandLineValues.getFile()
																									.listFiles();

		final ArrayList<File> lSelectedFileList = new ArrayList<File>();

		for (final File lFile : lListOfFiles)
			if (lFile.isFile() && lFile.getName()
																	.toLowerCase()
																	.contains(".tif"))
				lSelectedFileList.add(lFile);

		System.out.println("List of files to process:");
		for (final File lFile : lSelectedFileList)
			System.out.println("  " + lFile.getName());

		final ArrayList<String> lReports = new ArrayList<String>();

		final Scanner lScanner = new Scanner(System.in);

		for (final File lFile : lSelectedFileList)
			if (lFile.isFile() && lFile.getName()
																	.toLowerCase()
																	.contains(".tif"))
			{
				try
				{
					System.out.format("_________________________________________\n");
					System.out.format("Processing file: %s \n",
														lFile.toString());
					lReports.add(processFile(pCommandLineValues, lFile));

					lScanner.nextLine();
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}
			}

		for (int i = 0; i < lSelectedFileList.size(); i++)
			System.out.print(lReports.get(i)
																.replaceAll("A", "\t")
																.replaceAll("B", "\t")
																.replaceAll("P", "\t")
																.replaceAll(".tif", ""));

	}

}
