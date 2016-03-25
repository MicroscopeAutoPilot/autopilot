package rtlib.core.device.queue;

public interface QueueProvider<O extends StateQueueDeviceInterface>
{
	void buildQueue(O pO);
}
