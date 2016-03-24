package autopilot.utils.cpu;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class PerformanceMonitor
{
	private final int availableProcessors = ManagementFactory.getOperatingSystemMXBean()
																														.getAvailableProcessors();
	private long lastSystemTime = 0;
	private long lastProcessCpuTime = 0;

	public synchronized double getCpuUsage()
	{
		if (lastSystemTime == 0)
		{
			baselineCounters();
			return 0;
		}

		final long systemTime = System.nanoTime();
		long processCpuTime = 0;

		if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean)
		{
			processCpuTime = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuTime();
		}

		final double cpuUsage = (double) (processCpuTime - lastProcessCpuTime) / (systemTime - lastSystemTime);

		lastSystemTime = systemTime;
		lastProcessCpuTime = processCpuTime;

		return cpuUsage / availableProcessors;
	}

	private void baselineCounters()
	{
		lastSystemTime = System.nanoTime();

		if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean)
		{
			lastProcessCpuTime = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuTime();
		}
	}
}
