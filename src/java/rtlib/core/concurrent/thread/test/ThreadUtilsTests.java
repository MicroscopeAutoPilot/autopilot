package rtlib.core.concurrent.thread.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.core.units.Magnitude;

public class ThreadUtilsTests
{

	@Test
	public void testSleep() throws InterruptedException
	{
		Runnable lParasiteRunnable = () -> {
			long[] lDummyData = new long[10000];
			for (int i = 0; i < 10000; i++)
			{
				lDummyData[i] += Math.random() * 10;
				ThreadUtils.sleep(	lDummyData[i],
									TimeUnit.MILLISECONDS);
			}
		};

		for (int i = 0; i < 100; i++)
		{
			Thread lParasiteThread = new Thread(lParasiteRunnable);
			lParasiteThread.setDaemon(true);
			lParasiteThread.start();

			long lSleepTimeNanos = (long) (100000000 * Math.random());

			long lStart = System.nanoTime();
			ThreadUtils.sleep(lSleepTimeNanos, TimeUnit.NANOSECONDS);
			// Thread.sleep((long) Magnitude.nano2milli(lSleepTimeNanos));
			long lStop = System.nanoTime();
			long lElapsedTimeNanos = lStop - lStart;

			double lRelativeError = Magnitude.nano2milli((1.0 * lSleepTimeNanos - lElapsedTimeNanos) / lSleepTimeNanos);
			// System.out.println("rel error=" + lRelativeError);

			assertTrue(Math.abs(lRelativeError) < 1E-3);

		}
	}

}
