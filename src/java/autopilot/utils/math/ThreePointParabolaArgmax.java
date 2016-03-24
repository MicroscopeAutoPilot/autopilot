package autopilot.utils.math;

/**
 * 
 * @author royerloic
 */
public class ThreePointParabolaArgmax
{
	public double xe, ye;
	public boolean reliable;
	public boolean signPositive;

	// -----------------------------------------------------------------------------------------
	// Calculates the co-ordinates xe, ye of the extremum and returns
	// the 2nd derivative d2 if the extremum is deemed reliable or 0.0 otherwise
	// Negative return value indicates a valid maximum, positive return value a
	// valid minimum.
	// Zero return value indicates an unreliable extremum (this does not mean that
	// d2 is zero).
	// When d2 is exact zero, the function returns exact 0.0 in both xe and ye.

	public final double findExtremum(	final double xl,
																		final double xc,
																		final double xu,
																		final double yl,
																		final double yc,
																		final double yu)

	{
		double d1, d2;
		d2 = 2 * ((yu - yc) / (xu - xc) - (yl - yc) / (xl - xc))
					/ (xu - xl);
		d1 = (yu - yc) / (xu - xc) - 0.5 * d2 * (xu - xc);
		if (d2 != 0)
		{
			xe = xc - d1 / d2;
			ye = yc + 0.5 * d1 * (xe - xc);

			if (xu < xe || xe < xl)
			{
				reliable = false;
				return 0.0;
			}
			else if (Double.isNaN(xe) || Double.isNaN(ye))
			{
				reliable = false;
				return 0.0;
			}
			else
			{
				reliable = true;
				signPositive = d2 > 0;
				return d2;
			}
		}
		else
		{ // Degenerate d2
			xe = xc; // This could be NAN
			ye = yc; // This could be NAN
			reliable = false;
			return 0.0;
		}

	}

	@Override
	public String toString()
	{
		if (reliable)
		{
			return String.format("Extremum found at (%g,%g)", xe, ye);
		}
		else
		{
			return String.format(	"No reliable %s found.",
														signPositive ? "maximum" : "minimum");
		}
	}

}
