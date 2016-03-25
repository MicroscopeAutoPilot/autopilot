package rtlib.core.variable.persistence;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rtlib.core.variable.types.doublev.DoubleVariable;

public class DoubleVariableAsFile extends DoubleVariable implements
														Closeable

{
	private final ExecutorService mSingleThreadExecutor = Executors.newSingleThreadExecutor();

	private Double mCachedValue;

	private final File mFile;
	// private FileEventNotifier mFileEventNotifier;

	private final Object mLock = new Object();

	public DoubleVariableAsFile(final File pFile,
								final String pVariableName,
								final double pDoubleValue)
	{
		super(pVariableName, pDoubleValue);
		mFile = pFile;

	}

	@Override
	public double getValue()
	{
		if (mCachedValue != null)
		{
			return mCachedValue;
		}

		try
		{
			synchronized (mLock)
			{
				if (!mFile.exists())
				{
					mCachedValue = super.getValue();
					return mCachedValue;
				}
				final Scanner lScanner = new Scanner(mFile);
				final String lLine = lScanner.nextLine().trim();
				mCachedValue = Double.parseDouble(lLine);
				lScanner.close();
			}
			return mCachedValue;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return super.getValue();
		}
	}

	@Override
	public void setValue(final double pNewValue)
	{
		super.setValue(pNewValue);
		mCachedValue = pNewValue;
		mSingleThreadExecutor.execute(mFileSaverRunnable);
	}

	private final Runnable mFileSaverRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			final double lValue = mCachedValue;

			try
			{
				synchronized (mLock)
				{

					/*if (mFileEventNotifier != null)
						mFileEventNotifier.stopMonitoring();/**/
					final Formatter lFormatter = new Formatter(mFile);
					try
					{
						lFormatter.format("%g\n", lValue);
						lFormatter.flush();
					}
					finally
					{
						lFormatter.close();
					}
					/*if (mFileEventNotifier != null)
						mFileEventNotifier.startMonitoring();/**/
				}
				// ensureFileEventNotifierAllocated();
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
			}

		}

	};

	/*
	private void ensureFileEventNotifierAllocated() throws Exception
	{
		if (mFileEventNotifier == null)
		{
			mFileEventNotifier = new FileEventNotifier(mFile);
			mFileEventNotifier.startMonitoring();
			mFileEventNotifier.addFileEventListener(new FileEventNotifierListener()
			{

				@Override
				public void fileEvent(final FileEventNotifier pThis,
															final File pFile,
															final FileEventKind pEventKind)
				{
					getValue();
				}
			});

		}
	}/**/

	@Override
	public void close() throws IOException
	{
		/*
		try
		{
			if (mFileEventNotifier != null)
				mFileEventNotifier.stopMonitoring();
		}
		catch (final Exception e)
		{
			throw new IOException(e);
		}
		/**/
	}

}
