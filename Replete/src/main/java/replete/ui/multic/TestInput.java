package replete.ui.multic;

import replete.cli.ConsoleUtil;

public class TestInput {
    public static final int QUIT = 6;
    private static boolean keepGoingOutput = false;
    private static boolean keepGoingError = false;

    public static void main(String[] args) {
        int response;
        do {
            response = menu();
            switch(response) {
                case 1: startOutput(); break;
                case 2: stopOutput(); break;
                case 3: startError(); break;
                case 4: stopError(); break;
                case 5: noOp(); break;
            }
        } while(response != QUIT);
        System.out.println("[Program Ended]");
        keepGoingOutput = false;
        keepGoingError = false;
    }

    private static void startOutput() {
        System.out.println("<1: Start Output>");
        keepGoingOutput = true;
        new Thread() {
            @Override
            public void run() {
                while(keepGoingOutput) {
                    System.out.println("<Output>");
                    try {
                        Thread.sleep(3000);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    private static void stopOutput() {
        System.out.println("<2: Stop Output>");
        keepGoingOutput = false;
    }
    private static void startError() {
        System.out.println("<3: Start Error>");
        keepGoingError = true;
        new Thread() {
            @Override
            public void run() {
                while(keepGoingError) {
                    System.err.println("<Error>");
                    try {
                        Thread.sleep(3000);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    private static void stopError() {
        System.out.println("<4: Stop Error>");
        keepGoingError = false;
    }
    private static void noOp() {
        System.out.println("<5: No-Op>");
    }

    private static int menu() {
        int response = -1;

        while(response == -1) {
            System.out.println("Menu");
            System.out.println("====");
            System.out.println("1. Start Output");
            System.out.println("2. Stop Output");
            System.out.println("3. Start Error");
            System.out.println("4. Stop Error");
            System.out.println("5. No-Op");
            System.out.println("6. QUIT");
            System.out.print("==> ");

            String line = ConsoleUtil.getLine();
            try {
                response = Integer.parseInt(line);
                if(response < 1 || response > QUIT) {
                    System.out.println("[Invalid Input '" + response + "']");
                    response = -1;
                }
            } catch (Exception e) {
                System.out.println("[Invalid Input '" + line + "']");
                response = -1;
            }
        }

        return response;
    }
}
