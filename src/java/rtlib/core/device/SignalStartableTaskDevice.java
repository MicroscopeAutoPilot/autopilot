package rtlib.core.device;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.log.Loggable;
import rtlib.core.variable.types.booleanv.BooleanEventListenerInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;

public abstract class SignalStartableTaskDevice	extends
												SignalStartableDevice	implements
																		OpenCloseDeviceInterface,
																		AsynchronousExecutorServiceAccess,
																		Loggable,
																		Runnable
{

	private final SignalStartableTaskDevice lThis;

	protected final BooleanVariable mCancelBooleanVariable;

	protected volatile boolean mCanceledSignal = false;

	public SignalStartableTaskDevice(final String pDeviceName)
	{
		super(pDeviceName, true);
		lThis = this;

		mCancelBooleanVariable = new BooleanVariable(	pDeviceName + "Cancel",
														false);

		mCancelBooleanVariable.addEdgeListener(new BooleanEventListenerInterface()
		{

			@Override
			public void fire(final boolean pCurrentBooleanValue)
			{
				if (pCurrentBooleanValue)
				{
					mCanceledSignal = true;
				}
			}
		});
	}

	@Override
	public abstract void run();

	@Override
	public boolean start()
	{
		clearCanceled();
		Future<?> lExecuteAsynchronously = executeAsynchronously(this);
		return lExecuteAsynchronously != null;
	}

	public boolean pause()
	{

		return true;
	}

	public boolean resume()
	{

		return true;
	}

	@Override
	public boolean stop()
	{
		try
		{
			return waitForCompletion();
		}
		catch (ExecutionException e)
		{
			String lError = "Error during previous execution of loop function!";
			severe("Device", lError, e);
			return false;
		}
	}

	public BooleanVariable getIsCanceledBooleanVariable()
	{
		return mCancelBooleanVariable;
	}

	public void clearCanceled()
	{
		mCancelBooleanVariable.setValue(false);
		mCanceledSignal = false;
	}

	public boolean isCanceled()
	{
		return mCanceledSignal;
	}

}
