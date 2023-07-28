package learning.threading;

public class ThreadForms {

    public static void main(String[] args) {

        Runnable runnable = new MyRunnable();
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Anon Runnable");
            }
        };
        Thread t = new Thread(runnable2);
        t.start();

        Thread t2 = new Thread() {
            @Override
            public void run() {
                System.out.println("Anon Thread!");
            }
        };

        Thread t3 = new MyThread();
        t3.start();
    }

    private static class MyRunnable implements Runnable {
        @Override
        public void run() {
            // CONTEXT SWITCH - Allowed t3 to go
            System.out.println("MyRunnable Thread:run()");
        }
    }

    private static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("MyThread run()");
            // CONTEXT SWITCH
        }
    }

}
