package autopilot.stackanalysis.visualization.test;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import org.junit.Test;

import autopilot.stackanalysis.visualization.ScatterPlot3D;
import autopilot.stackanalysis.visualization.ScatterPlot3DData;

public class ScatterPlot3DTests
{

	@Test
	public void testBasic() throws Exception
	{
		final ScatterPlot3DData lScatterPlot3DData = new ScatterPlot3DData();

		for (double p = 0; p < 2 * PI; p += 0.01)
		{
			final double x = 3 * cos(p);
			final double y = 5 * sin(p);
			final double z = x * y;
			lScatterPlot3DData.addPoint(x, y, z, 0.01, 1, z, 1);
		}

		final ScatterPlot3D lScatterPlot3D = new ScatterPlot3D(	"test",
																														512,
																														512);
		lScatterPlot3D.ensureOpened();

		lScatterPlot3D.set(lScatterPlot3DData);

		Thread.sleep(25000);
	}

	@Test
	public void testChangeData() throws Exception
	{
		final ScatterPlot3DData lScatterPlot3DData = new ScatterPlot3DData();
		final ScatterPlot3D lScatterPlot3D = new ScatterPlot3D(	"test",
																														512,
																														512);

		lScatterPlot3D.ensureOpened();

		for (double u = 0; u < 1; u += 0.1)
		{

			lScatterPlot3DData.clear();
			for (double p = 0; p < 2 * PI; p += 0.01)
			{
				final double x = 3 * cos(p * u);
				final double y = 5 * sin(p);
				final double z = x * y * u;
				lScatterPlot3DData.addPoint(x, y, z, 0.01, 1, z, 1);
			}

			lScatterPlot3D.set(lScatterPlot3DData);

			lScatterPlot3D.ensureOpened();

			Thread.sleep(500);
		}

		Thread.sleep(25000);
	}

}
