package learning.parallel;

public class ThreadTest2 {
    private static int c;
    private static Object lock1 = new Object();

    // This method is not "thread-safe", due to interacting with each other through shared memory
    // Atomicity
    public static void add100() {
        for(int i = 0; i < 1000; i++) {
            c = c + 10;     // Not an atomic statement
            //c += 10;          // Are these atomic?   Not atomic!
            //c++;              // Are these atomic?   Not atomic!
            // ADD c + 10 (with the READ)
            // <----------> context switch (swapping out a thread or process for a new one on the CPU)
            // WRITE c
        }
    }
    public static void add100Sync() {
        for(int i = 0; i < 1000; i++) {
            synchronized(lock1) {   // This is the tightest and thus fastest synch block placement
                c = c + 10;     // Not an atomic statement
            }
        }

//        synchronized(lock1) {
//            c *= 200;
//        }
    }
    public static void add100Sync2() {   // Slightly slower version than add100Sync
        synchronized(lock1) {
            for(int i = 0; i < 1000; i++) {
                c = c + 10;     // Not an atomic statement
            }
        }
    }
    public static void add100Sync3() {   // Slightly slower version than add100Sync2
        for(int i = 0; i < 1000; i++) {
            c = c + 10;     // Not an atomic statement
        }
    }
    public static synchronized/*(ThreadTest2.class)*/ void add100Sync4() {   // Slightly slower version than add100Sync2
        for(int i = 0; i < 1000; i++) {
            c = c + 10;     // Not an atomic statement
        }
    }
    public static synchronized/*(ThreadTest2.class)*/ void add100Sync5() {   // Slightly slower version than add100Sync2
        for(int i = 0; i < 1000; i++) {
            c = c + 10;     // Not an atomic statement
        }
    }

    public synchronized/*(this)*/ int doSomething() {
        return 100;
    }

    public static int make100() {  // Thread-safe, no shared memory
        int c = 0;
        for(int i = 0; i < 10; i++) {
            c = c + 10;
        }
        return c;
    }

    public static void main(String[] args) {
        Runnable r = () -> add100Sync();

        Thread t0 = new Thread(r);
        Thread t1 = new Thread(r);

        t0.start();
        t1.start();

        try {
            t0.join();
            t1.join();
        } catch(InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("c = " + c);
    }
}
