package autopilot.measures.benchmark;

import gnu.trove.list.array.TDoubleArrayList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;

import autopilot.image.DoubleArrayImage;
import autopilot.image.readers.TiffReader;
import autopilot.image.readers.TiffReader.TiffStackCallBack;
import autopilot.measures.FocusMeasures;
import autopilot.measures.FocusMeasures.FocusMeasure;
import autopilot.measures.FocusMeasures.FocusMeasureType;
import autopilot.utils.HashMapMap;
import autopilot.utils.R.rplot.RPlot;
import autopilot.utils.math.Maximum;
import autopilot.utils.math.Mean;
import autopilot.utils.math.Median;
import autopilot.utils.math.Minimum;
import autopilot.utils.math.StandardDeviation;

public class BenchmarkMeasures
{
	private final File mFolder;
	private final File mResultFolder;
	private final String mFilter;
	private final String mMeasureFilter;

	private final HashMap<FocusMeasure, Mean> mMeanDistanceToFocusPointGroundTruth = new HashMap<FocusMeasure, Mean>();
	private final HashMap<FocusMeasure, Median> mMedianDistanceToFocusPointGroundTruth = new HashMap<FocusMeasure, Median>();
	private final HashMap<FocusMeasure, Maximum> mMaximumDistanceToFocusPointGroundTruth = new HashMap<FocusMeasure, Maximum>();
	private final HashMap<FocusMeasure, Median> mMedianRangeScore = new HashMap<FocusMeasure, Median>();
	private final HashMap<FocusMeasure, Minimum> mMinimumRangeScore = new HashMap<FocusMeasure, Minimum>();
	private final HashMap<FocusMeasure, Median> mMedianDensityOfLocalExtrema = new HashMap<FocusMeasure, Median>();
	private final HashMap<FocusMeasure, Median> mMedianComputationTimeInNanosecondsPerPixel = new HashMap<FocusMeasure, Median>();
	private final HashMapMap<String, FocusMeasure, Maximum> mMaximumFocusMeasureValue = new HashMapMap<String, FocusMeasure, Maximum>();

	private final HashMap<File, Median> mMedianErrorPerFile = new HashMap<File, Median>();
	private final HashMap<File, StandardDeviation> mStandardDeviationErrorPerFile = new HashMap<File, StandardDeviation>();
	private double mPSFSupportDiameter = FocusMeasures.cPSFSupportDiameter;
	private final boolean mGeneratePlotData;

	public BenchmarkMeasures(	final File pFolder,
														final String pFilter,
														final String pMeasureFilter,
														final File pResultFolder,
														final boolean pGeneratePlotData)
	{
		super();
		mFolder = pFolder;
		mFilter = pFilter;
		mMeasureFilter = pMeasureFilter;
		mResultFolder = pResultFolder;
		mGeneratePlotData = pGeneratePlotData;
	}

	public void setPSFSupportDiameter(final int pPSFSupportDiameter)
	{
		mPSFSupportDiameter = pPSFSupportDiameter;
	}

