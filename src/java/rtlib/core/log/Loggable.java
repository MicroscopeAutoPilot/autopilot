package rtlib.core.log;

import java.util.logging.Handler;
import java.util.logging.Logger;

public interface Loggable
{

	public static Logger getLoggerStatic(final String pSubSystemName)
	{
		final Logger mLogger = Logger.getLogger(pSubSystemName);
		for (final Handler lHandler : mLogger.getHandlers())
			lHandler.setFormatter(new CompactFormatter());
		mLogger.setUseParentHandlers(false);
		return mLogger;
	}

	public default Logger getLogger(final String pSubSystemName)
	{
		final Logger mLogger = Logger.getLogger(pSubSystemName);
		for (final Handler lHandler : mLogger.getHandlers())
			lHandler.setFormatter(new CompactFormatter());
		mLogger.setUseParentHandlers(false);
		return mLogger;
	}

	public static void loginfo(	Object pObject,
								final String pSubSystemName,
								String pMessage)
	{
		getLoggerStatic(pSubSystemName).info(pObject == null ? "null"
															: (pObject.getClass().getSimpleName()) + ": "
																+ pMessage);
	}

	public static void logwarning(	Object pObject,
									final String pSubSystemName,
									String pMessage)
	{
		getLoggerStatic(pSubSystemName).warning(pObject == null	? "null"
																: (pObject.getClass().getSimpleName()) + ": "
																	+ pMessage);
	}

	public static void logsevere(	Object pObject,
									final String pSubSystemName,
									String pMessage)
	{
		getLoggerStatic(pSubSystemName).severe(pObject == null	? "null"
																: (pObject.getClass().getSimpleName()) + ": "
																	+ pMessage);
	}

	public default void info(	final String pSubSystemName,
								String pMessage)
	{
		getLogger(pSubSystemName).info(this.getClass()
											.getSimpleName() + ": "
										+ pMessage);
	}

	public default void info(	final String pSubSystemName,
								String pFormat,
								Object... args)
	{
		getLogger(pSubSystemName).info(this.getClass()
											.getSimpleName() + ": "
										+ String.format(pFormat, args));
	}

	public default void warning(final String pSubSystemName,
								String pMessage)
	{
		getLogger(pSubSystemName).warning(this.getClass()
												.getSimpleName() + ": "
											+ pMessage);
	}

	public default void warning(final String pSubSystemName,
								String pFormat,
								Object... args)
	{
		getLogger(pSubSystemName).warning(this.getClass()
												.getSimpleName() + ": "
											+ String.format(pFormat,
															args));
	}

	public default void severe(	final String pSubSystemName,
								String pMessage)
	{
		getLogger(pSubSystemName).severe(this.getClass()
												.getSimpleName() + ": "
											+ pMessage);
	}

	public default void severe(	final String pSubSystemName,
								String pFormat,
								Object... args)
	{
		getLogger(pSubSystemName).severe(this.getClass()
												.getSimpleName() + ": "
											+ String.format(pFormat,
															args));
	}

	public default void severe(	final String pSubSystemName,
								String pMessage,
								Throwable e)
	{
		severe(	pSubSystemName,
				": '"	+ e.toString()
						+ "-> "
						+ e.getStackTrace()[0]
						+ "'");
	}

}
