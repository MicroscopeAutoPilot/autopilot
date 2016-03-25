package rtlib.core.device;

public class OpenCloseDeviceAdapter	implements
									OpenCloseDeviceInterface
{
	@Override
	public boolean open()
	{
		return true;
	}

	@Override
	public boolean close()
	{
		return true;
	}
}
