package rtlib.core.variable.persistence;

import java.io.File;
import java.util.Collection;
import java.util.Formatter;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rtlib.core.variable.VariableInterface;
import rtlib.core.variable.VariableListener;
import rtlib.core.variable.bundle.VariableBundle;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;

public class VariableBundleAsFile extends VariableBundle
{
	private final ExecutorService cSingleThreadExecutor = Executors.newSingleThreadExecutor();

	private final ConcurrentSkipListMap<String, VariableInterface<?>> mPrefixWithNameToVariableMap = new ConcurrentSkipListMap<String, VariableInterface<?>>();

	private final VariableListener mVariableListener;

	private final File mFile;

	private final Object mLock = new Object();

	public VariableBundleAsFile(final String pBundleName,
								final File pFile)
	{
		this(pBundleName, pFile, false);
	}

	@SuppressWarnings("rawtypes")
	public VariableBundleAsFile(final String pBundleName,
								final File pFile,
								final boolean pAutoReadOnGet)
	{
		super(pBundleName);
		mFile = pFile;

		mVariableListener = new VariableListener()
		{

			@Override
			public void getEvent(final Object pCurrentValue)
			{
				if (pAutoReadOnGet)
				{
					read();
				}
			}

			@Override
			public void setEvent(	final Object pCurrentValue,
									final Object pNewValue)
			{
				writeAsynchronously();
			}
		};

	}

	@Override
	public <O> void addVariable(final VariableInterface<O> pVariable)
	{
		this.addVariable("", pVariable);
	}

	public <O> void addVariable(final String pPrefix,
								final VariableInterface<O> pVariable)
	{
		super.addVariable(pVariable);
		final String lKey = pPrefix + (pPrefix.isEmpty() ? "" : ".")
							+ pVariable.getName();
		mPrefixWithNameToVariableMap.put(lKey.trim(), pVariable);
		registerListener(pVariable);
	}

	@Override
	public <O> void removeVariable(final VariableInterface<O> pVariable)
	{
		unregisterListener(pVariable);
		super.removeVariable(pVariable);
	}

	@Override
	public void removeAllVariables()
	{
		unregisterListenerForAllVariables();
		super.removeAllVariables();
	}

	@Override
	public VariableInterface<?> getVariable(final String pPrefixAndName)
	{
		return mPrefixWithNameToVariableMap.get(pPrefixAndName);
	}

	private void registerListener(final VariableInterface<?> pVariable)
	{
		if (pVariable instanceof DoubleVariable)
		{
			final DoubleVariable lDoubleVariable = (DoubleVariable) pVariable;
			lDoubleVariable.addListener(mVariableListener);
		}
		else if (pVariable instanceof ObjectVariable<?>)
		{
			final ObjectVariable<?> lObjectVariable = (ObjectVariable<?>) pVariable;
			lObjectVariable.addListener(mVariableListener);
		}
	}

	private void unregisterListener(final VariableInterface<?> pVariable)
	{
		pVariable.removeListener(mVariableListener);
	}

	private void unregisterListenerForAllVariables()
	{
		final Collection<VariableInterface<?>> lAllVariables = getAllVariables();
		for (final VariableInterface<?> lVariable : lAllVariables)
		{
			lVariable.removeListener(mVariableListener);
		}
	}

	public boolean read()
	{

		try
		{
			synchronized (mLock)
			{
				Scanner lScanner = null;
				if (mFile.exists())
				{
					try
					{
						lScanner = new Scanner(mFile);

						while (lScanner.hasNextLine())
						{
							final String lLine = lScanner.nextLine();
							final String[] lEqualsSplitStringArray = lLine.split("\t?=\t?");

							final String lKey = lEqualsSplitStringArray[0].trim();
							final String lValue = lEqualsSplitStringArray[1].trim();

							final VariableInterface<?> lVariable = mPrefixWithNameToVariableMap.get(lKey);

							if (lVariable instanceof DoubleVariable)
							{
								readDoubleVariable(lValue, lVariable);
							}
							else if (lVariable instanceof ObjectVariable<?>)
							{
								readObjectVariable(lValue, lVariable);
							}
						}
					}
					catch (final Exception e)
					{
						e.printStackTrace();
						return false;
					}
					finally
					{
						lScanner.close();
					}
				}

				return true;
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			return false;
		}

	}

	private void readDoubleVariable(final String lValue,
									final VariableInterface<?> lVariable)
	{
		final DoubleVariable lDoubleVariable = (DoubleVariable) lVariable;

		final String[] lSplitValueFloatExactStringArray = lValue.split("\t");
		final String lApproximateFloatValueString = lSplitValueFloatExactStringArray[0];
		final double lApproximateDoubleValue = Double.parseDouble(lApproximateFloatValueString);

		double lDoubleValue = lApproximateDoubleValue;
		if (lSplitValueFloatExactStringArray.length == 2)
		{
			final String lExactLongValueString = lSplitValueFloatExactStringArray[1];
			final long lExactLongValue = Long.parseLong(lExactLongValueString);
			final double lExactDoubleValue = Double.longBitsToDouble(lExactLongValue);
			lDoubleValue = lExactDoubleValue;
		}

		lDoubleVariable.setValue(lDoubleValue);
	}

	private void readObjectVariable(final String lValue,
									final VariableInterface<?> lVariable)
	{
		final ObjectVariable<?> lObjectVariable = (ObjectVariable<?>) lVariable;

		final ObjectVariable<String> lStringVariable = (ObjectVariable<String>) lObjectVariable;
		lStringVariable.setReference(lValue);
	}

	public boolean write()
	{
		synchronized (mLock)
		{
			Formatter lFormatter = null;
			try
			{
				lFormatter = new Formatter(mFile);
				for (final Map.Entry<String, VariableInterface<?>> lVariableEntry : mPrefixWithNameToVariableMap.entrySet())
				{
					final String lVariablePrefixAndName = lVariableEntry.getKey();
					final VariableInterface<?> lVariable = lVariableEntry.getValue();

					// System.out.println(lVariable);

					if (lVariable instanceof DoubleVariable)
					{
						final DoubleVariable lDoubleVariable = (DoubleVariable) lVariable;

						lFormatter.format(	"%s\t=\t%g\n",
											lVariablePrefixAndName,
											lDoubleVariable.getValue());

					}
					else if (lVariable instanceof ObjectVariable<?>)
					{
						final ObjectVariable<?> lObjectVariable = (ObjectVariable<?>) lVariable;

						lFormatter.format(	"%s\t=\t%s\n",
											lVariablePrefixAndName,
											lObjectVariable.get());
					}
				}

				lFormatter.flush();
				if (lFormatter != null)
				{
					// System.out.println("close formatter");
					lFormatter.close();
				}
				return true;
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
				return false;
			}

		}

	}

	private void writeAsynchronously()
	{
		// cSingleThreadExecutor.execute(mFileWriterRunnable);
		write();
	}

	private final Runnable mFileWriterRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			write();
		}
	};

	public void close()
	{
		cSingleThreadExecutor.shutdown();
		try
		{
			cSingleThreadExecutor.awaitTermination(	100,
													TimeUnit.SECONDS);
		}
		catch (final InterruptedException e)
		{
		}
	}

}
