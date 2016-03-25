package rtlib.core.concurrent.asyncprocs;

import java.util.concurrent.TimeUnit;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.variable.types.objectv.ObjectVariable;

public class ObjectVariableAsynchronousPooledProcessor<I, O>	implements
																OpenCloseDeviceInterface,
																StartStopDeviceInterface
{
	private static final long cTimeOutInSeconds = 1;

	private final ObjectVariable<I> mInputObjectVariable;
	private final ObjectVariable<O> mOutputObjectVariable;

	private final AsynchronousProcessorPool<I, O> mAsynchronousProcessorPool;

	public ObjectVariableAsynchronousPooledProcessor(	final String pName,
														final int pMaxQueueSize,
														final int pThreadPoolSize,
														final ProcessorInterface<I, O> pProcessor,
														final boolean pDropIfQueueFull)
	{
		super();

		mAsynchronousProcessorPool = new AsynchronousProcessorPool<I, O>(	pName,
																			pMaxQueueSize,
																			pThreadPoolSize,
																			pProcessor);

		mOutputObjectVariable = new ObjectVariable<O>(pName + "Output");

		mInputObjectVariable = new ObjectVariable<I>(pName + "Input")
		{
			@Override
			public void setReference(final I pNewReference)
			{
				if (pDropIfQueueFull)
				{
					mAsynchronousProcessorPool.passOrFail(pNewReference);
				}
				else
				{
					mAsynchronousProcessorPool.passOrWait(pNewReference);
				}
			}
		};

		final AsynchronousProcessorBase<O, O> lConnector = new AsynchronousProcessorBase<O, O>(	"AsynchronousProcessorPool->OutputObjectVariable",
																								pMaxQueueSize)
		{

			@Override
			public O process(final O pInput)
			{
				mOutputObjectVariable.setReference(pInput);
				return null;
			}
		};

		lConnector.start();
		mAsynchronousProcessorPool.connectToReceiver(lConnector);

	}

	public ObjectVariable<I> getInputObjectVariable()
	{
		return mInputObjectVariable;
	}

	public ObjectVariable<O> getOutputObjectVariable()
	{
		return mOutputObjectVariable;
	}

	@Override
	public boolean open()
	{
		return true;
	}

	@Override
	public boolean start()
	{
		return mAsynchronousProcessorPool.start();
	}

	@Override
	public boolean stop()
	{
		return mAsynchronousProcessorPool.stop(	cTimeOutInSeconds,
												TimeUnit.SECONDS);
	}

	@Override
	public boolean close()
	{
		mAsynchronousProcessorPool.close();
		return true;
	}

}
