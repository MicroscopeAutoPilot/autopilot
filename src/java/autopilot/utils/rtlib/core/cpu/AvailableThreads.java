package autopilot.utils.rtlib.core.cpu;

import java.util.Map;

/**
 * Created by royer on 25/03/16.
 */
public class AvailableThreads {
    static int sNumberOfThreadsAvailable = 1;

    static {
        Map<String, String> env = System.getenv();
        if (env.containsKey("TRAVIS") || env.containsKey("travis")) {
            System.out.println("!!!!!TRAVIS BUILD!!!!!");
            sNumberOfThreadsAvailable = 1;
        }
        else
            sNumberOfThreadsAvailable = Runtime.getRuntime().availableProcessors();
    }

    public static int getNumberOfThreads() {
        return sNumberOfThreadsAvailable;
    }
}
