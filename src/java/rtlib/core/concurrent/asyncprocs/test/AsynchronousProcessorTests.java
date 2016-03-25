package rtlib.core.concurrent.asyncprocs.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorPool;
import rtlib.core.concurrent.asyncprocs.ProcessorInterface;
import rtlib.core.concurrent.thread.ThreadUtils;

public class AsynchronousProcessorTests
{

	@Test
	public void testSimple2ProcessorPipeline()
	{
		final AsynchronousProcessorInterface<String, String> lProcessorA = new AsynchronousProcessorBase<String, String>(	"A",
																															10)
		{
			@Override
			public String process(final String pInput)
			{
				// System.out.println("Processor A received:" + pInput);
				return "A" + pInput;
			}
		};

		final AsynchronousProcessorInterface<String, String> lProcessorB = new AsynchronousProcessorBase<String, String>(	"B",
																															10)
		{
			@Override
			public String process(final String pInput)
			{
				// System.out.println("Processor B received:" + pInput);
				return "B" + pInput;
			}
		};

		lProcessorA.connectToReceiver(lProcessorB);
		assertTrue(lProcessorA.start());
		assertTrue(lProcessorB.start());

		boolean hasFailed = false;
		for (int i = 0; i < 100; i++)
		{
			hasFailed |= lProcessorA.passOrFail("demo" + i);
			// if(i>50) assertFalse();
		}
		assertTrue(hasFailed);
		ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);
		for (int i = 0; i < 100; i++)
		{
			assertTrue(lProcessorA.passOrFail("demo" + i));
			ThreadUtils.sleep(10, TimeUnit.MILLISECONDS);
		}

		assertTrue(lProcessorB.waitToFinish(1, TimeUnit.SECONDS));
		assertEquals(0, lProcessorB.getInputQueueLength());
		assertTrue(lProcessorB.stop(1, TimeUnit.SECONDS));

		assertTrue(lProcessorA.waitToFinish(1, TimeUnit.SECONDS));
		assertEquals(0, lProcessorA.getInputQueueLength());
		assertTrue(lProcessorA.stop(1, TimeUnit.SECONDS));

		assertTrue(lProcessorA.start());
		assertTrue(lProcessorB.start());

