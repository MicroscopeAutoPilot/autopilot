package rtlib.core.variable.types.objectv;

public class SingleUpdateTargetObjectVariable<O>	extends
													ObjectVariable<O>
{

	public SingleUpdateTargetObjectVariable(final String pVariableName)
	{
		super(pVariableName);
	}

	public SingleUpdateTargetObjectVariable(final String pVariableName,
											final O pReference)
	{
		super(pVariableName, pReference);
	}

	@Override
	public final void sendUpdatesTo(final ObjectVariable<O> pObjectVariable)
	{
		if (mVariablesToSendUpdatesTo.size() != 0)
		{
			throw new IllegalArgumentException(this.getClass()
													.getSimpleName() + ": cannot send updates to more  than one peer! (sending to one peer registered already)");
		}

		mVariablesToSendUpdatesTo.add(pObjectVariable);
	}

	public final ObjectVariable<O> sendUpdatesToInstead(final ObjectVariable<O> pObjectVariable)
	{
		if (mVariablesToSendUpdatesTo.size() >= 2)
		{
			throw new IllegalArgumentException(this.getClass()
													.getSimpleName() + ": cannot send updates to more than one peer! (more than 1 peer is registered already)");
		}

		mVariablesToSendUpdatesTo.clear();

		if (pObjectVariable == null)
		{
			if (mVariablesToSendUpdatesTo.isEmpty())
			{
				return null;
			}
			else
			{
				final ObjectVariable<O> lPreviousObjectVariable = mVariablesToSendUpdatesTo.get(0);
				return lPreviousObjectVariable;
			}
		}

		if (mVariablesToSendUpdatesTo.isEmpty())
		{
			mVariablesToSendUpdatesTo.add(pObjectVariable);
			return null;
		}
		else
		{
			final ObjectVariable<O> lPreviousObjectVariable = mVariablesToSendUpdatesTo.get(0);
			mVariablesToSendUpdatesTo.add(pObjectVariable);
			return lPreviousObjectVariable;
		}
	}

}