	public void run() throws IOException
	{
		final File[] lListFiles = mFolder.listFiles();

		final ArrayList<File> lSelectedFiles = new ArrayList<File>();

		for (final File lFile : lListFiles)
		{
			final String lName = lFile.getName();
			final boolean isTiff = lName.endsWith(".tif");
			final boolean isFiltered = mFilter.isEmpty() ? true
																									: lName.contains(mFilter);

			if (isTiff && isFiltered)
			{
				System.out.format("selected file: %s \n", lName);
				lSelectedFiles.add(lFile);
			}
		}

		for (final File lSelectedFile : lSelectedFiles)
		{
			System.out.format("Processing File: %s \n", lSelectedFile);
			testFocusMeasuresOn(lSelectedFile);
		}

		final FocusMeasure[] lFocusMeasures = FocusMeasures.FocusMeasure.values();

		final File lMaxFocusMeasureValueResultFile = new File(mResultFolder,
																													"MaxMeasuresResults." + mMeasureFilter
																															+ ".txt");
		final Formatter lMaxFocusMeasureValueResultFormatter = new Formatter(lMaxFocusMeasureValueResultFile);
		lMaxFocusMeasureValueResultFormatter.format("type\tfocus measure\tfile\tmax\n");

		for (final FocusMeasure lFocusMeasure : lFocusMeasures)
		{
			for (final File lSelectedFile : lSelectedFiles)
			{
				if (lFocusMeasure.name().contains(mMeasureFilter))
				{
					lMaxFocusMeasureValueResultFormatter.format("%s\t%s\t%s\t%g\n",
																											lFocusMeasure.getType()
																																		.name(),
																											lFocusMeasure.getLongName(),
																											lSelectedFile.getName(),
																											mMaximumFocusMeasureValue.get(lSelectedFile.getName(),
																																										lFocusMeasure)
																																								.getStatistic());
				}
			}
		}
		lMaxFocusMeasureValueResultFormatter.close();

		final File lResultFile = new File(mResultFolder,
																			"MeasuresResults." + mMeasureFilter
																					+ ".txt");
		final Formatter lFormatter = new Formatter(lResultFile);
		lFormatter.format("type\tfocus measure\tmedian\tmean\tmaximum\tmedian R score\tmedian dle\tns/p\n");

		for (final FocusMeasure lFocusMeasure : lFocusMeasures)
		{
			if (lFocusMeasure.name().contains(mMeasureFilter))
			{

				lFormatter.format("%s\t%s\t%g\t%g\t%g\t%g\t%g\t%g\n",
													lFocusMeasure.getType().name(),
													lFocusMeasure.getLongName(),
													mMedianDistanceToFocusPointGroundTruth.get(lFocusMeasure)
																																.getStatistic(),
													mMeanDistanceToFocusPointGroundTruth.get(lFocusMeasure)
																															.getStatistic(),
													mMaximumDistanceToFocusPointGroundTruth.get(lFocusMeasure)
																																	.getStatistic(),
													mMedianRangeScore.get(lFocusMeasure)
																						.getStatistic(),
													mMedianDensityOfLocalExtrema.get(lFocusMeasure)
																											.getStatistic(),
													mMedianComputationTimeInNanosecondsPerPixel.get(lFocusMeasure)
																																			.getStatistic());

			}
		}
		lFormatter.close();

		final File lErrorPerFileFile = new File(mResultFolder,
																						"ErrorPerFile." + mMeasureFilter
																								+ ".txt");
		lErrorPerFileFile.delete();
		final Formatter lErrorPerFileFormatter = new Formatter(lErrorPerFileFile);
		lErrorPerFileFormatter.format("file\tmedian error\tstandard deviation\n");
		for (final File lSelectedFile : lSelectedFiles)
		{
			final double lMedianErrorPerFile = mMedianErrorPerFile.get(lSelectedFile)
																														.getStatistic();

			final double lStandardDeviationPerFile = mStandardDeviationErrorPerFile.get(lSelectedFile)
																																							.getStatistic();

			lErrorPerFileFormatter.format("%s\t%g\t%g\n",
																		lSelectedFile.toString(),
																		lMedianErrorPerFile,
																		lStandardDeviationPerFile);
		}
		lErrorPerFileFormatter.close();

	}