		for (int i = 0; i < 100; i++)
		{
			hasFailed |= lProcessorA.passOrFail("demo" + i);
			// if(i>50) assertFalse();
		}
		assertTrue(hasFailed);
		ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);
		for (int i = 0; i < 100; i++)
		{
			assertTrue(lProcessorA.passOrFail("demo" + i));
			ThreadUtils.sleep(10, TimeUnit.MILLISECONDS);
		}

		assertTrue(lProcessorB.waitToFinish(1, TimeUnit.SECONDS));
		assertEquals(0, lProcessorB.getInputQueueLength());
		assertTrue(lProcessorB.stop(1, TimeUnit.SECONDS));

		assertTrue(lProcessorA.waitToFinish(1, TimeUnit.SECONDS));
		assertEquals(0, lProcessorA.getInputQueueLength());
		assertTrue(lProcessorA.stop(1, TimeUnit.SECONDS));

	}

	@Test
	public void testLongQueue()
	{
		final AsynchronousProcessorInterface<String, String> lProcessorA = new AsynchronousProcessorBase<String, String>(	"A",
																															1000)
		{
			@Override
			public String process(final String pInput)
			{
				ThreadUtils.sleep(1, TimeUnit.MILLISECONDS);
				return "A" + pInput;
			}
		};

		final AsynchronousProcessorInterface<String, String> lProcessorB = new AsynchronousProcessorBase<String, String>(	"B",
																															1000)
		{
			@Override
			public String process(final String pInput)
			{
				ThreadUtils.sleep(1, TimeUnit.MILLISECONDS);
				return "B" + pInput;
			}
		};

		lProcessorA.connectToReceiver(lProcessorB);
		assertTrue(lProcessorA.start());
		assertTrue(lProcessorB.start());

		for (int i = 0; i < 1000; i++)
		{
			lProcessorA.passOrFail("demo" + i);
		}

		assertTrue(lProcessorA.getInputQueueLength() > 0);
		assertTrue(lProcessorA.waitToFinish(2, TimeUnit.SECONDS));
		assertEquals(0, lProcessorA.getInputQueueLength());
		assertTrue(lProcessorA.stop(1, TimeUnit.SECONDS));

		assertTrue(lProcessorB.waitToFinish(2, TimeUnit.SECONDS));
		assertEquals(0, lProcessorB.getInputQueueLength());
		assertTrue(lProcessorB.stop(1, TimeUnit.SECONDS));

	}

	@Test
	public void testSimple2ProcessorPipelineWithPooledProcessor() throws InterruptedException
	{
		final AsynchronousProcessorInterface<Integer, Integer> lProcessorA = new AsynchronousProcessorBase<Integer, Integer>(	"A",
																																10)
		{
			@Override
			public Integer process(final Integer pInput)
			{
				ThreadUtils.sleep(	(long) (Math.random() * 1000000),
									TimeUnit.NANOSECONDS);
				return pInput;
			}
		};

		final ProcessorInterface<Integer, Integer> lProcessor = new ProcessorInterface<Integer, Integer>()
		{

			@Override
			public Integer process(final Integer pInput)
			{
				// System.out.println("Processor B received:"+pInput);
				ThreadUtils.sleep(	(long) (Math.random() * 1000000),
									TimeUnit.NANOSECONDS);
				return pInput;
			}

			@Override
			public void close() throws IOException
			{

			}
		};

		final AsynchronousProcessorPool<Integer, Integer> lProcessorB = new AsynchronousProcessorPool<>("B",
																										10,
																										2,
																										lProcessor);

		final ConcurrentLinkedQueue<Integer> lIntList = new ConcurrentLinkedQueue<>();

		final AsynchronousProcessorInterface<Integer, Integer> lProcessorC = new AsynchronousProcessorBase<Integer, Integer>(	"C",
																																10)
		{
			@Override
			public Integer process(final Integer pInput)
			{
				ThreadUtils.sleep(	(long) (Math.random() * 1000000),
									TimeUnit.NANOSECONDS);
				if (pInput > 0)
					lIntList.add(pInput);
				return pInput;
			}
		};

		// lProcessorA.connectToReceiver(lProcessorC);
		lProcessorA.connectToReceiver(lProcessorB);
		lProcessorB.connectToReceiver(lProcessorC);
		assertTrue(lProcessorA.start());
		assertTrue(lProcessorB.start());
		assertTrue(lProcessorC.start());

		for (int i = 1; i <= 1000; i++)
		{
			lProcessorA.passOrWait(i);
			ThreadUtils.sleep(1, TimeUnit.MILLISECONDS);
		}

		assertTrue(lProcessorA.waitToFinish(2, TimeUnit.SECONDS));
		assertTrue(lProcessorB.waitToFinish(2, TimeUnit.SECONDS));
		assertTrue(lProcessorC.waitToFinish(2, TimeUnit.SECONDS));

		assertEquals(0, lProcessorB.getInputQueueLength());
		assertEquals(0, lProcessorB.getInputQueueLength());
		assertEquals(0, lProcessorC.getInputQueueLength());

		assertTrue(lProcessorA.stop(2, TimeUnit.SECONDS));
		assertTrue(lProcessorB.stop(2, TimeUnit.SECONDS));
		assertTrue(lProcessorC.stop(2, TimeUnit.SECONDS));

		ThreadUtils.sleep(2, TimeUnit.SECONDS);

		for (int i = 1; i <= 1000; i++)
		{
			final Integer lPoll = lIntList.poll();
			assertEquals(i, lPoll, 0);
		}

	}
}
