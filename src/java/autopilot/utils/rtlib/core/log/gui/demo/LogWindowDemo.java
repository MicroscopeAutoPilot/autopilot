package autopilot.utils.rtlib.core.log.gui.demo;

import java.util.logging.Logger;

import autopilot.utils.rtlib.core.log.CompactFormatter;
import autopilot.utils.rtlib.core.log.gui.LogWindowHandler;

public class LogWindowDemo
{
	private LogWindowHandler mLogWindowHandler = null;

	private Logger mLogger = null;

	public LogWindowDemo()
	{
		mLogWindowHandler = LogWindowHandler.getInstance(	"demo",
															768,
															320);
		mLogger = Logger.getLogger("demo");
		mLogger.setUseParentHandlers(false);
		mLogWindowHandler.setFormatter(new CompactFormatter());
		mLogger.addHandler(mLogWindowHandler);
	}

	public void logMessage(int i)
	{
		mLogger.info(i + " Hello from LogWindowHandler...");
	}

	public static void main(String args[]) throws InterruptedException
	{
		final LogWindowDemo lLogWindowDemo = new LogWindowDemo();

		for (int i = 0; i < 1000; i++)
		{
			lLogWindowDemo.logMessage(i);
			Thread.sleep(1);
		}

		LogWindowHandler.setVisible(true);
		Thread.sleep(4000);
		LogWindowHandler.dispose();
	}
}