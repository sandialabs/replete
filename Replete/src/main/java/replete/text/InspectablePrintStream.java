package replete.text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import replete.util.ClassUtil;
import replete.util.DebugUtil;
import replete.util.ReflectionUtil;

// Identification events happen at the level of the "call" which
// means that a lot of text, including any number of lines might
// be printed before the lookFor output is generated.

public class InspectablePrintStream extends PrintStream {


    ////////////
    // FIELDS //
    ////////////

    private List<String> lookFor = new ArrayList<>();  // Could have callbacks one day too
                                                       // instead of only default behavior

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public InspectablePrintStream(File file, String csn) throws FileNotFoundException,
                                                         UnsupportedEncodingException {
        super(file, csn);
    }
    public InspectablePrintStream(File file) throws FileNotFoundException {
        super(file);
    }
    public InspectablePrintStream(OutputStream out, boolean autoFlush,
                                  String encoding) throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }
    public InspectablePrintStream(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }
    public InspectablePrintStream(OutputStream out) {
        super(out);
    }
    public InspectablePrintStream(String fileName, String csn) throws FileNotFoundException,
                                                               UnsupportedEncodingException {
        super(fileName, csn);
    }
    public InspectablePrintStream(String fileName) throws FileNotFoundException {
        super(fileName);
    }


    //////////////
    // MUTATORS //
    //////////////

    public InspectablePrintStream addLookFor(String s) {
        lookFor.add(s);
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void write(int b) {
        super.write(b);
        // TODO: check this method? It's only one character...
    }
    @Override
    public void write(byte buf[], int off, int len) {
        super.write(buf, off, len);
        check(s -> new String(buf, off, len).contains(s));
    }
    private void write2(char buf[]) {
        try {
            Method m = ReflectionUtil.getMethod(PrintStream.class, "write", char[].class);
            m.setAccessible(true);
            m.invoke(this, buf);
        } catch(Exception e1) {
            e1.printStackTrace();
        }

        check(e -> new String(buf).contains(e));
    }
    private void write2(String s) {
        try {
            Method m = ReflectionUtil.getMethod(PrintStream.class, "write", String.class);
            m.setAccessible(true);
            m.invoke(this, s);
        } catch(Exception e1) {
            e1.printStackTrace();
        }

        check(e -> s.contains(e));
    }
//    private void newLine() {
//        try {
//            Method m = PrintStream.class.getMethod("newLine");
//            m.setAccessible(true);
//            m.invoke(this);
//        } catch(Exception e1) {
//            e1.printStackTrace();
//        }
//    }
    @Override
    public void print(boolean b) {
        write2(b ? "true" : "false");
    }
    @Override
    public void print(char c) {
        write2(String.valueOf(c));
    }
    @Override
    public void print(int i) {
        write2(String.valueOf(i));
    }
    @Override
    public void print(long l) {
        write2(String.valueOf(l));
    }
    @Override
    public void print(float f) {
        write2(String.valueOf(f));
    }
    @Override
    public void print(double d) {
        write2(String.valueOf(d));
    }
    @Override
    public void print(char s[]) {
        write2(s);
    }
    @Override
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        write2(s);
    }
    @Override
    public void print(Object obj) {
        write2(String.valueOf(obj));
    }
//    @Override
//    public void println() {
//        newLine();
//    }
//    @Override
//    public void println(boolean x) {
//        synchronized (this) {
//            print(x);
//            newLine();
//        }
//    }
//    @Override
//    public void println(char x) {
//        synchronized (this) {
//            print(x);
//            newLine();
//        }
//    }
//    @Override
//    public void println(int x) {
//        synchronized (this) {
//            print(x);
//            newLine();
//        }
//    }
//    @Override
//    public void println(long x) {
//        synchronized (this) {
//            print(x);
//            newLine();
//        }
//    }
//    @Override
//    public void println(float x) {
//        synchronized (this) {
//            print(x);
//            newLine();
//        }
//    }
//    @Override
//    public void println(double x) {
//        synchronized (this) {
//            print(x);
//            newLine();
//        }
//    }
//    @Override
//    public void println(char x[]) {
//        synchronized (this) {
//            print(x);
//            newLine();
//        }
//    }
//    @Override
//    public void println(String x) {
//        synchronized (this) {
//            print(x);
//            newLine();
//        }
//    }
//    @Override
//    public void println(Object x) {
//        String s = String.valueOf(x);
//        synchronized (this) {
//            print(s);
//            newLine();
//        }
//    }
//    @Override
//    public PrintStream printf(String format, Object ... args) {
//        return format(format, args);
//    }
//    @Override
//    public PrintStream printf(Locale l, String format, Object ... args) {
//        return format(l, format, args);
//    }
    @Override
    public PrintStream format(String format, Object ... args) {
        super.format(format, args);
        check(s -> Arrays.asList(args).stream().map(a -> String.valueOf(a)).anyMatch(a -> a.contains(s)));
        return this;
    }
    @Override
    public PrintStream format(Locale l, String format, Object ... args) {
        super.format(l, format, args);
        check(s -> Arrays.asList(args).stream().map(a -> String.valueOf(a)).anyMatch(a -> a.contains(s)));
        return this;
    }
//    @Override
//    public PrintStream append(CharSequence csq) {
//        if (csq == null) {
//            print("null");
//        } else {
//            print(csq.toString());
//        }
//        return this;
//    }
    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        CharSequence cs = (csq == null ? "null" : csq);
        write2(cs.subSequence(start, end).toString());
        return this;
    }
//    @Override
//    public PrintStream append(char c) {
//        print(c);
//        return this;
//    }

    private boolean inCheck = false;
    private void check(Predicate<String> condition) {
        if(!inCheck && !ClassUtil.isOnCallStack(OutputStreamWriter.class.getName(), "flushBuffer")) {
            inCheck = true;
            Optional<String> found = lookFor.stream().filter(s -> condition.test(s)).findFirst();
            if(found.isPresent()) {
                doFound(found.get());
            }
            inCheck = false;
        }
    }
    private void doFound(String string) {
        String trace = DebugUtil.getNearTrace(1000);
        String msg =
            "<--FOUND '" + string +
            "' on thread '" + Thread.currentThread().getName() +
            "':\n" + trace.trim() + "-->";
        super.print(msg);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        System.setOut(new InspectablePrintStream(System.out).addLookFor("Mexico"));

        System.out.println("Colorado");
        System.out.println("New Mexico");
        System.out.println("Florida");
    }
}
