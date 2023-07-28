package learning.ui.draw;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DrawApp {

    public static void main(String[] args) {
        DrawFrame win = new DrawFrame();
        win.setVisible(true);                 // NEVER setVisible(true) inside Frame/Dialog constructor

//        DrawFrame win2 = new DrawFrame();   // Test JOptionPane modality
//        win2.setVisible(true);

        Shape s = new Rectangle();
        System.out.println(s.getName());

        ActionListener listener = new ActionListener() {   // Abstract inner class
            public void actionPerformed(ActionEvent e) {

            }
        };
        Shape s2 = new Shape() {
            @Override
            public String getName() {
                return "Anonymous Shape";
            }
        };

        System.out.println(s2.getClass());

        System.out.println(s2.getName());

        int a = 0;
        int _ = 0;
        int _3 = 0;
        int $ = 0;

        Person p = new Person() {
            @Override
            public void fart() {
                super.fart();
                System.out.println("sqqqqqeeesst");
            }
        };
        p.fart();
    }


    static class Person {
        public void fart() {
            System.out.println("bbssbbfsfbbtbfabsdfsdf");
        }
    }

    abstract static class Shape {
        abstract String getName();
    }

    static class Rectangle extends Shape {
        @Override
        String getName() {
            return "Rect";
        }
    }
}
