package autopilot.utils.R;

import com.github.rcaller.rStuff.RCaller;
import com.github.rcaller.rStuff.RCode;

import java.io.File;
import java.io.IOException;



/**
 */
public class R
{

	private RCaller mRCaller;
	private RCode mRcode;
	private File mPlotFile;

	public R()
	{
		super();
		try
		{
			mRCaller = new RCaller();

			final String lOS = System.getProperty("os.name").toLowerCase();
			System.out.println(lOS);

			if (lOS.contains("mac"))
			{
				mRCaller.setRscriptExecutable("/opt/local/bin/rscript");
			}
			else if (lOS.contains("nix") || lOS.contains("linux"))
			{
				mRCaller.setRscriptExecutable("/usr/bin/Rscript");
			}

			mRcode = new RCode();
			mRcode.clear();
		}
		catch (final Exception e)
		{
			System.out.println(e.toString());
		}
	}

	public final void addDoubleArray(	final String pName,
																		final double[] pArray)
	{
		mRcode.addDoubleArray(pName, pArray);
	}

	public final void addDoubleArray(	final String pName,
																		final double[][] pMatrix)
	{
		mRcode.addDoubleMatrix(pName, pMatrix);
	}

	public final void addCode(final String pCode)
	{
		mRcode.addRCode(pCode);
	}

	public final void startPlot() throws IOException
	{
		mPlotFile = mRcode.startPlot();
	}

	public final void endPlot()
	{
		mRcode.endPlot();
	}

	public final void showPlot()
	{
		mRcode.showPlot(mPlotFile);
	}

	public void run()
	{
		mRCaller.setRCode(mRcode);
		//mRCaller.redirectROutputToConsole();
		mRCaller.runOnly();

	}

}
