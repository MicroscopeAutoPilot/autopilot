package rtlib.core.variable;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class NamedVariable<O>
{

	private String mVariableName;

	private final CopyOnWriteArrayList<VariableSetListener<O>> mVariableSetListeners = new CopyOnWriteArrayList<VariableSetListener<O>>();
	private final CopyOnWriteArrayList<VariableGetListener<O>> mVariableGetListeners = new CopyOnWriteArrayList<VariableGetListener<O>>();

	public NamedVariable(final String pVariableName)
	{
		super();
		mVariableName = pVariableName;
	}

	public void addListener(final VariableListener<O> pVariableListener)
	{
		mVariableSetListeners.add(pVariableListener);
		mVariableGetListeners.add(pVariableListener);
	}

	public void removeListener(final VariableListener<O> pVariableListener)
	{
		mVariableSetListeners.remove(pVariableListener);
		mVariableGetListeners.remove(pVariableListener);
	}

	public void addSetListener(final VariableSetListener<O> pVariableSetListener)
	{
		mVariableSetListeners.add(pVariableSetListener);
	}

	public void addGetListener(final VariableGetListener<O> pVariableGetListener)
	{
		mVariableGetListeners.add(pVariableGetListener);
	}

	public void removeSetListener(final VariableSetListener<O> pVariableSetListener)
	{
		mVariableSetListeners.remove(pVariableSetListener);
	}

	public void removeGetListener(final VariableGetListener<O> pVariableGetListener)
	{
		mVariableGetListeners.remove(pVariableGetListener);
	}

	public void removeAllSetListeners()
	{
		mVariableSetListeners.clear();
	}

	public void removeAllGetListeners()
	{
		mVariableGetListeners.clear();
	}

	public void removeAllListeners()
	{
		mVariableSetListeners.clear();
		mVariableGetListeners.clear();
	}

	public CopyOnWriteArrayList<VariableSetListener<O>> getVariableSetListeners()
	{
		return mVariableSetListeners;
	}

	public CopyOnWriteArrayList<VariableGetListener<O>> getVariableGetListeners()
	{
		return mVariableGetListeners;
	}

	public void notifyListenersOfSetEvent(	final O pCurentValue,
											final O pNewValue)
	{
		for (final VariableSetListener<O> lVariableListener : getVariableSetListeners())
		{
			lVariableListener.setEvent(pCurentValue, pNewValue);
		}
	}

	public void notifyListenersOfGetEvent(final O pCurrentValue)
	{
		for (final VariableGetListener<O> lVariableListener : getVariableGetListeners())
		{
			lVariableListener.getEvent(pCurrentValue);
		}
	}

	public String getName()
	{
		return mVariableName;
	}

	public void setVariableName(final String variableName)
	{
		mVariableName = variableName;
	}

	public abstract O get();

}
