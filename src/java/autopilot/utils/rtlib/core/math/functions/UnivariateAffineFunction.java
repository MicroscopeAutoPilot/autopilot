package autopilot.utils.rtlib.core.math.functions;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.Serializable;

import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(
{ "slope", "constant", "min", "max" })
public class UnivariateAffineFunction	implements
										UnivariateAffineComposableFunction,
										FunctionDomain,
										Serializable
{

	private static final long serialVersionUID = 1L;

	private volatile double mA, mB;
	private volatile double mMin = Double.NEGATIVE_INFINITY,
			mMax = Double.POSITIVE_INFINITY;

	public static UnivariateAffineFunction identity()
	{
		return new UnivariateAffineFunction(1, 0);
	}

	public static UnivariateAffineFunction axplusb(double pA, double pB)
	{
		return new UnivariateAffineFunction(pA, pB);
	}

	public UnivariateAffineFunction()	throws NullArgumentException,
										NoDataException
	{
		this(1, 0);
	}

	public UnivariateAffineFunction(UnivariateAffineComposableFunction pUnivariateAffineComposableFunction)
	{
		mA = pUnivariateAffineComposableFunction.getSlope();
		mB = pUnivariateAffineComposableFunction.getConstant();
		mMin = pUnivariateAffineComposableFunction.getMin();
		mMax = pUnivariateAffineComposableFunction.getMax();
	}

	public UnivariateAffineFunction(double pA, double pB)	throws NullArgumentException,
															NoDataException
	{
		mA = pA;
		mB = pB;
	}

	public void setConstant(double pB)
	{
		mB = pB;
	}

	public void setSlope(double pA)
	{
		mA = pA;
	}

	@Override
	public double getConstant()
	{
		return mB;
	}

	@Override
	public double getSlope()
	{
		return mA;
	}

	@Override
	public double getMin()
	{
		return mMin;
	}

	@Override
	public void setMin(double pMin)
	{
		mMin = pMin;
	}

	@Override
	public double getMax()
	{
		return mMax;
	}

	@Override
	public void setMax(double pMax)
	{
		mMax = pMax;
	}

	@Override
	public void composeWith(UnivariateAffineFunction pFunction)
	{
		mA = mA * pFunction.getSlope();
		mB = mA * pFunction.getConstant() + mB;

		UnivariateAffineFunction lInverse = pFunction.inverse();
		if (lInverse == null)
		{
			mMin = Double.NEGATIVE_INFINITY;
			mMax = Double.POSITIVE_INFINITY;
		}
		else
		{
			mMin = min(lInverse.value(mMin), lInverse.value(mMax));
			mMax = max(lInverse.value(mMin), lInverse.value(mMax));
		}
	}

	private UnivariateAffineFunction inverse()
	{
		if (mA == 0)
			return null;
		double lInverseA = 1 / mA;
		double lInverseB = -mB / mA;
		return new UnivariateAffineFunction(lInverseA, lInverseB);
	}

	@Override
	public double value(double pX)
	{
		return mA * pX + mB;
	}

	@Override
	public String toString()
	{
		return "UnivariateAffineFunction [Y = " + mA
				+ " * X + "
				+ mB
				+ "]";
	}

	@Override
	public void setIdentity()
	{
		mA = 1;
		mB = 0;
	}

}
