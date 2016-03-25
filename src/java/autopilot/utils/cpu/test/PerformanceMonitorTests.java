package autopilot.utils.cpu.test;

import java.lang.management.ManagementFactory;

import org.junit.Test;

public class PerformanceMonitorTests
{

	/*@Test
	public void demo() throws InterruptedException
	{
		final int lAvailableProcessors = Runtime.getRuntime()
																						.availableProcessors();
		System.out.println("lAvailableProcessors=" + lAvailableProcessors);

		for (int i = 0; i < 100; i++)
		{
			final double lSystemLoadAverage = ManagementFactory.getOperatingSystemMXBean()
																													.getSystemLoadAverage() / lAvailableProcessors;

			System.out.println("lSystemLoadAverage=" + lSystemLoadAverage);

			Thread.sleep(250);
		}
	}

	@Test
	public void burn() throws InterruptedException
	{
		for (int i = 0; i < 100000000; i++)
		{
			System.out.println("'");
		}
	}/**/
}
