package autopilot.fmatrix.constraingraph.constrains;

import autopilot.fmatrix.constraingraph.variables.ConstantVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;

/**
 * This class represents constrains of the form: V1 + V2 + ... + Vk = C
 * 
 * @author Loic Royer
 * 
 */
public class ConstrainSumConstantEquality extends Constrain
{

	/**
	 * Constructs a {@link ConstrainSumConstantEquality} constrain from a list V1,
	 * ..., Vk of state variables and a constant C.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pConstantVariable
	 *          constant variable
	 * @param pStateVariables
	 *          state variable
	 */
	public ConstrainSumConstantEquality(final String pConstrainName,
																			final ConstantVariable pConstantVariable,
																			final StateVariable... pStateVariables)
	{
		super(pConstrainName);
		final ConstantVariable lConstrainVariable = pConstantVariable;
		setConstrainVariable(lConstrainVariable);
		for (final StateVariable lStateVariable : pStateVariables)
		{
			addStateVariable(lStateVariable);
		}

	}

	/**
	 * Constructs a {@link ConstrainSumConstantEquality} constrain from a list V1,
	 * ..., Vk of state variables and a constant double c.
	 * 
	 * @param pConstrainName
	 *          contrain name
	 * @param pConstantVariableValue
	 *          constant variable value
	 * @param pStateVariables
	 *          state variable
	 */
	public ConstrainSumConstantEquality(final String pConstrainName,
																			final double pConstantVariableValue,
																			final StateVariable... pStateVariables)
	{
		this(	pConstrainName,
					new ConstantVariable(	pConstrainName + "ConstantValue",
																pConstantVariableValue),
					pStateVariables);
	}

	public final double getValue()
	{
		return getConstrainVariable().getValue();
	}

}
