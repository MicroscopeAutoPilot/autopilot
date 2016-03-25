package rtlib.core.cpu;

import net.openhft.affinity.AffinityStrategies;
import net.openhft.affinity.AffinityThreadFactory;

public class Affinity
{
	public static final AffinityThreadFactory cSameCoreAfinityThreadFactory = new AffinityThreadFactory("SameCore",
																										AffinityStrategies.SAME_CORE);
	public static final AffinityThreadFactory cDifferentCoreAfinityThreadFactory = new AffinityThreadFactory(	"DifferentCore",
																												AffinityStrategies.DIFFERENT_CORE);

	public static final AffinityThreadFactory cSameSocketAfinityThreadFactory = new AffinityThreadFactory(	"SameSocket",
																											AffinityStrategies.SAME_SOCKET);
	public static final AffinityThreadFactory cDifferentSocketAfinityThreadFactory = new AffinityThreadFactory(	"DifferentSocket",
																												AffinityStrategies.DIFFERENT_SOCKET);

	public static final Thread createPinnedOnThreadSameCore(String pName,
															Runnable pRunnable)
	{
		Thread lNewThread = cSameCoreAfinityThreadFactory.newThread(pRunnable);
		lNewThread.setName(pName);
		return lNewThread;
	}
}
