package rtlib.core.concurrent.asyncprocs;

public class AsynchronousProcessorToString<I>	extends
												AsynchronousProcessorBase<I, String> implements
																					AsynchronousProcessorInterface<I, String>
{

	public AsynchronousProcessorToString()
	{
		super("AsynchronousProcessorToString", 100);
	}

	@Override
	public String process(final I pInput)
	{
		final String lString = pInput.toString();
		System.out.println(lString);
		return lString;
	}

}