	public void testFocusMeasuresOn(final File pTiffFile) throws IOException
	{
		final DoubleArrayImage lDoubleArrayImage = null;
		final FocusMeasure[] lFocusMeasures = FocusMeasures.FocusMeasure.values();

		final HashMap<FocusMeasure, TDoubleArrayList> lData = new HashMap<FocusMeasure, TDoubleArrayList>();
		for (final FocusMeasure lFocusMeasure : lFocusMeasures)
		{
			if (lFocusMeasure.name().contains(mMeasureFilter))
			{
				lData.put(lFocusMeasure, new TDoubleArrayList());
			}
		}

		TiffReader.readTiffStack(pTiffFile, new TiffStackCallBack()
		{
			@Override
			public boolean image(	final int pImageIndex,
														final DoubleArrayImage pDoubleArrayImage)
			{
				System.out.format("ImageIndex=%d \n", pImageIndex);
				for (final FocusMeasure lFocusMeasure : lFocusMeasures)
				{
					if (lFocusMeasure.name().contains(mMeasureFilter))
					{
						System.out.format("Measure: %s \n", lFocusMeasure.name());
						final long lBeginTime = System.nanoTime();
						FocusMeasures.cPSFSupportDiameter = mPSFSupportDiameter;
						FocusMeasures.cOTFFilterRatio = 1 / mPSFSupportDiameter;
						final double lMeasure = FocusMeasures.computeFocusMeasure(lFocusMeasure,
																																			pDoubleArrayImage);
						Maximum lMaximum = mMaximumFocusMeasureValue.get(	pTiffFile.getName(),
																															lFocusMeasure);
						if (lMaximum == null)
						{
							lMaximum = new Maximum();
							mMaximumFocusMeasureValue.put(pTiffFile.getName(),
																						lFocusMeasure,
																						lMaximum);
						}
						lMaximum.enter(lMeasure);

						final long lEndTime = System.nanoTime();
						final double lMicroSecondsPerPixels = (lEndTime - lBeginTime) / pDoubleArrayImage.getLength();
						Median lMedian = mMedianComputationTimeInNanosecondsPerPixel.get(lFocusMeasure);
						if (lMedian == null)
						{
							lMedian = new Median();
							mMedianComputationTimeInNanosecondsPerPixel.put(lFocusMeasure,
																															lMedian);
						}
						lMedian.enter(lMicroSecondsPerPixels);

						// System.out.format(" = %g \n",lMeasure);
						lData.get(lFocusMeasure).add(lMeasure);

					}
				}
				return true;
			}
		},
															lDoubleArrayImage);

		System.out.format("Normalizing... \n");
		int lNumberOfImages = 0;
		for (final FocusMeasure lFocusMeasure : lFocusMeasures)
		{
			if (lFocusMeasure.name().contains(mMeasureFilter))
			{
				System.out.format("  %s \n", lFocusMeasure.name());
				final TDoubleArrayList lFocusSeries = lData.get(lFocusMeasure);
				final TDoubleArrayList lNormalized = normalize(lFocusSeries);
				lData.put(lFocusMeasure, lNormalized);
				lNumberOfImages = lNormalized.size();
			}
		}

		if (mGeneratePlotData)
		{
			generatePlots(pTiffFile, lFocusMeasures, lData, lNumberOfImages);
		}

		System.out.format("Computing banchmark scores \n");

		final int lFocusPointGroundTruth = parseFocusPointGroundTruth(pTiffFile.getName());
		final double lStepSizeInMicrons = parseStepSize(pTiffFile.getName());

		for (final FocusMeasure lFocusMeasure : lFocusMeasures)
		{
			if (lFocusMeasure.name().contains(mMeasureFilter))
			{
				final TDoubleArrayList lFocusSeries = lData.get(lFocusMeasure);

				final double lFocusPointAccordingToMeasure = indexStartingAtOneOfMaximum(lFocusSeries);
				final double lDistanceToFocusPointGroundTruth = lStepSizeInMicrons * Math.abs(lFocusPointGroundTruth - lFocusPointAccordingToMeasure);
				final double lRange90 = computeRange(lFocusSeries, 0.9);
				computeRange(lFocusSeries, 0.5);
				final double lRange10 = computeRange(lFocusSeries, 0.1);
				final double lRangeScore = lRange10 / lRange90;
				final double lDensityOfLocalExtrema = densityOfLocalExtrema(lFocusSeries);

				final String lResultLine = String.format(	"%s\t%s\t%d\t%g\t%g\t%g\t%g\n",
																									pTiffFile.getName(),
																									lFocusMeasure.getLongName(),
																									lFocusPointGroundTruth,
																									lFocusPointAccordingToMeasure,
																									lDistanceToFocusPointGroundTruth,
																									lRangeScore,
																									lDensityOfLocalExtrema);

				Mean lMean = mMeanDistanceToFocusPointGroundTruth.get(lFocusMeasure);
				if (lMean == null)
				{
					lMean = new Mean();
					mMeanDistanceToFocusPointGroundTruth.put(	lFocusMeasure,
																										lMean);
				}
				lMean.enter(lDistanceToFocusPointGroundTruth);

				Median lMedian = mMedianDistanceToFocusPointGroundTruth.get(lFocusMeasure);
				if (lMedian == null)
				{
					lMedian = new Median();
					mMedianDistanceToFocusPointGroundTruth.put(	lFocusMeasure,
																											lMedian);
				}
				lMedian.enter(lDistanceToFocusPointGroundTruth);

				Median lMedianErrorPerFile = mMedianErrorPerFile.get(pTiffFile);
				if (lMedianErrorPerFile == null)
				{
					lMedianErrorPerFile = new Median();
					mMedianErrorPerFile.put(pTiffFile, lMedianErrorPerFile);
				}
				lMedianErrorPerFile.enter(lDistanceToFocusPointGroundTruth);

				StandardDeviation lStandardDeviation = mStandardDeviationErrorPerFile.get(pTiffFile);
				if (lStandardDeviation == null)
				{
					lStandardDeviation = new StandardDeviation();
					mStandardDeviationErrorPerFile.put(	pTiffFile,
																							lStandardDeviation);
				}
				lStandardDeviation.enter(lDistanceToFocusPointGroundTruth);

				Maximum lMaximum = mMaximumDistanceToFocusPointGroundTruth.get(lFocusMeasure);
				if (lMaximum == null)
				{
					lMaximum = new Maximum();
					mMaximumDistanceToFocusPointGroundTruth.put(lFocusMeasure,
																											lMaximum);
				}

				lMaximum.enter(lDistanceToFocusPointGroundTruth);

				lMedian = mMedianRangeScore.get(lFocusMeasure);
				if (lMedian == null)
				{
					lMedian = new Median();
					mMedianRangeScore.put(lFocusMeasure, lMedian);
				}

				lMedian.enter(lRangeScore);

				Minimum lMinimum = mMinimumRangeScore.get(lFocusMeasure);
				if (lMinimum == null)
				{
					lMinimum = new Minimum();
					mMinimumRangeScore.put(lFocusMeasure, lMinimum);
				}

				lMinimum.enter(lRangeScore);

				lMedian = mMedianDensityOfLocalExtrema.get(lFocusMeasure);
				if (lMedian == null)
				{
					lMedian = new Median();
					mMedianDensityOfLocalExtrema.put(lFocusMeasure, lMedian);
				}
				lMedian.enter(lDensityOfLocalExtrema);

				appendToMeasureFile(lFocusMeasure, lResultLine);

			}
		}

		if (mGeneratePlotData)
		{
			generatePlotTxts(	pTiffFile,
												lFocusMeasures,
												lData,
												lFocusPointGroundTruth,
												lStepSizeInMicrons);
		}

		System.out.format("Done... \n");

		// Desktop lDesktop = Desktop.getDesktop();
		// lDesktop.open(lPdfFile);

	}

