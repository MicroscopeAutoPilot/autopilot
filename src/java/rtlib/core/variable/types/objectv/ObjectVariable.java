package rtlib.core.variable.types.objectv;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import rtlib.core.variable.EventPropagator;
import rtlib.core.variable.NamedVariable;
import rtlib.core.variable.ObjectVariableInterface;

public class ObjectVariable<O> extends NamedVariable<O>	implements
														ObjectVariableInterface<O>,
														ObjectInputOutputVariableInterface<O>
{
	protected volatile O mReference;
	protected final CopyOnWriteArrayList<ObjectVariable<O>> mVariablesToSendUpdatesTo = new CopyOnWriteArrayList<ObjectVariable<O>>();

	public ObjectVariable(final String pVariableName)
	{
		super(pVariableName);
		mReference = null;
	}

	public ObjectVariable(	final String pVariableName,
							final O pReference)
	{
		super(pVariableName);
		mReference = pReference;
	}

	@Override
	public void setCurrent()
	{
		EventPropagator.clear();
		setReference(mReference);
	}

	public void setCurrentInternal()
	{
		setReference(mReference);
	}

	@Override
	public void set(final O pNewReference)
	{
		setReference(pNewReference);
	}

	@Override
	public void setReference(final O pNewReference)
	{
		EventPropagator.clear();
		setReferenceInternal(pNewReference);
	}

	public boolean setReferenceInternal(final O pNewReference)
	{
		if (EventPropagator.hasBeenTraversed(this))
		{
			return false;
		}

		final O lNewValueAfterHook = setEventHook(	mReference,
													pNewReference);

		EventPropagator.add(this);
		if (mVariablesToSendUpdatesTo != null)
		{
			for (final ObjectVariable<O> lObjectVariable : mVariablesToSendUpdatesTo)
			{
				if (EventPropagator.hasNotBeenTraversed(lObjectVariable))
				{
					lObjectVariable.setReferenceInternal(lNewValueAfterHook);
				}
			}
		}

		final O lOldReference = mReference;
		mReference = lNewValueAfterHook;

		notifyListenersOfSetEvent(lOldReference, lNewValueAfterHook);

		return true;
	}

	public void sync(final O pNewValue, final boolean pClearEventQueue)
	{
		if (pClearEventQueue)
		{
			EventPropagator.clear();
		}

		// We protect ourselves from called code that might clear the Thread
		// traversal list:
		final ArrayList<Object> lCopyOfListOfTraversedObjects = EventPropagator.getCopyOfListOfTraversedObjects();

		if (mVariablesToSendUpdatesTo != null)
		{
			for (final ObjectVariable<O> lObjectVariable : mVariablesToSendUpdatesTo)
			{
				EventPropagator.setListOfTraversedObjects(lCopyOfListOfTraversedObjects);
				if (EventPropagator.hasNotBeenTraversed(lObjectVariable))
				{
					lObjectVariable.setReferenceInternal(pNewValue);
				}
			}
		}
		EventPropagator.setListOfTraversedObjects(lCopyOfListOfTraversedObjects);
		EventPropagator.addAllToListOfTraversedObjects(mVariablesToSendUpdatesTo);

	}

	public O setEventHook(final O pOldValue, final O pNewValue)
	{
		return pNewValue;
	}

	public O getEventHook(final O pCurrentReference)
	{
		return pCurrentReference;
	}

	@Override
	public O getReference()
	{
		final O lNewReferenceAfterHook = getEventHook(mReference);
		notifyListenersOfGetEvent(lNewReferenceAfterHook);
		return lNewReferenceAfterHook;
	}

	@Override
	public O get()
	{
		return getReference();
	}

	@Override
	public void sendUpdatesTo(final ObjectVariable<O> pObjectVariable)
	{
		if (!mVariablesToSendUpdatesTo.contains(pObjectVariable))
			mVariablesToSendUpdatesTo.add(pObjectVariable);
	}

	@Override
	public void doNotSendUpdatesTo(final ObjectVariable<O> pObjectVariable)
	{
		mVariablesToSendUpdatesTo.remove(pObjectVariable);
	}

	@Override
	public void doNotSendAnyUpdates()
	{
		mVariablesToSendUpdatesTo.clear();
	}

	public ObjectVariable<O> sendUpdatesToInstead(ObjectVariable<O> pObjectVariable)
	{

		ObjectVariable<O> lObjectVariable = null;
		if (mVariablesToSendUpdatesTo.size() == 0)
		{
			if (pObjectVariable == null)
				return null;
			mVariablesToSendUpdatesTo.add(pObjectVariable);
		}
		else if (mVariablesToSendUpdatesTo.size() == 1)
		{
			if (pObjectVariable == null)
				return mVariablesToSendUpdatesTo.get(0);
			lObjectVariable = mVariablesToSendUpdatesTo.get(0);
			mVariablesToSendUpdatesTo.set(0, pObjectVariable);
		}
		else if (mVariablesToSendUpdatesTo.size() > 1)
		{
			if (pObjectVariable == null)
				return mVariablesToSendUpdatesTo.get(0);

			lObjectVariable = mVariablesToSendUpdatesTo.get(0);
			mVariablesToSendUpdatesTo.clear();
			mVariablesToSendUpdatesTo.add(pObjectVariable);
		}

		return lObjectVariable;
	}

	@Override
	public void syncWith(final ObjectVariable<O> pObjectVariable)
	{
		this.sendUpdatesTo(pObjectVariable);
		pObjectVariable.sendUpdatesTo(this);
	}

	@Override
	public void doNotSyncWith(final ObjectVariable<O> pObjectVariable)
	{
		this.doNotSendUpdatesTo(pObjectVariable);
		pObjectVariable.doNotSendUpdatesTo(this);
	}

	public boolean isNotNull()
	{
		return mReference != null;
	}

	public boolean isNull()
	{
		return mReference == null;
	}

	@Override
	public String toString()
	{
		try
		{
			return getName() + "="
					+ ((mReference == null)	? "null"
											: mReference.toString());
		}
		catch (final NullPointerException e)
		{
			return getName() + "=null";
		}
	}

}
