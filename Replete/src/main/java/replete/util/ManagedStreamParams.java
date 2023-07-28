package replete.util;

import java.util.ArrayList;
import java.util.List;

public class ManagedStreamParams {
    boolean outputOn;
    boolean timeStampOn;
    boolean traceOn;
    List<String> patterns;

    public ManagedStreamParams()
    {
        outputOn = true;
        timeStampOn = false;
        traceOn = false;
        patterns = new ArrayList<String>();
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public ManagedStreamParams setPatterns(List<String> newPatterns) {
        patterns = newPatterns;
        return this;
    }

    public boolean isOutputOn() {
        return outputOn;
    }

    public ManagedStreamParams setOutputOn(boolean outputOn) {
        this.outputOn = outputOn;
        return this;
    }

    public boolean isTimeStampOn() {
        return timeStampOn;
    }

    public ManagedStreamParams setTimeStampOn(boolean timeStampOn) {
        this.timeStampOn = timeStampOn;
        return this;
    }

    public boolean isTraceOn() {
        return traceOn;
    }

    public ManagedStreamParams setTraceOn(boolean traceOn) {
        this.traceOn = traceOn;
        return this;
    }

}
