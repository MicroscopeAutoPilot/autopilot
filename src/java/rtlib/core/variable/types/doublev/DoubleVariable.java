package rtlib.core.variable.types.doublev;

import rtlib.core.units.Magnitude;
import rtlib.core.units.SIUnit;
import rtlib.core.variable.exceptions.InvalidMagnitudeException;
import rtlib.core.variable.types.objectv.ObjectVariable;

public class DoubleVariable extends ObjectVariable<Double>	implements
															DoubleVariableInterface

{
	private final SIUnit mSIUnit;
	private final Magnitude mMagnitude;

	public DoubleVariable(final String pVariableName)
	{
		this(pVariableName, 0);
	}

	public DoubleVariable(	final String pVariableName,
							final double pDoubleValue)
	{
		this(pVariableName, SIUnit.ArbitraryUnit, pDoubleValue);
	}

	public DoubleVariable(	final String pVariableName,
							final SIUnit pSIUnit,
							final double pDoubleValue)
	{
		this(	pVariableName,
				pSIUnit,
				pSIUnit.getMagnitude(),
				pDoubleValue);
	}

	public DoubleVariable(	final String pVariableName,
							final SIUnit pSIUnit,
							final Magnitude pMagnitude)
	{
		this(pVariableName, pSIUnit, pMagnitude, 0);
	}

	public DoubleVariable(	final String pVariableName,
							final SIUnit pSIUnit,
							final Magnitude pMagnitude,
							final double pDoubleValue)
	{
		super(pVariableName, pDoubleValue);
		if (!pSIUnit.isBaseUnit() && pMagnitude != pSIUnit.getMagnitude())
			throw new UnsupportedOperationException("To avoid confusion you cannot specify a non-base unit with a different magnitude.");
		mSIUnit = pSIUnit;
		mMagnitude = pMagnitude;
	}

	@Override
	public SIUnit getUnit()
	{
		return mSIUnit;
	}

	@Override
	public Magnitude getMagnitude()
	{
		return mMagnitude;
	}

	@Override
	public void setValue(double pValue, Magnitude pMagnitude)
	{
		setValue(mMagnitude.convertFrom(pValue, pMagnitude));
	}

	@Override
	public double getValue(Magnitude pMagnitude)
	{
		return pMagnitude.convertFrom(getValue(), mMagnitude);
	}

	@Override
	public void setValue(double pNewValue)
	{
		setReference(pNewValue);
	}

	@Override
	public double getValue()
	{
		return getReference();
	}

	@Override
	public void sendUpdatesTo(DoubleVariable pVariable)
	{
		preventMismatchedMagnitudes(pVariable);
		super.sendUpdatesTo(pVariable);
	}

	@Override
	public void doNotSendUpdatesTo(DoubleVariable pVariable)
	{
		preventMismatchedMagnitudes(pVariable);
		super.doNotSendUpdatesTo(pVariable);
	}

	@Override
	public void syncWith(DoubleVariable pVariable)
	{
		preventMismatchedMagnitudes(pVariable);
		super.syncWith(pVariable);
	}

	@Override
	public void doNotSyncWith(DoubleVariable pVariable)
	{
		preventMismatchedMagnitudes(pVariable);
		super.doNotSyncWith(pVariable);
	}

	private void preventMismatchedMagnitudes(DoubleVariable pVariable)
	{
		// NOTE: Right now we prevent syncing of variables of different
		// magnitudes,
		// although in principle we could accommodate for that.
		// however, this complicates things and may be confusing. It forces the
		// user
		// to decide on a base unit and use that throughout.
		// This acts as a kind of type checking that will prevent bugs and
		// complexity from getting too high (synced variables that have all
		// sorts
		// of magnitudes...)
		if (getMagnitude() != pVariable.getMagnitude())
			throw new InvalidMagnitudeException(this, pVariable);
	}

}
