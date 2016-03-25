package rtlib.core.concurrent.asyncprocs;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import rtlib.core.concurrent.executors.WaitingScheduledFuture;
import rtlib.core.concurrent.timing.Waiting;
import rtlib.core.log.Loggable;

public abstract class AsynchronousProcessorBase<I, O>	implements
														AsynchronousProcessorInterface<I, O>,
														AsynchronousExecutorServiceAccess,
														AsynchronousSchedulerServiceAccess,
														Loggable,
														Waiting
{

	private final String mName;
	private AsynchronousProcessorInterface<O, ?> mReceiver;
	private final BlockingQueue<I> mInputQueue;
	private final AtomicReference<WaitingScheduledFuture<?>> mScheduledFuture = new AtomicReference<>();
	private final AtomicBoolean mIsProcessing = new AtomicBoolean(false);

	public AsynchronousProcessorBase(	final String pName,
										final int pMaxQueueSize)
	{
		super();
		mName = pName;
		mInputQueue = new ArrayBlockingQueue<I>(pMaxQueueSize <= 0	? 1
																	: pMaxQueueSize);

	}

	@Override
	public void connectToReceiver(final AsynchronousProcessorInterface<O, ?> pAsynchronousProcessor)
	{
		mReceiver = pAsynchronousProcessor;
	}

	@Override
	public boolean start()
	{
		try
		{
			final Runnable lRunnable = () -> {

				try
				{
					final I lInput = mInputQueue.poll(	1,
														TimeUnit.SECONDS);
					if (lInput == null)
					{
						return;
					}
					mIsProcessing.set(true);
					final O lOutput = process(lInput);
					mIsProcessing.set(false);
					if (lOutput != null)
					{
						send(lOutput);
					}
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}

			};

			mScheduledFuture.set(scheduleAtFixedRate(	lRunnable,
														1,
														TimeUnit.NANOSECONDS));

			return true;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean stop()
	{
		return stop(Long.MAX_VALUE, TimeUnit.DAYS);
	}

	@Override
	public boolean stop(final long pTimeOut, TimeUnit pTimeUnit)
	{
		try
		{
			final WaitingScheduledFuture<?> lWaitingScheduledFuture = mScheduledFuture.getAndSet(null);
			if (lWaitingScheduledFuture != null)
			{
				lWaitingScheduledFuture.cancel(false);
				lWaitingScheduledFuture.waitForCompletion(	pTimeOut,
															pTimeUnit);
			}
			return true;
		}
		catch (final ExecutionException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean waitToFinish(final long pTimeOut,
								TimeUnit pTimeUnit)
	{
		waitFor(pTimeOut,
				pTimeUnit,
				() -> !mIsProcessing.get() && mInputQueue.isEmpty());
		return mInputQueue.isEmpty();
	}

	@Override
	public void close()
	{

	}

	@Override
	public boolean passOrWait(	final I pObject,
								final long pTimeOut,
								TimeUnit pTimeUnit)
	{
		waitFor(pTimeOut,
				pTimeUnit,
				() -> mScheduledFuture.get() != null);
		try
		{
			if (pObject == null)
				return false;
			mInputQueue.offer(pObject, pTimeOut, pTimeUnit);
		}
		catch (final InterruptedException e)
		{
			return passOrWait(pObject, pTimeOut, pTimeUnit);
		}
		return false;
	}

	@Override
	public boolean passOrWait(final I pObject)
	{
		waitFor(() -> mScheduledFuture.get() != null);
		try
		{
			if (pObject == null)
				return false;
			mInputQueue.put(pObject);
			return true;
		}
		catch (final InterruptedException e)
		{
			return passOrWait(pObject);
		}

	}

	@Override
	public boolean passOrFail(final I pObject)
	{
		if (mScheduledFuture.get() == null)
		{
			return false;
		}

		if (pObject != null)
			return mInputQueue.offer(pObject);
		else
			return false;
	}

	@Override
	public abstract O process(I pInput);

	protected void send(final O lOutput)
	{
		if (mReceiver != null)
		{
			mReceiver.passOrWait(lOutput);
		}
	}

	@Override
	public int getInputQueueLength()
	{
		return mInputQueue.size();
	}

	@Override
	public int getRemainingCapacity()
	{
		return mInputQueue.remainingCapacity();
	}

	public BlockingQueue<I> getInputQueue()
	{
		return mInputQueue;
	}

}
