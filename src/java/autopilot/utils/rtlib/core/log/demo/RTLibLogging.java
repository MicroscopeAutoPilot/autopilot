package autopilot.utils.rtlib.core.log.demo;

import autopilot.utils.rtlib.core.log.Loggable;
import autopilot.utils.rtlib.core.log.gui.LogWindowHandler;
import org.junit.Test;

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
