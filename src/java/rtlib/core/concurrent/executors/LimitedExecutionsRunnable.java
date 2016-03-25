package rtlib.core.concurrent.executors;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class LimitedExecutionsRunnable implements Runnable
{
	private final AtomicLong mExecutionCounter = new AtomicLong();
	private final Runnable mDelegatedRunnable;
	private volatile ScheduledFuture<?> mScheduledFuture;
	private final long mMaximumNumberOfExecutions;

	public LimitedExecutionsRunnable(	Runnable pDelegateRunnable,
										long pMaximumNumberOfExecutions)
	{
		this.mDelegatedRunnable = pDelegateRunnable;
		this.mMaximumNumberOfExecutions = pMaximumNumberOfExecutions;
	}

	@Override
	public void run()
	{
		if (mScheduledFuture == null)
			throw new UnsupportedOperationException("Scheduling and execution of " + LimitedExecutionsRunnable.class.getSimpleName()
													+ " instances should be done using this class methods only. ");

		mDelegatedRunnable.run();
		if (mExecutionCounter.incrementAndGet() == mMaximumNumberOfExecutions)
		{
			mScheduledFuture.cancel(false);
		}
	}

	public ScheduledFuture<?> runNTimes(ScheduledExecutorService pScheduledExecutorService,
										long pPeriod,
										TimeUnit pTimeUnit)
	{
		return runNTimes(	pScheduledExecutorService,
							pPeriod,
							pTimeUnit);
	}

	public ScheduledFuture<?> runNTimes(ScheduledExecutorService pScheduledExecutorService,
										long pInitialDelay,
										long pPeriod,
										TimeUnit pTimeUnit)
	{
		mScheduledFuture = pScheduledExecutorService.scheduleAtFixedRate(	this,
																			pInitialDelay,
																			pPeriod,
																			pTimeUnit);
		return mScheduledFuture;
	}

	public ScheduledFuture<?> runNTimes(AsynchronousSchedulerServiceAccess pThis,
										long pPeriod,
										TimeUnit pUnit)
	{
		mScheduledFuture = pThis.scheduleAtFixedRate(	this,
														pPeriod,
														pUnit);
		return mScheduledFuture;
	}

	public static LimitedExecutionsRunnable wrap(	Runnable pDelegateRunnable,
													long pMaximumNumberOfExecutions)
	{
		return new LimitedExecutionsRunnable(	pDelegateRunnable,
												pMaximumNumberOfExecutions);
	}
}
