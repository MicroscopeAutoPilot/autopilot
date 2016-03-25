package rtlib.core.concurrent.asyncprocs;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

public interface AsynchronousProcessorInterface<I, O>	extends
														ProcessorInterface<I, O>,
														Closeable
{

	public void connectToReceiver(AsynchronousProcessorInterface<O, ?> pAsynchronousProcessor);

	public boolean start();

	public boolean passOrWait(I pObject);

	public boolean passOrWait(	I pObject,
								final long pTimeOut,
								TimeUnit pTimeUnit);

	public boolean passOrFail(I pObject);

	public boolean waitToFinish(final long pTimeOut,
								TimeUnit pTimeUnit);

	public boolean stop();

	public boolean stop(final long pTimeOut, TimeUnit pTimeUnit);

	public int getInputQueueLength();

	public int getRemainingCapacity();

}
