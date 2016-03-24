package autopilot.utils.R.rplot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import autopilot.utils.R.R;

public class RPlot
{
	private final R mR;
	private final File mPlotFile;
	private final String mScript;

	private RPlotSeries mXAxisSerie;
	private final ArrayList<RPlotSeries> mSeriesList = new ArrayList<RPlotSeries>();

	private String mPlotTitle = "Title";
	private final String mXAxisLabel = "x";
	private final String mYAxisLabel = "y";
	private final String mLegendTitle = "Legend";
	private int mTitleFontSize = 16;
	private final int mAxisLabelFontSize = 12;
	private final int mLegendTitleFontSize = 12;
	private final int mLegendTextFontSize = 8;
	private final String mLegendPosition = "right";

	private final int mLineTypeCounter = 0;

	public RPlot(final File pFile) throws IOException
	{
		mR = new R();
		mPlotFile = pFile;
		final InputStream lResourceAsStream = RPlot.class.getResourceAsStream("RPlot.R");
		mScript = readStream(lResourceAsStream);
	}

	public void setTitle(final String pPlotTitle)
	{
		mPlotTitle = pPlotTitle;
	}

	public int getTitleFontSize()
	{
		return mTitleFontSize;
	}

	public void setTitleFontSize(final int pFontSize)
	{
		mTitleFontSize = pFontSize;
	}

	public RPlotSeries addXSeries(final String pString,
																final int pBegin,
																final int pEnd)
	{
		final double[] lArray = new double[pEnd - pBegin];
		int j = pBegin;
		for (int i = 0; i < lArray.length; i++)
		{
			lArray[i] = j++;
		}

		return addXSeries(pString, lArray);
	}

	public RPlotSeries addXSeries(final String pName,
																final double[] pSeries)
	{
		final RPlotSeries lRPlotSeries = new RPlotSeries(pName, pSeries);
		mXAxisSerie = lRPlotSeries;
		return mXAxisSerie;
	}

	public RPlotSeries addYSeries(final String pName,
																final double[] pSeries)
	{
		final RPlotSeries lRPlotSeries = new RPlotSeries(pName, pSeries);
		// lRPlotSeries.setLineType(mLineTypeCounter++);
		mSeriesList.add(lRPlotSeries);
		return lRPlotSeries;
	}

	public void plot()
	{
		addXSeriesData();
		addYSeriesData();
		final String lPostProcessedScript = postProcessScript(mScript);
		System.out.println(lPostProcessedScript);
		mR.addCode(lPostProcessedScript);
		mR.run();
	}

	private void addXSeriesData()
	{
		final String lName = mXAxisSerie.getName();
		final double[] lData = mXAxisSerie.getSeriesData();
		mR.addDoubleArray(lName, lData);
	}

	private void addYSeriesData()
	{
		for (final RPlotSeries lRPlotSeries : mSeriesList)
		{
			final String lName = lRPlotSeries.getName();
			final double[] lData = lRPlotSeries.getSeriesData();
			mR.addDoubleArray(lName, lData);
		}
	}

