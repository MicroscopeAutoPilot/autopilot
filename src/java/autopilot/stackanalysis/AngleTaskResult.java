package autopilot.stackanalysis;

public class AngleTaskResult
{
	public double alpha, beta;
	public double pvalue;

	@Override
	public String toString()
	{
		return String.format(	"AngleTaskResult [alpha=%s, beta=%s, beta=%g]",
													alpha,
													beta,
													pvalue);
	}

}
