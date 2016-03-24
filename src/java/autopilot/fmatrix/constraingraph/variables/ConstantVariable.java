package autopilot.fmatrix.constraingraph.variables;

/**
 * Constant variables encapsulate a single double value.
 * 
 * @author royer
 */
public class ConstantVariable extends Variable
{

	/**
	 * Constructs a constant variable around a given value
	 * 
	 * @param pName
	 *          name of constant variable
	 * @param pValue
	 *          constant value
	 */
	public ConstantVariable(final String pName, final double pValue)
	{
		super(pName);
		setValue(pValue);
	}

}
