package autopilot.utils.rtlib.core.math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;

public interface UnivariateAffineComposableFunction	extends
													UnivariateFunction, FunctionDomain
{
	
	void setConstant(double pConstant);
	double getConstant();

	void setSlope(double pSlope);
	double getSlope();
	
	public void composeWith(UnivariateAffineFunction pFunction);

	public void setIdentity();


}
