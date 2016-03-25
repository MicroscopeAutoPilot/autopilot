package autopilot.utils.rtlib.core.concurrent.timing;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public interface Waiting
{

	default public void notifyWaitingThreads()
	{
		synchronized (this)
		{
			notifyAll();
		}
	}

	default public Boolean waitFor(Callable<Boolean> pCallable)
	{
		return waitFor(Long.MAX_VALUE, TimeUnit.DAYS, pCallable);
	}

	default public Boolean waitFor(	long pTimeOut,
									TimeUnit pTimeUnit,
									Callable<Boolean> pCallable)
	{

		synchronized (this)
		{
			try
			{
				AtomicLong lCounter = new AtomicLong();
				long lTimeOutInMillis = pTimeUnit.toMillis(pTimeOut);
				while (!pCallable.call() && lCounter.incrementAndGet() < lTimeOutInMillis)
				{
					try
					{
						wait(1);
					}
					catch (InterruptedException e)
					{
					}
				}
				return pCallable.call();
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}
