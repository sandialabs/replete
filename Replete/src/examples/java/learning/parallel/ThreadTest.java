package learning.parallel;

import replete.threads.ThreadUtil;

public class ThreadTest {
    private int c;

    public void doSomething() {
        synchronized(this) {
            int x = 0;
            int y = x + 1;
            int a = x + y;
            c = x + y + a;
        }
        c++;
        int z = c + 10;
        synchronized(this) {
//            z +=
        }
    }

    public static void main(String[] args) {
        ThreadUtil.dumpThreads();

        Thread t0 = new CustomThread();
        Thread t1 = new Thread(new MyCustomRunnable());
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("2: " + getClass().getName());
            }
        });
        Thread t3 = new Thread(() -> {
            System.out.println("3: " + ThreadTest.class.getName());
        });

        t0.start();
        t1.start();
        t2.start();
        t3.start();

        ThreadUtil.dumpThreads();

        try {
            t0.join();
            t1.join();
            t2.join();
            t3.join();
        } catch(InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("All the threads are done!");
    }

    private static class CustomThread extends Thread {
        @Override
        public void run() {
            System.out.println("0: " + getClass().getName());
        }
    }

    private static class MyCustomRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("1: " + getClass().getName());
        }
    }
}
