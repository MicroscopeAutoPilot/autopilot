package autopilot.fmatrix.constraingraph.constrains;

import autopilot.fmatrix.constraingraph.variables.ConstantVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;

/**
 * This class represents constrains of the form: Vavg = (V1 + V2 + ... +Vk)/k
 * 
 * @author Loic Royer
 * 
 */
public class ConstrainHarmonic extends ConstrainDifference
{

	public static final double[] cFilterCoefs = new double[]
	{ -1.0 / 6, 4.0 / 6, 4.0 / 6, 1.0 / 6 };

	/**
	 * Constructs a {@link ConstrainHarmonic} constrain of the form V(i) =
	 * -(1/6)*V(i-2) + (4/6)*V(i-1) + (4/6)*V(i+1) -(1/6)*V(i+2).
	 * 
	 * Where V(i-2), V(i-1), V(i+1), V(i+2) are variables with know values and
	 * V(i) is a variable that needs to be constrained using a harmonic constrain.
	 * Harmonic constrains are usefull to 'fill-in' missing information assuming a
	 * constant second order derivative.
	 * 
	 * The 'harmonic' naming comes from harmonic surfaces that are solutions to
	 * Laplace's equation. (https://en.wikipedia.org/wiki/Harmonic_function)
	 * 
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pVi
	 *          variable that will be filled-in
	 * @param pStateVariables
	 *          state variables
	 */
	public ConstrainHarmonic(	final String pConstrainName,
														final StateVariable pVi,
														final StateVariable... pStateVariables)
	{
		super(pConstrainName);
		final ConstantVariable lConstrainVariable = new ConstantVariable(	pConstrainName,
																																			0);
		setConstrainVariable(lConstrainVariable);
		addStateVariable(pVi);
		for (final StateVariable lStateVariable : pStateVariables)
		{
			addStateVariable(lStateVariable);
		}
	}
}
