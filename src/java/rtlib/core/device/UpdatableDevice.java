package rtlib.core.device;

public abstract class UpdatableDevice extends NamedVirtualDevice implements
																UpdatableInterface
{

	private volatile boolean mIsUpToDate = false;

	public UpdatableDevice(String pDeviceName)
	{
		super(pDeviceName);
	}

	/* (non-Javadoc)
	 * @see rtlib.core.device.UpdatableInterface#ensureIsUpToDate()
	 */
	@Override
	public abstract void ensureIsUpToDate();

	/* (non-Javadoc)
	 * @see rtlib.core.device.UpdatableInterface#isUpToDate()
	 */
	@Override
	public boolean isUpToDate()
	{
		return mIsUpToDate;
	}

	/* (non-Javadoc)
	 * @see rtlib.core.device.UpdatableInterface#setUpToDate(boolean)
	 */
	@Override
	public void setUpToDate(boolean pIsUpToDate)
	{
		mIsUpToDate = pIsUpToDate;
	}

	/* (non-Javadoc)
	 * @see rtlib.core.device.UpdatableInterface#requestUpdate()
	 */
	@Override
	public void requestUpdate()
	{
		mIsUpToDate = false;
	}
}
