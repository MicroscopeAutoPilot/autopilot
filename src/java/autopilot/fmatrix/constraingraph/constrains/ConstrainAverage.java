package autopilot.fmatrix.constraingraph.constrains;

import autopilot.fmatrix.constraingraph.variables.ConstantVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;

/**
 * This class represents constrains of the form: Vavg = (V1 + V2 + ... +Vk)/k
 * 
 * @author Loic Royer
 * 
 */
public class ConstrainAverage extends ConstrainDifference
{

	/**
	 * Constructs a {@link ConstrainAverage} constrain from a state variable Vavg,
	 * and a list of state variables V1...Vk.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pVavg
	 *          average variable
	 * 
	 * @param pStateVariables
	 *          statve variables
	 */
	public ConstrainAverage(final String pConstrainName,
													final StateVariable pVavg,
													final StateVariable... pStateVariables)
	{
		super(pConstrainName);
		final ConstantVariable lConstrainVariable = new ConstantVariable(	pConstrainName,
																																			0);
		setConstrainVariable(lConstrainVariable);
		addStateVariable(pVavg);
		for (final StateVariable lStateVariable : pStateVariables)
		{
			addStateVariable(lStateVariable);
		}
	}
}
