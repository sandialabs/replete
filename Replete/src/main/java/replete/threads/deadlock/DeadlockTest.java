package replete.threads.deadlock;

import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;

public class DeadlockTest {

    public static void causeDeadlockSync() {
        Object lock1 = new Object();
        Object lock2 = new Object();

        synchronized(lock1) {
            System.out.println(Thread.currentThread().getName() + " (Current) acquired lock1");

            Thread thread2 = new Thread(() -> {
                synchronized(lock2) {
                    System.out.println(Thread.currentThread().getName() + " acquired lock2");
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);  // Sleep not required here
                    } catch(InterruptedException ignore) {
                    }
                    synchronized(lock1) {
                        System.out.println(Thread.currentThread().getName() + " acquired lock1");  // Won't happen
                    }
                }
                System.out.println(Thread.currentThread().getName() + " DONE");  // Won't happen
            }, "DeadlockTestSyncThread2");

            thread2.start();

            try {
                TimeUnit.MILLISECONDS.sleep(500);  // Should be long enough for thread2 to get lock2
            } catch(InterruptedException ignore) {
            }

            synchronized(lock2) {
                System.out.println(Thread.currentThread().getName() + " (Current) acquired lock2");  // Won't happen
            }
        }
    }

    public static void causeDeadlockAsync() {
        Object lock1 = new Object();
        Object lock2 = new Object();

        Thread thread1 = new Thread(() -> {
            synchronized(lock1) {
                System.out.println(Thread.currentThread().getName() + " acquired lock1");
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch(InterruptedException ignore) {
                }
                synchronized(lock2) {
                    System.out.println(Thread.currentThread().getName() + " acquired lock2");
                }
            }
            System.out.println(Thread.currentThread().getName() + " DONE");
        }, "DeadlockTestAsyncThread1");

        Thread thread2 = new Thread(() -> {
            synchronized(lock2) {
                System.out.println(Thread.currentThread().getName() + " acquired lock2");
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch(InterruptedException ignore) {
                }
                synchronized(lock1) {
                    System.out.println(Thread.currentThread().getName() + " acquired lock1");
                }
            }
            System.out.println(Thread.currentThread().getName() + " DONE");
        }, "DeadlockTestAsyncThread2");

        Thread thread3 = new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch(Exception e) {
                e.printStackTrace();
            }
            synchronized(lock2) {
                System.out.println(Thread.currentThread().getName() + " acquired lock2");
                synchronized(lock1) {
                    System.out.println(Thread.currentThread().getName() + " acquired lock1");
                }
            }
            System.out.println(Thread.currentThread().getName() + " DONE");
        }, "DeadlockTestAsyncThread3");

        thread1.start();
        thread2.start();
        thread3.start();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        DeadlockDetector deadlockDetector = DeadlockDetector.get();
        deadlockDetector.setPeriod(5, TimeUnit.SECONDS);
        deadlockDetector.addHandler(new StandardConsoleDeadlockHandler(true, false));
//        deadlockDetector.addHandler((d, infos) -> {
//            System.out.println("Find Duration: " + d.getFindThreadsDurationStats());  // Always seems to be 0
//        });
        deadlockDetector.start();

        Lay.BLtg(Lay.fr("Deadlock Test", CommonConcepts.THREAD_DEADLOCKED),
            "C", Lay.p(),
            "S", Lay.FL("R",
                Lay.btn("Cause &Deadlock (Sync)",
                    (ActionListener) e -> {
                        Thread t = new Thread("Current") {
                            @Override
                            public void run() {
                                causeDeadlockSync();
                            }
                        };
                        t.start();
                    }),
                Lay.btn("Cause &Deadlock (Async)",
                    (ActionListener) e -> causeDeadlockAsync()),
                Lay.btn("&Close", "closer")
            ),
            "size=[500,200],center,visible=true"
        );
    }
}
