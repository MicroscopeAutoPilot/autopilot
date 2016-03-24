package autopilot.utils.R.rplot;

import java.awt.Color;

public class RPlotSeries
{
	private static final String lSeriePlotRTemplate = "geom_line(aes(y = @Variable@, group = \"@Variable@\", color = \"@Variable@\"), linetype=@LineType@ )";
	// @LineType@
	// color = \"@Color@\",
	private static final int cMaxNumberOfLineTypes = 6;
	private static final String[] cLineTypesStrings = new String[]
	{ "solid", "dashed", "dotted", "dotdash", "longdash", "twodash" };

	private final String mName;
	private final double[] mSeriesData;

	private Color mColor = Color.black;
	private int mLineType = 0;

	public RPlotSeries(final String pName, final double[] pSeriesData)
	{
		mName = pName;
		mSeriesData = pSeriesData;
	}

	public String getName()
	{
		return mName;
	}

	public double[] getSeriesData()
	{
		return mSeriesData;
	}

	public String getRCode()
	{
		final String lRCode = getPostProcessedCode(lSeriePlotRTemplate);
		return lRCode;
	}

	private String getPostProcessedCode(final String pRCodeString)
	{
		String lPostProcessedCodeString = pRCodeString;

		lPostProcessedCodeString = lPostProcessedCodeString.replaceAll(	"@Variable@",
																																		mName);
		lPostProcessedCodeString = lPostProcessedCodeString.replaceAll(	"@Color@",
																																		getColorHexCode());
		lPostProcessedCodeString = lPostProcessedCodeString.replaceAll(	"@LineType@",
																																		getLineTypeString());

		return lPostProcessedCodeString;
	}

	public final String getLineTypeString()
	{
		return "\"" + cLineTypesStrings[mLineType] + "\"";
	}

	public final void setColor(final Color pColor)
	{
		mColor = pColor;
	}

	public final String getColorHexCode()
	{
		String lRGBHexCode = Integer.toHexString(mColor.getRGB());
		lRGBHexCode = "#" + lRGBHexCode.substring(2, lRGBHexCode.length());
		return lRGBHexCode;
	}

	public final void setLineType(final int pLineType)
	{
		mLineType = pLineType % cMaxNumberOfLineTypes;
	}

	public final int getLineType()
	{
		return mLineType;
	}

}
