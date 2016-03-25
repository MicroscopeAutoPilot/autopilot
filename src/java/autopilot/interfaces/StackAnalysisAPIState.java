package autopilot.interfaces;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import autopilot.utils.rtlib.core.log.CompactFormatter;
import autopilot.utils.rtlib.core.log.gui.LogWindowHandler;
import autopilot.image.DoubleArrayImage;
import autopilot.stackanalysis.FocusStackAnalysis;

public class StackAnalysisAPIState
{

	private volatile FocusStackAnalysis mFocusStackAnalysis = new FocusStackAnalysis();
	private volatile double[] mZArray;
	private volatile int mPlaneIndex;

	private final Logger mLogger;
	private final ConsoleHandler mConsoleHandler;
	private LogWindowHandler mLogWindowHandler;

	public StackAnalysisAPIState()
	{
		super();
		mLogger = Logger.getLogger("autopilot");
		mLogger.setUseParentHandlers(false);

		mConsoleHandler = new ConsoleHandler();
		mConsoleHandler.setFormatter(new CompactFormatter());

	}

	public void setParameter(String pName, double pValue)
	{
		mFocusStackAnalysis.setDoubleParameter(pName, pValue);

		if (pName.equalsIgnoreCase("debug"))
		{
			mFocusStackAnalysis.setBooleanParameter("visualize",
																							pValue >= 0);
		}
		else if (pName.equalsIgnoreCase("logconsole"))
		{
			if (pValue > 0)
			{
				mLogger.addHandler(mConsoleHandler);
			}
			else
			{
				mLogger.removeHandler(mConsoleHandler);
			}
		}
		else if (pName.equalsIgnoreCase("logwindow"))
		{
			if (pValue > 0 && mLogWindowHandler == null)
			{
				mLogWindowHandler = LogWindowHandler.getInstance("AutoPilot log");
				mLogger.addHandler(mLogWindowHandler);
			}
			else if (pValue <= 0 && mLogWindowHandler != null)
			{
				mLogger.removeHandler(mLogWindowHandler);
				mLogWindowHandler.close();
				mLogWindowHandler = null;
			}
		}

	}

	public void newStack(double[] pZArray)
	{
		mZArray = pZArray;
		mFocusStackAnalysis.reset();
		mFocusStackAnalysis.setDoubleParameter("nbplanes", pZArray.length);
		mPlaneIndex = 0;
	}

	public void loadPlane(final ByteBuffer p16BitImageByteBuffer,
												final int pWidth,
												final int pHeight)
	{
		final double z = mZArray[mPlaneIndex++];
		final DoubleArrayImage lDoubleArrayImage = Utils.getDoubleArrayImage(	p16BitImageByteBuffer,
																																					pWidth,
																																					pHeight);
		mFocusStackAnalysis.loadPlane(z, lDoubleArrayImage);
	}

	public int getResult(	double pMaxWaitTimeInSeconds,
												String pName,
												double[] pResultArray)
	{
		if (mFocusStackAnalysis == null)
			return -2;

		try
		{
			if (pName.equalsIgnoreCase("{dz,alpha,beta}"))
			{
				pResultArray[0] = nullToNaN(mFocusStackAnalysis.getDeltaZ(pMaxWaitTimeInSeconds));
				pResultArray[1] = nullToNaN(mFocusStackAnalysis.getAlpha(pMaxWaitTimeInSeconds));
				pResultArray[2] = nullToNaN(mFocusStackAnalysis.getBeta(pMaxWaitTimeInSeconds));
			}

			return 0;
		}
		catch (InterruptedException | ExecutionException e)
		{
			e.printStackTrace();
			return -3;
		}

	}

	private double nullToNaN(Double pValue)
	{
		return pValue == null ? Double.NaN : pValue;
	}

}