	private void generatePlotTxts(final File pTiffFile,
																final FocusMeasure[] lFocusMeasures,
																final HashMap<FocusMeasure, TDoubleArrayList> lData,
																final int lFocusPointGroundTruth,
																final double lStepSizeInMicrons) throws FileNotFoundException
	{
		final File lFocusMeasuresTextFileFolder = new File(mResultFolder + "/txt/");
		lFocusMeasuresTextFileFolder.mkdirs();
		for (final FocusMeasure lFocusMeasure : lFocusMeasures)
		{
			final File lFocusMeasuresTextFile = new File(	lFocusMeasuresTextFileFolder,
																										pTiffFile.getName() + "."
																												+ lFocusMeasure.toString()
																												+ ".plot.txt");

			if (lFocusMeasure.name().contains(mMeasureFilter))
			{
				final Formatter lFormatter = new Formatter(lFocusMeasuresTextFile);

				final TDoubleArrayList lNormalized = lData.get(lFocusMeasure);

				int i = 1;
				for (final double lMeasure : lNormalized.toArray())
				{
					final int lGroundTruth = i == lFocusPointGroundTruth ? 1
																															: 0;

					lFormatter.format("%d\t%g\t%d\t%g\n",
														i,
														i * lStepSizeInMicrons,
														lGroundTruth,
														lMeasure);

					i++;
				}

				lFormatter.flush();
				lFormatter.close();
			}
		}
	}

	private void generatePlots(	final File pTiffFile,
															final FocusMeasure[] lFocusMeasures,
															final HashMap<FocusMeasure, TDoubleArrayList> lData,
															final int lNumberOfImages) throws IOException
	{
		final File lPdfPlotsFolder = new File(mResultFolder + "/pdf/");
		lPdfPlotsFolder.mkdirs();

		for (final FocusMeasureType lFocusMeasureType : FocusMeasureType.values())
		{
			final File lPdfFile = new File(	lPdfPlotsFolder,
																			pTiffFile.getName() + "."
																					+ lFocusMeasureType.name()
																					+ ".plot.pdf");

			System.out.format("Plotting into PDF file: %s \n", lPdfFile);
			final RPlot lRPlot = new RPlot(lPdfFile);
			lRPlot.setTitleFontSize(12);
			lRPlot.setTitle(lFocusMeasureType.name() + ".  focus measures for "
											+ pTiffFile.getName());

			lRPlot.addXSeries("ImageIndex", 0, lNumberOfImages);
			for (final FocusMeasure lFocusMeasure : lFocusMeasures)
			{
				if (lFocusMeasure.getType() == lFocusMeasureType)
				{
					if (lFocusMeasure.name().contains(mMeasureFilter))
					{
						final TDoubleArrayList lFocusSeries = lData.get(lFocusMeasure);
						lRPlot.addYSeries(lFocusMeasure.name(),
															lFocusSeries.toArray());

					}
				}
			}
			lRPlot.plot();
		}
	}

