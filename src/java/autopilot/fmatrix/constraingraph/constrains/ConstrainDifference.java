package autopilot.fmatrix.constraingraph.constrains;

import autopilot.fmatrix.constraingraph.variables.ObservableVariable;
import autopilot.fmatrix.constraingraph.variables.StateVariable;

/**
 * This class represents constrains of the form: V1 - V2 = O. Where V1 and V2
 * are two state variables and O is an observable.
 * 
 * @author Loic Royer
 * 
 */
public class ConstrainDifference extends Constrain
{

	/**
	 * Constructs a {@link ConstrainDifference} (internal use)
	 * 
	 * @param pConstrainName
	 *          constrain name
	 */
	protected ConstrainDifference(final String pConstrainName)
	{
		super(pConstrainName);
	}

	/**
	 * Constructs a {@link ConstrainDifference} constrain from an observable
	 * variable O and two state variables V1 and V2.
	 * 
	 * @param pConstrainName
	 *          constrain name
	 * @param pConstrainObservableVariable
	 *          observable variable
	 * @param pV1
	 *          first variable
	 * @param pV2
	 *          second variable
	 */
	public ConstrainDifference(	final String pConstrainName,
															final ObservableVariable pConstrainObservableVariable,
															final StateVariable pV1,
															final StateVariable pV2)
	{
		super(pConstrainName);
		setConstrainVariable(pConstrainObservableVariable);
		addStateVariable(pV1);
		addStateVariable(pV2);
	}

	/**
	 * Returns the observable variable O.
	 * 
	 * @return observable variable
	 */
	public ObservableVariable getObservableVariable()
	{
		return (ObservableVariable) getConstrainVariable();
	}

}
