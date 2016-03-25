package rtlib.core.device;

public abstract class NameableAbstract implements NameableInterface
{
	private String mName;

	@SuppressWarnings("unused")
	private NameableAbstract()
	{
		super();
	}

	public NameableAbstract(final String pName)
	{
		super();
		mName = pName;
	}

	/* (non-Javadoc)
	 * @see rtlib.core.device.NameableInterface#getName()
	 */
	@Override
	public String getName()
	{
		return mName;
	}

	/* (non-Javadoc)
	 * @see rtlib.core.device.NameableInterface#setName(java.lang.String)
	 */
	@Override
	public void setName(final String name)
	{
		mName = name;
	}

	@Override
	public String toString()
	{
		return String.format("NameableAbstract [mName=%s]", mName);
	}
}
