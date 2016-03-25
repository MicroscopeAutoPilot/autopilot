package rtlib.core.concurrent.asyncprocs;

import java.util.concurrent.TimeUnit;

public class AsynchronousProcessorNull<I, O>	extends
												AsynchronousProcessorBase<I, O>	implements
																				AsynchronousProcessorInterface<I, O>
{

	public AsynchronousProcessorNull(	final String pName,
										final int pMaxQueueSize)
	{
		super(pName, pMaxQueueSize);
	}

	@Override
	public O process(final I pInput)
	{
		// Example: here is where the logic happens, here nothing happens and it
		// returns null
		return null;
	}

	@Override
	public boolean waitToFinish(final long pTime, TimeUnit pTimeUnit)
	{
		return true;
	}

}
