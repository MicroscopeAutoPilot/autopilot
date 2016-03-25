package rtlib.core.concurrent.asyncprocs;

public interface ProcessorInterface<I, O> extends AutoCloseable
{

	public O process(I pInput);

}
