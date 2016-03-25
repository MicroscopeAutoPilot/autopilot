package rtlib.core.concurrent.executors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CompletingThreadPoolExecutor extends ThreadPoolExecutor
{

	private final BlockingQueue<Future<?>> mFutureQueue = new LinkedBlockingQueue<Future<?>>(Integer.MAX_VALUE);

	public CompletingThreadPoolExecutor(int pCorePoolSize,
										int pMaximumPoolSize,
										long pKeepAliveTime,
										TimeUnit pUnit,
										BlockingQueue<Runnable> pWorkQueue)
	{
		super(	pCorePoolSize,
				pMaximumPoolSize,
				pKeepAliveTime,
				pUnit,
				pWorkQueue);
	}

	public CompletingThreadPoolExecutor(int pCorePoolSize,
										int pMaximumPoolSize,
										long pKeepAliveTime,
										TimeUnit pUnit,
										BlockingQueue<Runnable> pWorkQueue,
										RejectedExecutionHandler pHandler)
	{
		super(	pCorePoolSize,
				pMaximumPoolSize,
				pKeepAliveTime,
				pUnit,
				pWorkQueue,
				pHandler);
	}

	public CompletingThreadPoolExecutor(int pCorePoolSize,
										int pMaximumPoolSize,
										long pKeepAliveTime,
										TimeUnit pUnit,
										BlockingQueue<Runnable> pWorkQueue,
										ThreadFactory pThreadFactory,
										RejectedExecutionHandler pHandler)
	{
		super(	pCorePoolSize,
				pMaximumPoolSize,
				pKeepAliveTime,
				pUnit,
				pWorkQueue,
				pThreadFactory,
				pHandler);
	}

	public CompletingThreadPoolExecutor(int pCorePoolSize,
										int pMaximumPoolSize,
										long pKeepAliveTime,
										TimeUnit pUnit,
										BlockingQueue<Runnable> pWorkQueue,
										ThreadFactory pThreadFactory)
	{
		super(	pCorePoolSize,
				pMaximumPoolSize,
				pKeepAliveTime,
				pUnit,
				pWorkQueue,
				pThreadFactory);
	}

	@Override
	public Future<?> submit(Runnable pTask)
	{
		Future<?> lFutur = super.submit(pTask);
		addFutur(lFutur);
		return lFutur;
	}

	@Override
	public <T> Future<T> submit(Runnable pTask, T pResult)
	{
		Future<T> lFutur = super.submit(pTask, pResult);
		addFutur(lFutur);
		return lFutur;
	}

	@Override
	public <T> Future<T> submit(Callable<T> pTask)
	{
		Future<T> lFutur = super.submit(pTask);
		addFutur(lFutur);
		return lFutur;
	}

	private void addFutur(Future<?> pFutur)
	{
		mFutureQueue.add(pFutur);
	}

	public Future<?> getFutur(long pTimeOut, TimeUnit pTimeUnit) throws InterruptedException
	{
		return mFutureQueue.poll(pTimeOut, pTimeUnit);
	}

	public void waitForCompletion(long pTimeOut, TimeUnit pTimeUnit) throws ExecutionException,
																	TimeoutException
	{
		final long lStartTimeNanos = System.nanoTime();
		final long lDeadlineTimeNanos = lStartTimeNanos + pTimeUnit.toNanos(pTimeOut);

		while (mFutureQueue.peek() != null && System.nanoTime() <= lDeadlineTimeNanos)
		{
			Future<?> lFuture = null;
			try
			{
				lFuture = mFutureQueue.poll();
				if (lFuture != null)
					lFuture.get(pTimeOut, pTimeUnit);
			}
			catch (InterruptedException e)
			{
				// System.out.println("InterruptedException");
				reinject(lFuture);
			}
		}

		if (System.nanoTime() > lDeadlineTimeNanos)
			throw new TimeoutException("Run out of time waiting for " + this.getClass()
																			.getSimpleName()
										+ " tasks to finish!");
	}

	private void reinject(Future<?> lFuture)
	{
		if (lFuture != null)
			try
			{
				mFutureQueue.put(lFuture);
			}
			catch (InterruptedException e1)
			{
				reinject(lFuture);
			}
	}

}
