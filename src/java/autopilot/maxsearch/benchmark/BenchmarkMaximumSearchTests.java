package autopilot.maxsearch.benchmark;

import java.io.IOException;

import org.junit.Test;

import autopilot.maxsearch.BrentMethodSearch;
import autopilot.maxsearch.GoldenRatioMaximumSearch;
import autopilot.maxsearch.MaximumSearchInterface;

public class BenchmarkMaximumSearchTests
{

	@Test
	public void benchmarkMaximumSearchs() throws IOException
	{
		try
		{
			new GoldenRatioMaximumSearch();
			final MaximumSearchInterface lBrentMethodSearch = new BrentMethodSearch();
			// RobustMaximumSearch lRobustGoldenRatioMaximumSearch = new
			// RobustMaximumSearch(lGoldenRatioSearch,1,6);
			// RobustMaximumSearch lRobustBrentMethodMaximumSearch = new
			// RobustMaximumSearch(lBrentMethodSearch,1,6);

			final BenchmarkMaximumSearch lBenchmarkMaximumSearchLowNoise = new BenchmarkMaximumSearch(0.001,
																																																1000);
			// System.out.print(lBenchmarkMaximumSearchLowNoise.benchmark(lTetraSearch));
			// System.out.print(lBenchmarkMaximumSearchLowNoise.benchmark(lGoldenRatioSearch));
			System.out.print(lBenchmarkMaximumSearchLowNoise.benchmark(lBrentMethodSearch));
			// System.out.print(lBenchmarkMaximumSearchLowNoise.benchmark(lRobustGoldenRatioMaximumSearch));
			// System.out.print(lBenchmarkMaximumSearchLowNoise.benchmark(lRobustBrentMethodMaximumSearch));

			final BenchmarkMaximumSearch lBenchmarkMaximumSearchHighNoise = new BenchmarkMaximumSearch(	0.1,
																																																	1000);
			// System.out.print(lBenchmarkMaximumSearchHighNoise.benchmark(lTetraSearch));
			// System.out.print(lBenchmarkMaximumSearchHighNoise.benchmark(lGoldenRatioSearch));
			System.out.print(lBenchmarkMaximumSearchHighNoise.benchmark(lBrentMethodSearch));
			// System.out.print(lBenchmarkMaximumSearchHighNoise.benchmark(lRobustGoldenRatioMaximumSearch));
			// System.out.print(lBenchmarkMaximumSearchHighNoise.benchmark(lRobustBrentMethodMaximumSearch));
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}

	}

}
