package rtlib.core.log.gui;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import rtlib.core.log.CompactFormatter;

public class LogWindowHandler extends Handler
{
	private static LogWindowHandler sHandler = null;

	private LogWindow mWindow = null;

	/**
	 * private constructor, preventing initialization
	 */
	private LogWindowHandler(String pTitle, int pWidth, int pHeight)
	{
		setLevel(Level.INFO);
		if (mWindow == null)
			mWindow = new LogWindow(pTitle, pWidth, pHeight);
	}

	public static synchronized void setVisible(boolean pVisible)
	{
		if (sHandler != null)
			sHandler.mWindow.setVisible(pVisible);
	}

	public static void dispose()
	{
		if (sHandler != null)
			sHandler.mWindow.dispose();
	}

	public static synchronized LogWindowHandler getInstance(String pTitle)
	{
		return getInstance(pTitle, 768, 320);
	}

	public static synchronized LogWindowHandler getInstance(String pTitle,
															int pWidth,
															int pHeight)
	{
		if (sHandler == null)
		{
			sHandler = new LogWindowHandler(pTitle, pWidth, pHeight);
			sHandler.setFormatter(new CompactFormatter());
		}
		return sHandler;
	}

	@Override
	public synchronized void publish(LogRecord record)
	{
		try
		{
			String message = null;
			// check if the record is loggable
			if (!isLoggable(record))
				return;
			try
			{
				message = getFormatter().format(record);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				mWindow.append(message);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void close()
	{
		try
		{
			if (mWindow != null)
				mWindow.dispose();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void flush()
	{
	}

}
