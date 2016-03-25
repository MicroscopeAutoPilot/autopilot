package rtlib.core.device;

public abstract class BaseDisplayDevice extends SignalStartableDevice	implements
																		OpenCloseDeviceInterface,
																		DisplayableInterface
{

	public BaseDisplayDevice(final String pDeviceName)
	{
		super(pDeviceName);
	}

}
