package replete.scrutinize.archive.app1;

import java.lang.management.ManagementFactory;

import com.sun.management.UnixOperatingSystemMXBean;

// doesn't really work...
public class PerformanceMonitor {
    private static int  availableProcessors = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
    private static long lastSystemTime      = 0;
    private static long lastProcessCpuTime  = 0;

    public static synchronized double getCpuUsage()
    {
        if(lastSystemTime == 0) {
            baselineCounters();
            return -1.0;
        }

        long systemTime     = System.nanoTime();
        long processCpuTime = 0;

        if(ManagementFactory.getOperatingSystemMXBean() instanceof UnixOperatingSystemMXBean) {
            processCpuTime = ((UnixOperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuTime();
        }

        double cpuUsage = (double) ( processCpuTime - lastProcessCpuTime ) / ( systemTime - lastSystemTime );
System.out.println(processCpuTime + " " + lastProcessCpuTime + " " + systemTime + " " + lastSystemTime + " " + cpuUsage);
        lastSystemTime     = systemTime;
        lastProcessCpuTime = processCpuTime;

        return cpuUsage / availableProcessors;
    }

    private static void baselineCounters() {
        lastSystemTime = System.nanoTime();
        if(ManagementFactory.getOperatingSystemMXBean() instanceof UnixOperatingSystemMXBean) {
            lastProcessCpuTime = ((UnixOperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuTime();
        }
    }
}