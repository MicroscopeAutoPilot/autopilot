package rtlib.core.concurrent.asyncprocs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AsynchronousProcessorAdapter<I, O> implements
												AsynchronousProcessorInterface<I, O>
{

	@Override
	public O process(final I pInput)
	{
		return null;
	}

	@Override
	public void close() throws IOException
	{
	}

	@Override
	public void connectToReceiver(final AsynchronousProcessorInterface<O, ?> pAsynchronousProcessor)
	{
	}

	@Override
	public boolean start()
	{
		return true;
	}

	@Override
	public boolean stop()
	{
		return true;
	}

	@Override
	public boolean stop(final long pTimeOut, TimeUnit pTimeUnit)
	{
		return true;
	}

	@Override
	public boolean passOrWait(final I pObject)
	{
		return true;
	}

	@Override
	public boolean passOrWait(	final I pObject,
								final long pTimeOut,
								TimeUnit pTimeUnit)
	{
		return true;
	}

	@Override
	public boolean passOrFail(final I pObject)
	{
		return true;
	}

	@Override
	public boolean waitToFinish(final long pTime, TimeUnit pTimeUnit)
	{
		return true;
	}

	@Override
	public int getInputQueueLength()
	{
		return 0;
	}

	@Override
	public int getRemainingCapacity()
	{
		return 0;
	}

}
