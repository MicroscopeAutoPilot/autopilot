package autopilot.fmatrix.constraingraph;

import autopilot.fmatrix.constraingraph.variables.Variable;

/**
 * Super class containing common and basic code for handling the names of
 * constrains and variables.
 * 
 * @author royer
 */
public class Named
{

	private final String mName;

	/**
	 * Consructs a Named constrain or variable given a name.
	 * 
	 * @param pName
	 *          name.
	 */
	public Named(final String pName)
	{
		super();
		mName = pName;
	}

	/**
	 * Returns the name of the constrain or variable.
	 * 
	 * @return name
	 */
	public String getName()
	{
		return mName;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
							+ (getName() == null ? 0 : getName().hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final Variable other = (Variable) obj;
		if (getName() == null)
		{
			if (other.getName() != null)
			{
				return false;
			}
		}
		else if (!getName().equals(other.getName()))
		{
			return false;
		}
		return true;
	}

}
