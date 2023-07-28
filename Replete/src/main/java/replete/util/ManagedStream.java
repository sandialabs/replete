package replete.util;

import java.io.IOException;
import java.io.InterruptedIOException;

//package replete.util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import replete.text.PatternPool;

// TODOS:
//   3. Add regex filter, filter on println

public class ManagedStream extends PrintStream
{
    ManagedStreamParams params;
    boolean newLineFlag = true;
    private boolean trouble = false;
    PatternPool pool = new PatternPool();
    List<Pattern> filterPatterns = new ArrayList<Pattern>();

    public ManagedStreamParams getParams() {
        return params;
    }

    public ManagedStream setParams(ManagedStreamParams params) {
        this.params = params;
        filterPatterns.clear();
        for(String regex: this.params.getPatterns()){
            Pattern p = pool.getPattern(regex);
            filterPatterns.add(p);
        }
        return this;
    }

    public ManagedStream(PrintStream outStream) {
        super(outStream);
        params = new ManagedStreamParams();
    }

    public ManagedStream(PrintStream outStream, ManagedStreamParams params) {
        super(outStream);
        this.params = params;
    }

    private void ensureOpen() throws IOException {
        if (out == null) {
            throw new IOException("Stream closed");
        }
    }

    @Override
    public void flush() {
        synchronized (this) {
            try {
                ensureOpen();
                out.flush();
            }
            catch (IOException x) {
                trouble = true;
            }
        }
    }

    @Override
    public void write(int b) {
        String s = String.valueOf(b);
        managedWrite(s);
    }

    @Override
    public void write(byte buf[], int off, int len) {
        String s = new String(buf, off, len);
        managedWrite(s);
    }

    private void managedWrite(String s) {
        if(params.isOutputOn()) {
            if(params.isTimeStampOn() && newLineFlag) {
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                s = timeStamp + " : " + s;
                newLineFlag= false;
            }
            if(params.isTimeStampOn()) {
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                timeStamp = "\n" + timeStamp + " : ";
                s = s.replaceAll("\\n", timeStamp);
            }
            if(params.isTraceOn()) {
                String trace = getTraceStr();
                s = "\n" + trace + "\n" + s;
            }
            // Do search and replace \n with \n timeStamp : , unless \n is the last character.
            // Set time_stamp_on if the last character is a newline.
            lowWrite(s);
        }
    }

    private void lowWrite(String s) {
        try {
            synchronized (this) {
                ensureOpen();
                for(int i = 0, n = s.length() ; i < n ; i++) {
                    char c = s.charAt(i);
                    out.write(c);
                };
                if (s.indexOf('\n') >= 0) {
                    out.flush();
                }
                if (s.indexOf('\n') == s.length()-1) {
                    newLineFlag = true;
                }
            }
        }
        catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        }
        catch (IOException x) {
            trouble = true;
        }
    }


    private StackTraceElement[] getTrace()
    {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        return trace;
    }

    private boolean calledBy(String className)
    {
        StackTraceElement[] trace = getTrace();
        boolean found = false;
        Pattern p = Pattern.compile(className);

        for(StackTraceElement e:trace)
        {
            if(p.matcher(e.getClassName()).find()) {
                found = true;
            }
        }
        return found;
    }

    private String getTraceStr()
    {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        String elementStr;
        String traceStr = "";

        for(StackTraceElement e:(Arrays.copyOfRange(trace,4,trace.length)))
        {
            String classname = e.getClassName();
            String methodname = e.getMethodName();
            //if(methodname != "getStackTrace"
            //        && methodname != "getTrace"
            //        && methodname != "managedWrite"
            //        && methodname != "print"
            //        && methodname != "write")
            //{
                elementStr = classname + "." + methodname;
                traceStr += elementStr + ":";
            //}
        }
        return traceStr;
    }

    public void disable()
    {
        params.setOutputOn(false);
    }

    public void enable()
    {
        params.setOutputOn(true);
    }

    public void timeStamp(boolean on)
    {
        params.setTimeStampOn(on);
    }

    public void trace(boolean on)
    {
        params.setTraceOn(on);
    }

    @Override
    public void print(boolean b) {
        managedWrite(String.valueOf(b));
    }

    @Override
    public void print(char c) {
        managedWrite(String.valueOf(c));
    }

    @Override
    public void print(int i) {
        managedWrite(String.valueOf(i));
    }

    @Override
    public void print(long l) {
        managedWrite(String.valueOf(l));
    }

    @Override
    public void print(float f) {
        managedWrite(String.valueOf(f));
    }

    @Override
    public void print(double d) {
        managedWrite(String.valueOf(d));
    }

    @Override
    public void print(char buf[]) {
        String s = new String(buf);
        managedWrite(s);
    }

    @Override
    public void print(String s) {
        managedWrite(String.valueOf(s));
    }

    @Override
    public void print(Object obj) {
        managedWrite(String.valueOf(obj));
    }

    public static void main(String[] args) {

    }
}
