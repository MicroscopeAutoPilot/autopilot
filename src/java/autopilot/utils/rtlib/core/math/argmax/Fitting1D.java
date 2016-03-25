package autopilot.utils.rtlib.core.math.argmax;

public interface Fitting1D
{
	public double[] fit(double[] pX, double[] pY);

	public double getRMSD();
}
