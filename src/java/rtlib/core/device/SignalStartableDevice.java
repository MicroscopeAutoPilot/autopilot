package rtlib.core.device;

import rtlib.core.variable.types.booleanv.BooleanEventListenerInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;

public abstract class SignalStartableDevice	extends
											NamedVirtualDevice	implements
																OpenCloseDeviceInterface,
																StartStopDeviceInterface
{

	protected final BooleanVariable mStartSignal;

	protected final BooleanVariable mStopSignal;

	public SignalStartableDevice(final String pDeviceName)
	{
		this(pDeviceName, false);
	}

	public SignalStartableDevice(	final String pDeviceName,
									final boolean pOnlyStart)
	{
		super(pDeviceName);

		mStartSignal = new BooleanVariable(	pDeviceName + "Start",
											false);

		mStopSignal = new BooleanVariable(pDeviceName + "Stop", false);

		mStartSignal.addEdgeListener(new BooleanEventListenerInterface()
		{
			@Override
			public void fire(final boolean pCurrentBooleanValue)
			{
				if (pCurrentBooleanValue)
				{
					start();
				}
			}
		});

		if (!pOnlyStart)
		{
			mStopSignal.addEdgeListener(new BooleanEventListenerInterface()
			{
				@Override
				public void fire(final boolean pCurrentBooleanValue)
				{
					if (pCurrentBooleanValue)
					{
						stop();
					}
				}
			});
		}
	}

	public BooleanVariable getStartSignalBooleanVariable()
	{
		return mStartSignal;
	}

	public BooleanVariable getStopSignalBooleanVariable()
	{
		return mStopSignal;
	}

	@Override
	public abstract boolean start();

	@Override
	public abstract boolean stop();

}
