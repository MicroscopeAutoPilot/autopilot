package autopilot.fmatrix.constraingraph.variables;

import autopilot.fmatrix.constraingraph.Named;

/**
 * Superclass for all variables in a constrain graph.
 * 
 * @author royer
 */
public class Variable extends Named
{
	private volatile double mValue;

	/**
	 * Constructs a Variable given a name.
	 * 
	 * @param pName
	 *          variable name
	 */
	public Variable(final String pName)
	{
		super(pName);
	}

	/**
	 * Sets the value of a variable.
	 * 
	 * @param pValue
	 *          value
	 */
	public void setValue(final double pValue)
	{
		mValue = pValue;
	}

	/**
	 * Gets the value of a variable.
	 * 
	 * @return value
	 */
	public double getValue()
	{
		return mValue;
	}

	@Override
	public String toString()
	{
		return String.format(	"Variable [mName=%s, mValue=%s]",
													getName(),
													mValue);
	}
}
