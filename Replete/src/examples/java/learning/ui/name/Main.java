package learning.ui.name;

import java.lang.Thread.UncaughtExceptionHandler;

public class Main {

    class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
        }
    }

    public static void main(String[] args) {
        // Read old app state (from ~/.appstate)
        // Set uncaught exception handlers

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
//                alwaysTrue(Thread.currentThread() == t);
                System.out.println(t + " " + e);
                // log or show dialog
//                Dialogs.showMessage(null, null);
            }
        });
//
//        if(true) {
//            throw new RuntimeException("asdfsd");
//        }

//        UIDebugUtil.enableColor();

        NameFrame frame = new NameFrame();
        frame.setVisible(true);

//        ThreadUtil.dumpThreads();

        // Java AWT Abstract Windowing Toolkit
        // Heavyweight components - native OS components

        // Java Swing
        // Lightweight components - logic to draw pixels within a context

        // EDT = Event Dispatch Thread (UI Thread)
    }
}

