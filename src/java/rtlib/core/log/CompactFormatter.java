package rtlib.core.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class CompactFormatter extends Formatter
{

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	@Override
	public String format(LogRecord record)
	{
		final StringBuilder lStringBuilder = new StringBuilder();

		// final Date lDate = new Date(record.getMillis());

		lStringBuilder.append(record.getLevel()
									.getLocalizedName()
									.substring(0, 1))
						.append(": ")
						.append(formatMessage(record).trim())
						.append(LINE_SEPARATOR);

		if (record.getThrown() != null)
		{
			try
			{
				final StringWriter sw = new StringWriter();
				final PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				lStringBuilder.append(sw.toString());
			}
			catch (final Throwable ex)
			{
				ex.printStackTrace();
			}
		}

		return lStringBuilder.toString();
	}
}
