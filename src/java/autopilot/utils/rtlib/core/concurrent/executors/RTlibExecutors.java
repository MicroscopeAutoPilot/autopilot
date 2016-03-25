package autopilot.utils.rtlib.core.concurrent.executors;

import java.lang.ref.SoftReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class RTlibExecutors
{

	private static ConcurrentHashMap<Object, SoftReference<CompletingThreadPoolExecutor>> cThreadPoolExecutorMap = new ConcurrentHashMap<>(100);
	private static ConcurrentHashMap<Object, SoftReference<ScheduledThreadPoolExecutor>> cScheduledThreadPoolExecutorMap = new ConcurrentHashMap<>(100);

	public static final CompletingThreadPoolExecutor getThreadPoolExecutor(final Object pObject)
	{
		final SoftReference<CompletingThreadPoolExecutor> lSoftReference = cThreadPoolExecutorMap.get(pObject);
		if (lSoftReference == null)
			return null;
		return lSoftReference.get();
	}

	public static final ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor(final Object pObject)
	{
		final SoftReference<ScheduledThreadPoolExecutor> lSoftReference = cScheduledThreadPoolExecutorMap.get(pObject);
		if (lSoftReference == null)
			return null;
		return lSoftReference.get();
	}

	public static void resetThreadPoolExecutor(final Object pObject)
	{
		cThreadPoolExecutorMap.remove(pObject);
	}

	public static void resetScheduledThreadPoolExecutor(final Object pObject)
	{
		cScheduledThreadPoolExecutorMap.remove(pObject);
	}

	public static final CompletingThreadPoolExecutor getOrCreateThreadPoolExecutor(	final Object pObject,
																					final int pPriority,
																					final int pCorePoolSize,
																					final int pMaxPoolSize,
																					final int pMaxQueueLength)
	{
		SoftReference<CompletingThreadPoolExecutor> lSoftReferenceOnThreadPoolExecutor = cThreadPoolExecutorMap.get(pObject);

		CompletingThreadPoolExecutor lThreadPoolExecutor;

		if (lSoftReferenceOnThreadPoolExecutor == null || lSoftReferenceOnThreadPoolExecutor.get() == null)
		{
			final BlockingQueue<Runnable> lNewQueue = new LinkedBlockingQueue<>(pMaxQueueLength);

			lThreadPoolExecutor = new CompletingThreadPoolExecutor(	pCorePoolSize,
																	pMaxPoolSize,
																	1,
																	TimeUnit.MINUTES,
																	lNewQueue,
																	getThreadFactory(	pObject.getClass()
																								.getSimpleName(),
																						pPriority));

			lThreadPoolExecutor.allowCoreThreadTimeOut(false);
			lThreadPoolExecutor.prestartAllCoreThreads();

			lSoftReferenceOnThreadPoolExecutor = new SoftReference<>(lThreadPoolExecutor);
			cThreadPoolExecutorMap.put(	pObject,
										lSoftReferenceOnThreadPoolExecutor);
		}

		lThreadPoolExecutor = lSoftReferenceOnThreadPoolExecutor.get();

		return lThreadPoolExecutor;
	}

	public static final ScheduledThreadPoolExecutor getOrCreateScheduledThreadPoolExecutor(	final Object pObject,
																							final int pPriority,
																							final int pCorePoolSize,
																							final int pMaxQueueLength)
	{

		SoftReference<ScheduledThreadPoolExecutor> lSoftReferenceOnThreadPoolExecutor = cScheduledThreadPoolExecutorMap.get(pObject);

		ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor;

		if (lSoftReferenceOnThreadPoolExecutor == null || lSoftReferenceOnThreadPoolExecutor.get() == null)
		{
			lScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(	pCorePoolSize,
																			getThreadFactory(	pObject.getClass()
																										.getSimpleName(),
																								pPriority));

			lScheduledThreadPoolExecutor.allowCoreThreadTimeOut(false);
			lScheduledThreadPoolExecutor.prestartAllCoreThreads();
			lScheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
			lScheduledThreadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
			lScheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);

			lSoftReferenceOnThreadPoolExecutor = new SoftReference<>(lScheduledThreadPoolExecutor);
			cScheduledThreadPoolExecutorMap.put(pObject,
												lSoftReferenceOnThreadPoolExecutor);
		}

		lScheduledThreadPoolExecutor = lSoftReferenceOnThreadPoolExecutor.get();

		return lScheduledThreadPoolExecutor;
	}

	public static final ThreadFactory getThreadFactory(	final String pName,
														final int pPriority)
	{
		final ThreadFactory lThreadFactory = new ThreadFactory()
		{
			@Override
			public Thread newThread(Runnable pRunnable)
			{
				final Thread lThread = new Thread(pRunnable);
				lThread.setName(pName + "-" + pRunnable.hashCode());
				lThread.setPriority(pPriority);
				lThread.setDaemon(true);

				return lThread;
			}
		};
		return lThreadFactory;
	}

}
