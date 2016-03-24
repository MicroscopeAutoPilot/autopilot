package autopilot.fmatrix.constraingraph.constrains;

import java.util.ArrayList;

import autopilot.fmatrix.constraingraph.Named;
import autopilot.fmatrix.constraingraph.variables.ConstantVariable;
import autopilot.fmatrix.constraingraph.variables.ObservableVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;
import autopilot.fmatrix.constraingraph.variables.Variable;

/**
 * Base class for constrains. Contains fields and methods common to all
 * constrains.
 * 
 * @author royer
 * 
 */
public class Constrain extends Named
{

	private final ArrayList<StateVariable> mStateVariableList = new ArrayList<StateVariable>();
	private Variable mConstrainVariable = null;
	private double mWeight = 1;
	private boolean mRelative = true;

	/**
	 * Constructs a constrain given a name.
	 * 
	 * @param pName
	 *          name
	 */
	public Constrain(final String pName)
	{
		super(pName);
	}

	/**
	 * Returns the weight of the constrain.
	 * 
	 * @return weight
	 */
	public double getWeight()
	{
		return mWeight;
	}

	/**
	 * Sets the weight of the constrain
	 * 
	 * @param pWeight
	 *          weight
	 */
	public void setWeight(final double pWeight)
	{
		mWeight = pWeight;
	}

	/**
	 * Adds the given state variable to the constrain.
	 * 
	 * @param pStateVariable
	 *          state variable
	 */
	public final void addStateVariable(final StateVariable pStateVariable)
	{
		mStateVariableList.add(pStateVariable);
	}

	/**
	 * Returns the list of state variables belonging to that constrain.
	 * 
	 * @return state variable list.
	 */
	public final ArrayList<StateVariable> getStateVariableList()
	{
		return mStateVariableList;
	}

	/**
	 * Returns the state variable at the given index.
	 * 
	 * @param pIndex
	 *          state variable index position in list
	 * @return state variable
	 */
	public StateVariable getVariable(final int pIndex)
	{
		return mStateVariableList.get(pIndex);
	}

	/**
	 * Convenience method that returns the first state variable.
	 * 
	 * @return first state variable.
	 */
	public StateVariable getVariable1()
	{
		return mStateVariableList.get(0);
	}

	/**
	 * Convenience method that returns the second state variable.
	 * 
	 * @return second state variable.
	 */
	public StateVariable getVariable2()
	{
		return mStateVariableList.get(1);
	}

	/**
	 * Sets the constrain's main variable.
	 * 
	 * @param pConstrainVariable
	 *          constrain variable
	 */
	public final void setConstrainVariable(final Variable pConstrainVariable)
	{
		mConstrainVariable = pConstrainVariable;
	}

	/**
	 * Returns the constrain's main variable.
	 * 
	 * @return main variable.
	 */
	public final Variable getConstrainVariable()
	{
		return mConstrainVariable;
	}

	/**
	 * Returns true if the constrain is a constant constrain.
	 * 
	 * @return true if constant
	 */
	public final boolean isConstant()
	{
		return mConstrainVariable instanceof ConstantVariable;
	}

	/**
	 * Returns true if the constrain is observable.
	 * 
	 * @return true if observable
	 */
	public final boolean isObservable()
	{
		return mConstrainVariable instanceof ObservableVariable;
	}

	/**
	 * Returns true if constrain is a relative constrain.
	 * 
	 * @return true if relative
	 */
	public boolean isRelative()
	{
		return mRelative;
	}

	/**
	 * Returns true if the constrain is absolute.
	 * 
	 * @return true if absolute
	 */
	public boolean isAbsolute()
	{
		return !mRelative;
	}

	/**
	 * Sets whether the constrain is relative or absolute.
	 * 
	 * @param pRelative
	 *          true if relative, false if absolute.
	 */
	public void setRelative(final boolean pRelative)
	{
		mRelative = pRelative;
	}

	/**
	 * Returns true if the constrain is observable and if it is missing an
	 * observation.
	 * 
	 * @return true if constrain observable and if missing observable
	 */
	public boolean isMissingObservation()
	{
		if (isObservable())
		{
			return ((ObservableVariable) mConstrainVariable).isMissing();
		}
		return false;
	}

	@Override
	public String toString()
	{
		return String.format(	"Constrain [getName()=%s, mStateVariableList=%s, mConstrainVariable=%s]",
													getName(),
													mStateVariableList,
													mConstrainVariable);
	}

}
