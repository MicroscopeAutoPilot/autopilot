package rtlib.core.concurrent.asyncprocs;

public class AsynchronousProcessor<I, O>	extends
											AsynchronousProcessorBase<I, O>
{

	private ProcessorInterface<I, O> mProcessor;

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public AsynchronousProcessor(	String pName,
									int pMaxQueueSize,
									final ProcessorInterface pProcessor)
	{
		super(pName, pMaxQueueSize);
		mProcessor = pProcessor;
	}

	@Override
	public O process(I pInput)
	{
		return mProcessor.process(pInput);
	}

}
