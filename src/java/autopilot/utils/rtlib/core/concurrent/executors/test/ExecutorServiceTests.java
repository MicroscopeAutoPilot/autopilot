package autopilot.utils.rtlib.core.concurrent.executors.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import autopilot.utils.rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import autopilot.utils.rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import autopilot.utils.rtlib.core.concurrent.executors.WaitingScheduledFuture;
import autopilot.utils.rtlib.core.concurrent.thread.ThreadUtils;

public class ExecutorServiceTests
{
	private static final int cNumberOfTasks = 1000;
	AtomicInteger mCounter = new AtomicInteger(0);

	private class ExecutorServiceTest	implements
										AsynchronousExecutorServiceAccess,
										AsynchronousSchedulerServiceAccess
	{

		public void doSomething() throws InterruptedException
		{
			for (int i = 0; i < cNumberOfTasks; i++)
			{
				final int j = i;
				final Runnable lTask = () -> {
					// System.out.println("task-" + j);
					try
					{
						ThreadUtils.sleep(4, TimeUnit.MILLISECONDS);
					}
					catch (final Exception e)
					{
						e.printStackTrace();
					}
					mCounter.incrementAndGet();
				};
				// System.out.println("submitting : " + j);
				final Future<?> lFuture = executeAsynchronously(lTask);

				// System.out.println(" done.");

			}
		}

		public WaitingScheduledFuture<?> scheduleSomething() throws InterruptedException
		{

			final Runnable lTask = () -> {
				// System.out.println("scheduled task-");
				try
				{
					// System.out.println("scheduled task start");
					mCounter.incrementAndGet();
					ThreadUtils.sleep(10, TimeUnit.MILLISECONDS);
					// System.out.println("scheduled task end");
				}
				catch (final Exception e)
				{
					e.printStackTrace();
				}

			};
			// System.out.println("submitting");

			return scheduleNTimesAtFixedRate(	lTask,
												20L,
												10L,
												TimeUnit.MILLISECONDS);

		}

	}

	@Test
	public void testAsynhronousExecution()	throws InterruptedException,
											ExecutionException,
											TimeoutException
	{

		final ExecutorServiceTest lExecutorServiceTest = new ExecutorServiceTest();

		mCounter.set(0);
		lExecutorServiceTest.doSomething();
		// System.out.print("WAITING");
		assertTrue(lExecutorServiceTest.waitForCompletion(	10,
															TimeUnit.SECONDS));
		assertEquals(cNumberOfTasks, mCounter.get());
		// System.out.println("...done");

		mCounter.set(0);
		lExecutorServiceTest.doSomething();
		// System.out.print("WAITING");
		assertTrue(lExecutorServiceTest.waitForCompletion(	10,
															TimeUnit.SECONDS));
		assertEquals(cNumberOfTasks, mCounter.get());
		// System.out.println("...done");

		mCounter.set(0);
		lExecutorServiceTest.doSomething();
		// System.out.print("WAITING");
		assertFalse(lExecutorServiceTest.waitForCompletion(	10,
															TimeUnit.MILLISECONDS));
		if (cNumberOfTasks <= mCounter.get())
			System.out.println("mCounter.get()=" + mCounter.get());
		assertTrue(cNumberOfTasks > mCounter.get());
		// System.out.println("...done");

	}

	@Test
	public void testPeriodicScheduling() throws InterruptedException,
										ExecutionException,
										TimeoutException
	{

		final ExecutorServiceTest lExecutorServiceTest = new ExecutorServiceTest();

		mCounter.set(0);
		WaitingScheduledFuture<?> lWaitingScheduledFuture = lExecutorServiceTest.scheduleSomething();
		System.out.print("WAITING");

		assertTrue(lWaitingScheduledFuture.waitForCompletion(	100,
																TimeUnit.SECONDS));

		System.out.println("mCounter.get()=" + mCounter.get());
		assertEquals(20, mCounter.get());
		System.out.println("...done");

		mCounter.set(0);
		lWaitingScheduledFuture = lExecutorServiceTest.scheduleSomething();
		System.out.print("WAITING");
		assertTrue(lWaitingScheduledFuture.waitForCompletion(	100,
																TimeUnit.SECONDS));
		assertEquals(20, mCounter.get());
		System.out.println("...done");

		mCounter.set(0);
		lWaitingScheduledFuture = lExecutorServiceTest.scheduleSomething();
		System.out.print("WAITING");
		assertFalse(lWaitingScheduledFuture.waitForCompletion(	100,
																TimeUnit.MILLISECONDS));
		System.out.println(mCounter.get());
		assertTrue(mCounter.get() > 1);
		assertTrue(20 > mCounter.get());
		// System.out.println("...done");

	}

}
