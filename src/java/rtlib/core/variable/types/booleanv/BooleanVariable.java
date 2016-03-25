package rtlib.core.variable.types.booleanv;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rtlib.core.variable.VariableListener;
import rtlib.core.variable.types.doublev.DoubleVariable;

public class BooleanVariable extends DoubleVariable	implements
													BooleanInputOutputVariableInterface

{

	private CopyOnWriteArrayList<BooleanEventListenerInterface> mEdgeListenerList;
	private CopyOnWriteArrayList<BooleanEventListenerInterface> mLowToHighEdgeListenerList;
	private CopyOnWriteArrayList<BooleanEventListenerInterface> mHighToLowEdgeListenerList;

	public BooleanVariable(	final String pVariableName,
							final boolean pInitialState)
	{
		super(pVariableName, boolean2double(pInitialState));

		mEdgeListenerList = new CopyOnWriteArrayList<BooleanEventListenerInterface>();
		mLowToHighEdgeListenerList = new CopyOnWriteArrayList<BooleanEventListenerInterface>();
		mHighToLowEdgeListenerList = new CopyOnWriteArrayList<BooleanEventListenerInterface>();

		addListener(new VariableListener<Double>()
		{

			@Override
			public void getEvent(final Double pCurrentValue)
			{
			}

			@Override
			public void setEvent(	final Double pCurrentValue,
									final Double pNewValue)
			{
				final boolean lOldBooleanValue = double2boolean(pCurrentValue);
				final boolean lNewBooleanValue = double2boolean(pNewValue);

				if (lNewBooleanValue == lOldBooleanValue)
				{
					return;
				}

				for (final BooleanEventListenerInterface lEdgeListener : mEdgeListenerList)
				{
					lEdgeListener.fire(lNewBooleanValue);
				}

				if (lNewBooleanValue)
				{
					for (final BooleanEventListenerInterface lEdgeListener : mLowToHighEdgeListenerList)
					{
						lEdgeListener.fire(lNewBooleanValue);
					}
				}
				else if (!lNewBooleanValue)
				{
					for (final BooleanEventListenerInterface lEdgeListener : mHighToLowEdgeListenerList)
					{
						lEdgeListener.fire(lNewBooleanValue);
					}
				}
			}

		});
	}

	public void addEdgeListener(final BooleanEventListenerInterface pEdgeListener)
	{
		mEdgeListenerList.add(pEdgeListener);
	}

	public void removeEdgeListener(final BooleanEventListenerInterface pEdgeListener)
	{
		mEdgeListenerList.remove(pEdgeListener);
	}

	public void addLowToHighEdgelistener(final BooleanEventListenerInterface pLowToHighEdgeListener)
	{
		mLowToHighEdgeListenerList.add(pLowToHighEdgeListener);
	}

	public void removeLowToHighEdgelistener(final BooleanEventListenerInterface pLowToHighEdgeListener)
	{
		mLowToHighEdgeListenerList.add(pLowToHighEdgeListener);
	}

	public void addHighToLowEdgeWith(final BooleanEventListenerInterface pHighToLowEdgeListener)
	{
		mHighToLowEdgeListenerList.add(pHighToLowEdgeListener);
	}

	public void removeHighToLowEdgeWith(final BooleanEventListenerInterface pHighToLowEdgeListener)
	{
		mHighToLowEdgeListenerList.add(pHighToLowEdgeListener);
	}

	@Override
	public final void setValue(final boolean pNewBooleanValue)
	{
		setValue(boolean2double(pNewBooleanValue));
	}

	public final void toggle()
	{
		final double lOldValue = getValue();
		final double lNewToggledValue = lOldValue > 0 ? 0 : 1;

		setValue(lNewToggledValue);
	}

	public void setEdge(boolean pState)
	{
		setValue(!pState);
		setValue(pState);
	}

	protected void setBooleanValueInternal(final boolean pNewBooleanValue)
	{
		setReferenceInternal(boolean2double(pNewBooleanValue));
	}

	@Override
	public boolean getBooleanValue()
	{
		final double lValue = getValue();
		final boolean lBooleanValue = double2boolean(lValue);
		return lBooleanValue;
	}

	public static boolean double2boolean(final double pDoubleValue)
	{
		return pDoubleValue > 0;
	}

	public static double boolean2double(final boolean pBooleanValue)
	{
		return pBooleanValue ? 1 : 0;
	}

	public void waitForTrueAndToggleToFalse()
	{
		waitForStateAndToggle(true, 1, 20000, TimeUnit.MILLISECONDS);
	}

	public void waitForFalseAndToggle()
	{
		waitForStateAndToggle(false, 1, 20000, TimeUnit.MILLISECONDS);
	}

	public void waitForStateAndToggle(	final boolean pState,
										final long pMaxPollingPeriod,
										final long pTimeOut,
										final TimeUnit pTimeUnit)
	{
		System.out.println("waitForStateAndToggle");
		final CountDownLatch lIsTrueSignal = new CountDownLatch(1);
		final BooleanVariable lThis = this;
		final BooleanEventListenerInterface lBooleanEventListenerInterface = new BooleanEventListenerInterface()
		{
			@Override
			public void fire(final boolean pCurrentBooleanValue)
			{
				if (pCurrentBooleanValue == pState)
				{
					lIsTrueSignal.countDown();
					lThis.setValue(!pState);
					System.out.println("lThis.setValue(!pState);");
				}
			}
		};

		addEdgeListener(lBooleanEventListenerInterface);

		long lTimeOutCounter = 0;
		while (getBooleanValue() != pState)
		{
			System.out.println("while (getBooleanValue() != pState)");
			try
			{
				if (lIsTrueSignal.await(pMaxPollingPeriod, pTimeUnit))
				{
					System.out.println("System.out.println(while (getBooleanValue() != pState));");
					break;
				}
				else
				{
					System.out.println("lTimeOutCounter += pMaxPollingPeriod;");
					lTimeOutCounter += pMaxPollingPeriod;
					if (lTimeOutCounter >= pTimeOut)
					{
						break;
					}
				}
			}
			catch (final InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		removeEdgeListener(lBooleanEventListenerInterface);

		setValue(!pState);
	}

}
