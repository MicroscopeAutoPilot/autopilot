package autopilot.utils.rtlib.core.concurrent.executors;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public interface AsynchronousSchedulerServiceAccess
{

	public default ScheduledThreadPoolExecutor initializeScheduledExecutors()
	{
		return RTlibExecutors.getOrCreateScheduledThreadPoolExecutor(	this,
																		Thread.NORM_PRIORITY,
																		1,
																		Integer.MAX_VALUE);

	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public default WaitingScheduledFuture<?> schedule(	Runnable pRunnable,
														long pDelay,
														TimeUnit pUnit)
	{
		ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor = RTlibExecutors.getScheduledThreadPoolExecutor(this);
		if (lScheduledThreadPoolExecutor == null)
			lScheduledThreadPoolExecutor = initializeScheduledExecutors();

		return new WaitingScheduledFuture(lScheduledThreadPoolExecutor.schedule(pRunnable,
																				pDelay,
																				pUnit));
	}

	public default WaitingScheduledFuture<?> scheduleAtFixedRate(	Runnable pRunnable,
																	long pPeriod,
																	TimeUnit pUnit)
	{
		return scheduleAtFixedRate(pRunnable, 0, pPeriod, pUnit);
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public default WaitingScheduledFuture<?> scheduleAtFixedRate(	Runnable pRunnable,
																	long pInitialDelay,
																	long pPeriod,
																	TimeUnit pTimeUnit)
	{
		ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor = RTlibExecutors.getScheduledThreadPoolExecutor(this);
		if (lScheduledThreadPoolExecutor == null)
			lScheduledThreadPoolExecutor = initializeScheduledExecutors();

		return new WaitingScheduledFuture(lScheduledThreadPoolExecutor.scheduleAtFixedRate(	pRunnable,
																							pInitialDelay,
																							pPeriod,
																							pTimeUnit));
	}

	public default WaitingScheduledFuture<?> scheduleNTimesAtFixedRate(	Runnable pRunnable,
																		long pTimes,
																		long pPeriod,
																		TimeUnit pTimeUnit)
	{
		return scheduleNTimesAtFixedRate(	pRunnable,
											pTimes,
											0,
											pPeriod,
											pTimeUnit);
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public default WaitingScheduledFuture<?> scheduleNTimesAtFixedRate(	Runnable pRunnable,
																		long pTimes,
																		long pInitialDelay,
																		long pPeriod,
																		TimeUnit pTimeUnit)
	{
		final LimitedExecutionsRunnable lLimitedExecutionsRunnable = LimitedExecutionsRunnable.wrap(pRunnable,
																									pTimes);

		ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor = RTlibExecutors.getScheduledThreadPoolExecutor(this);
		if (lScheduledThreadPoolExecutor == null)
			lScheduledThreadPoolExecutor = initializeScheduledExecutors();

		return new WaitingScheduledFuture(lLimitedExecutionsRunnable.runNTimes(	lScheduledThreadPoolExecutor,
																				pInitialDelay,
																				pPeriod,
																				pTimeUnit));

	}

}
