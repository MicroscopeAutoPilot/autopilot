package rtlib.core.log.demo;

import org.junit.Test;

import rtlib.core.log.Loggable;
import rtlib.core.log.gui.LogWindowHandler;

public class RTLibLogging implements Loggable
{

	@Test
	public void demo() throws InterruptedException
	{
		for (int i = 0; i < 100; i++)
			info("demo", "bla");

		final LogWindowHandler lLogWindowHandler = LogWindowHandler.getInstance("demo",
																				768,
																				320);

		getLogger("demo").addHandler(lLogWindowHandler);

		for (int i = 0; i < 100; i++)
			info("demo", "blu");

		Thread.sleep(4000);

	}

}
