package autopilot.fmatrix.constraingraph.constrains;

import autopilot.fmatrix.constraingraph.variables.ConstantVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;

/**
 * This class represents constrains of the form: V1 + V3 + ... = V2 + V4 + ...
 * 
 * @author Loic Royer
 * 
 */
public class ConstrainSumEquality extends Constrain
{

	/**
	 * Constructs a {@link ConstrainSumEquality} constrain from an alternated list
	 * of state variables. Odd indexed variables belong to the left hand side and
	 * even numbered variables to the right hand side.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pStateVariables
	 *          state variable
	 */
	public ConstrainSumEquality(final String pConstrainName,
															final StateVariable... pStateVariables)
	{
		super(pConstrainName);
		final ConstantVariable lConstrainVariable = new ConstantVariable(	pConstrainName + "ConstantValue",
																																			0);
		setConstrainVariable(lConstrainVariable);
		for (final StateVariable lStateVariable : pStateVariables)
		{
			addStateVariable(lStateVariable);
		}

	}

}
