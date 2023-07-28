package replete.threads.deadlock;

import java.util.Map;

public interface DeadlockHandler {
  void handleDeadlock(DeadlockDetector detector, Map<Long, DeadlockedThreadDescriptor> deadlockedThreadDescriptors);
}