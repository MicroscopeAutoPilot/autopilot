package rtlib.core.device.queue;

import java.util.concurrent.Future;

public interface StateQueueDeviceInterface
{
	void clearQueue();

	void addCurrentStateToQueue();

	void finalizeQueue();

	int getQueueLength();

	Future<Boolean> playQueue();

}