	private String postProcessScript(final String pScript)
	{
		String lPostProcessedScript = pScript;
		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@PlotFilePath@",
																														mPlotFile.getAbsolutePath());

		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@PlotTitle@",
																														mPlotTitle);

		final String lColumnList = getColumnList();
		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@ColumnList@",
																														lColumnList);

		final String lXAxisVariableName = mXAxisSerie.getName();
		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@XAxisVariableName@",
																														lXAxisVariableName);

		final String lPlotYSeriesRCode = getPlotSeriesRCode();
		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@PlotYSeriesRCode@",
																														lPlotYSeriesRCode);

		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@PlotTitle@",
																														mPlotTitle);

		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@XAxisLabel@",
																														mXAxisLabel);

		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@YAxisLabel@",
																														mYAxisLabel);

		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@LegendTitle@",
																														mLegendTitle);

		final String lLegendColors = getLegendColors();
		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@LegendColors@",
																														lLegendColors);

		final String lLegendBreaks = getLegendBreaks();
		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@LegendBreaks@",
																														lLegendBreaks);

		final String lLegendLabels = getLegendLabels();
		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@LegendLabels@",
																														lLegendLabels);

		final String lLegendLineTypes = getLegendLineTypes();
		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@LegendLineTypes@",
																														lLegendLineTypes);

		/*
		 * p <- p + opts(axis.title.x=theme_text(size=@AxisLabelFontSize@)) p <- p +
		 * opts(axis.title.y=theme_text(size=@AxisLabelFontSize@, angle=90)) + p <-
		 * p + opts(plot.title=theme_text(size=@TitleFontSize@)) + p <- p +
		 * opts(legend.text=theme_text(size=@LegendTextFontSize@)) + p <- p +
		 * opts(legend.title=theme_text(size=@LegendTitleFontSize@)) +
		 */

		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@TitleFontSize@",
																														""	+ getTitleFontSize());

		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@AxisLabelFontSize@",
																														""	+ mAxisLabelFontSize);

		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@LegendTitleFontSize@",
																														""	+ mLegendTitleFontSize);

		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@LegendTextFontSize@",
																														""	+ mLegendTextFontSize);

		lPostProcessedScript = lPostProcessedScript.replaceAll(	"@LegendPosition@",
																														""	+ mLegendPosition);

		return lPostProcessedScript;
	}

	private String getLegendColors()
	{
		final StringBuilder lStringBuilder = new StringBuilder();

		for (final RPlotSeries lRPlotSeries : mSeriesList)
		{
			final String lColorHexCode = lRPlotSeries.getColorHexCode();

			lStringBuilder.append("\"");
			lStringBuilder.append(lColorHexCode);
			lStringBuilder.append("\"");
			lStringBuilder.append(",");
		}
		if (lStringBuilder.length() > 0)
		{
			lStringBuilder.deleteCharAt(lStringBuilder.length() - 1);
		}

		return lStringBuilder.toString();
	}

	private String getLegendBreaks()
	{
		final StringBuilder lStringBuilder = new StringBuilder();

		for (final RPlotSeries lRPlotSeries : mSeriesList)
		{
			final String lVariableName = lRPlotSeries.getName();

			lStringBuilder.append("\"");
			lStringBuilder.append(lVariableName);
			lStringBuilder.append("\"");
			lStringBuilder.append(",");
		}
		if (lStringBuilder.length() > 0)
		{
			lStringBuilder.deleteCharAt(lStringBuilder.length() - 1);
		}

		return lStringBuilder.toString();
	}

	private String getLegendLabels()
	{
		final StringBuilder lStringBuilder = new StringBuilder();

		for (final RPlotSeries lRPlotSeries : mSeriesList)
		{
			final String lVariableName = lRPlotSeries.getName();

			lStringBuilder.append("\"");
			lStringBuilder.append(lVariableName);
			lStringBuilder.append("\"");
			lStringBuilder.append(",");
		}
		if (lStringBuilder.length() > 0)
		{
			lStringBuilder.deleteCharAt(lStringBuilder.length() - 1);
		}

		return lStringBuilder.toString();
	}

	private String getLegendLineTypes()
	{
		final StringBuilder lStringBuilder = new StringBuilder();

		for (final RPlotSeries lRPlotSeries : mSeriesList)
		{
			final String lLineType = lRPlotSeries.getLineTypeString();

			lStringBuilder.append(lLineType);
			lStringBuilder.append(",");
		}
		if (lStringBuilder.length() > 0)
		{
			lStringBuilder.deleteCharAt(lStringBuilder.length() - 1);
		}

		return lStringBuilder.toString();
	}

	private String getColumnList()
	{
		final StringBuilder lStringBuilder = new StringBuilder();

		final String lXAxisVariableName = mXAxisSerie.getName();
		lStringBuilder.append(lXAxisVariableName);
		lStringBuilder.append("=");
		lStringBuilder.append(lXAxisVariableName);
		lStringBuilder.append(",");

		for (final RPlotSeries lRPlotSeries : mSeriesList)
		{
			final String lVariableName = lRPlotSeries.getName();

			lStringBuilder.append(lVariableName);
			lStringBuilder.append("=");
			lStringBuilder.append(lVariableName);
			lStringBuilder.append(",");
		}
		if (lStringBuilder.length() > 0)
		{
			lStringBuilder.deleteCharAt(lStringBuilder.length() - 1);
		}

		return lStringBuilder.toString();
	}

	private String getPlotSeriesRCode()
	{
		final StringBuilder lStringBuilder = new StringBuilder();

		for (final RPlotSeries lRPlotSeries : mSeriesList)
		{
			final String lSeriePlotRCode = lRPlotSeries.getRCode();
			lStringBuilder.append("p <- p + ");
			lStringBuilder.append(lSeriePlotRCode);
			lStringBuilder.append("\n");
		}
		if (lStringBuilder.length() > 0)
		{
			lStringBuilder.deleteCharAt(lStringBuilder.length() - 1);
		}

		return lStringBuilder.toString();
	}

	private double[][] generateMatrix(final ArrayList<RPlotSeries> pSeriesList)
	{
		final int lNumberOfSeries = pSeriesList.size();

		final double[][] lMatrix = new double[lNumberOfSeries][];

		int i = 0;
		for (final RPlotSeries lRPlotSeries : pSeriesList)
		{
			lMatrix[i++] = lRPlotSeries.getSeriesData();
		}

		return lMatrix;
	}

	public static String readStream(final InputStream pStream) throws IOException
	{
		final String lLineSep = System.getProperty("line.separator");
		final BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(pStream));
		String nextLine = "";
		final StringBuffer lStringBuffer = new StringBuffer();
		while ((nextLine = lBufferedReader.readLine()) != null)
		{
			lStringBuffer.append(nextLine);
			lStringBuffer.append(lLineSep);
		}
		lBufferedReader.close();
		return lStringBuffer.toString();
	}

}