	private void appendToMeasureFile(	final FocusMeasure pFocusMeasure,
																		final String pResultLine) throws IOException
	{
		final File lResultFile = new File(mResultFolder, "Results.txt");
		final boolean lFileExists = lResultFile.exists();
		String lExistingContent = "";
		if (lFileExists)
		{
			lExistingContent = readFromFile(lResultFile);
		}
		final Formatter lFormatter = new Formatter(lResultFile);
		lFormatter.format(lExistingContent);
		lFormatter.format(pResultLine);
		lFormatter.close();
	}

	public static int parseFocusPointGroundTruth(final String pName)
	{
		final int lStart = pName.indexOf("f=");
		int lEnd = pName.indexOf("_", lStart);
		if (lEnd < 0)
		{
			lEnd = pName.indexOf(".", lStart);
		}
		final String lFocusPointString = pName.substring(lStart + 2, lEnd);
		final int lFocusPoint = Integer.parseInt(lFocusPointString);
		return lFocusPoint;
	}

	public static double parseStepSize(final String pName)
	{
		final int lStart = pName.indexOf("s=");
		final int lEnd = pName.indexOf("_", lStart);
		final String lStepSizeString = pName.substring(lStart + 2, lEnd);
		final double lStepSize = Double.parseDouble(lStepSizeString);
		return lStepSize;
	}

	private double indexStartingAtOneOfMaximum(final TDoubleArrayList pFocusSeries)
	{
		final int length = pFocusSeries.size();
		int index = -1;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < length; i++)
		{
			final double lFocusValue = pFocusSeries.get(i);
			if (lFocusValue > max)
			{
				max = lFocusValue;
				index = i + 1;
			}
		}

		return index;
	}

	private double computeRange(final TDoubleArrayList pFocusSeries,
															final double pPercent)
	{
		final int length = pFocusSeries.size();
		int count = 0;
		for (int i = 0; i < length; i++)
		{
			final double lFocusValue = pFocusSeries.get(i);
			if (lFocusValue >= pPercent)
			{
				count++;
			}
		}
		final double lRange = (double) count / length;

		return lRange;
	}

	private double densityOfLocalExtrema(final TDoubleArrayList pFocusSeries)
	{
		final int length = pFocusSeries.size();
		final double[] array = pFocusSeries.toArray();
		int count = 0;
		int total = 0;
		for (int i = 1; i < length - 1; i++)
		{
			if (array[i] < array[i - 1] && array[i] < array[i + 1]
					|| array[i] > array[i - 1]
					&& array[i] > array[i + 1])
			{
				count++;
			}
			total++;
		}
		final double lDensity = (double) count / total;

		return lDensity;
	}

	private static final TDoubleArrayList normalize(final TDoubleArrayList pFocusSeries)
	{
		final TDoubleArrayList lNormalizedFocusSeries = new TDoubleArrayList(pFocusSeries.size());
		final int length = pFocusSeries.size();

		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < length; i++)
		{
			final double value = pFocusSeries.getQuick(i);
			min = Math.min(min, value);
			max = Math.max(max, value);
		}

		for (int i = 0; i < length; i++)
		{
			final double value = pFocusSeries.getQuick(i);
			final double normalized = (value - min) / (max - min);
			lNormalizedFocusSeries.add(normalized);
		}

		return lNormalizedFocusSeries;
	}

	public static String readFromFile(final File pFile) throws IOException
	{
		final String lineSep = System.getProperty("line.separator");
		final BufferedReader br = new BufferedReader(new FileReader(pFile));
		String nextLine = "";
		final StringBuffer sb = new StringBuffer();
		while ((nextLine = br.readLine()) != null)
		{
			sb.append(nextLine);
			//
			// note:
			// BufferedReader strips the EOL character.
			//
			sb.append(lineSep);
		}
		br.close();
		return sb.toString();

	}

}
