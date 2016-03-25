package rtlib.core.math.regression.ewr;

import java.util.ArrayList;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.ejml.data.DenseMatrix64F;

public class EnsembleLinearRegressors implements MultivariateFunction
{
	private int mDimension;
	private ArrayList<DenseMatrix64F> mA = new ArrayList<>();
	private ArrayList<DenseMatrix64F> mB = new ArrayList<>();

	public EnsembleLinearRegressors(int pDimension)
	{
		mDimension = pDimension;
	}

	@Override
	public double value(double[] pPoint)
	{
		// TODO: finish
		// DenseMatrix64F lX = new DenseMatrix64F(pPoint);
		//
		// for(int)
		return 0;
	}

}
