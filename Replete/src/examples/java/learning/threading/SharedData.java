package learning.threading;

import java.util.concurrent.atomic.AtomicInteger;

import replete.threads.ThreadUtil;

public class SharedData {

    private static int x = 0;
    private static AtomicInteger y = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {

        Runnable r1 = () -> {
            ThreadUtil.sleep(1500);   // Sleeps have nothing to do with weird behavior
            x = x + 10;
            y.addAndGet(10);
//            x += 10;    // MUST BE ATOMIC RIGHT???

            // i = i + 1; vs i++;



//            y = x + 10;
//            x <= y;
        };

        Runnable r2 = () -> {
            ThreadUtil.sleep(1500);   // Sleeps have nothing to do with weird behavior
            x = x + 30;
            y.addAndGet(30);
        };

        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);

        t1.start();
        t2.start();

        //Thread.sleep(1000?????);

        t1.join();
        t2.join();

        System.out.println(x);
        System.out.println(y);   // We luckily saw all three: 10, 30, 40, Race Condition
    }

}
