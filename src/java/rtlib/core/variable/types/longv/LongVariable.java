package rtlib.core.variable.types.longv;

import rtlib.core.variable.types.objectv.ObjectVariable;

public class LongVariable extends ObjectVariable<Long>	implements
														LongVariableInterface

{

	public LongVariable(final String pVariableName)
	{
		this(pVariableName, 0);
	}

	public LongVariable(final String pVariableName,
						final long pIntValue)
	{
		super(pVariableName, pIntValue);
	}

	@Override
	public void setValue(long pNewValue)
	{
		setReference(pNewValue);
	}

	@Override
	public long getValue()
	{
		return getReference();
	}

	@Override
	public void sendUpdatesTo(LongVariable pVariable)
	{
		super.sendUpdatesTo(pVariable);
	}

	@Override
	public void doNotSendUpdatesTo(LongVariable pVariable)
	{
		super.doNotSendUpdatesTo(pVariable);
	}

	@Override
	public void syncWith(LongVariable pVariable)
	{
		super.syncWith(pVariable);
	}

	@Override
	public void doNotSyncWith(LongVariable pVariable)
	{
		super.doNotSyncWith(pVariable);
	}

}
