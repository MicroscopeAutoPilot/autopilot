package rtlib.core.math.functions;

import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;

public class PolynomialFunction	extends
																org.apache.commons.math3.analysis.polynomials.PolynomialFunction
{

	private static final long serialVersionUID = 1L;

	public PolynomialFunction()	throws NullArgumentException,
															NoDataException
	{
		super(new double[]
		{ 0 });
	}

	public PolynomialFunction(double[] pC) throws NullArgumentException,
																				NoDataException
	{
		super(pC);
	}

}
