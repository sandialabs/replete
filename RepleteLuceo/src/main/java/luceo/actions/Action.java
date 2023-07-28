package luceo.actions;

import java.util.ArrayList;
import java.util.List;

import replete.text.RStringBuilder;
import replete.text.StringUtil;

// Two problems so far:
//  - we might need an "execution ID" to tie parent & child execution summaries together
//  - we might need a way to keep track of action call order

public abstract class Action {


    ////////////
    // FIELDS //
    ////////////

    private List<ExecuteSummary> executeSummaries = new ArrayList<>();
    private ExecuteSummary currentSummary;
    // can calculate attempt count, success count and failure count from
    // and duration averages by iterating over summaries. if performance
    // is a problem we can create aggregate variables at this level


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public List<ExecuteSummary> getExecuteSummaries() {
        return executeSummaries;
    }
    public ExecuteSummary getCurrentSummary() {
        return currentSummary;
    }

    // Mutators

    protected void addMessage(ExecuteMessageLevel level, String message) {
        addMessage(level, message, null);
    }
    protected void addMessage(ExecuteMessageLevel level, String message, Exception error) {
        if(currentSummary == null) {
            throw new IllegalStateException("Trying to add message but no current execution");
        }
        currentSummary.messages.add(new ExecuteMessage(level, message, error));
    }


    /////////////
    // EXECUTE //
    /////////////

    // This method signature is a thing of beauty... fundamental to whole vision,
    // though it's not impossible there might be some kind of params... maybe?
    public void execute(/*ExecuteContext context*/) {
        // WHat would go in the context object here?
        //  - Execution ID
        //  - TTC?
        //  - access to a controller that can invoke other actions upon request

        // execute these together
        synchronized(this) {
            currentSummary = new ExecuteSummary();
            executeSummaries.add(currentSummary);
            currentSummary.startOuter = System.currentTimeMillis();
        }

        try {
//            checkRequiredInputs(); // order with started?
//            validateInputs();

            //firestart?
            //fireFirstProgressMessage?

            currentSummary.startInner = System.currentTimeMillis();
            try {
                //check pause and stop - where ttc provided?
                executeInner();
            } finally {
                currentSummary.endInner = System.currentTimeMillis();
            }

            // dirty = false
            // - dirty might be the wrong word here
            // - the curent output available corresponds to the current inputs
            // executed with these outputs = true

            // aggregateSuccessCount++ if performance reasons
        } catch(Exception e) {
            synchronized(this) {
                currentSummary.error = e;
                currentSummary.messages.add(new ExecuteMessage(ExecuteMessageLevel.FATAL, null, e));
                // aggregateFailCount++ if performance reasons
            }
            //fireerror
            throw e; //?  is this needed with complete event doing its thing?
        } finally {
            currentSummary.endOuter = System.currentTimeMillis();
            //firecomplete
            currentSummary = null;  // When done executing, there is no "current" execute summary
        }
    }

    // notifiers stop,pause,stopreq,pausereq,prog
    // ttc could somehow be on execute() param


    //////////////
    // ABSTRACT //
    //////////////

    protected abstract void executeInner();


    ///////////////
    // RENDERING //
    ///////////////

    public void printSummary() {
        RStringBuilder buffer = new RStringBuilder();
        renderSummary(buffer, 0);
        System.out.println(buffer.toString());
    }
    public void renderSummary(RStringBuilder buffer, int level) {
        String sp = StringUtil.spaces(level * 4);
        buffer.appendln(sp + getClass().getName() + " (" + executeSummaries.size() + " executions)");
        long totalDurOuterAll = 0;
        int totalCntOuterAll = 0;
        long totalDurOuterSuc = 0;
        int totalCntOuterSuc = 0;
        long totalDurInnerAll = 0;
        int totalCntInnerAll = 0;
        long totalDurInnerSuc = 0;
        int totalCntInnerSuc = 0;

        int e = 1;
        for(ExecuteSummary summary : executeSummaries) {
            String extraOuter;
            if(summary.getStartOuter() != -1 && summary.getEndOuter() != -1) {
                extraOuter = " (" + (summary.getEndOuter() - summary.getStartOuter()) + " millis)";
            } else {
                extraOuter = "";
            }
            String extraInner;
            if(summary.getStartInner() != -1 && summary.getEndInner() != -1) {
                extraInner = " (" + (summary.getEndInner() - summary.getStartInner()) + " millis)";
            } else {
                extraInner = "";
            }

            buffer.appendln(sp + "    Execution #" + e);
            buffer.appendln(sp + "        Start (Outer): " + (summary.getStartOuter() == -1 ? "(Unset)" : summary.getStartOuter()));
            buffer.appendln(sp + "        End   (Outer): " + (summary.getEndOuter() == -1 ? "(Unset)" : summary.getEndOuter()) + extraOuter);
            buffer.appendln(sp + "        Start (Inner): " + (summary.getStartInner() == -1 ? "(Unset)" : summary.getStartInner()));
            buffer.appendln(sp + "        End   (Inner): " + (summary.getEndInner() == -1 ? "(Unset)" : summary.getEndInner()) + extraInner);
            buffer.appendln(sp + "        Success? " + StringUtil.yesNo(summary.isSuccess()));
            if(summary.getError() != null) {
                buffer.appendln(sp + "        Error: " + summary.getError());
            }
            if(!summary.getMessages().isEmpty()) {
                buffer.appendln(sp + "        Messages:");
                for(ExecuteMessage msg : summary.getMessages()) {
                    buffer.appendln(sp + "            " + msg);
                }
            }

            if(summary.getStartOuter() != -1 && summary.getEndOuter() != -1) {
                long dur = summary.getEndOuter() - summary.getStartOuter();
                totalDurOuterAll += dur;
                totalCntOuterAll++;
                if(summary.isSuccess()) {
                    totalDurOuterSuc += dur;
                    totalCntOuterSuc++;
                }
            }

            if(summary.getStartInner() != -1 && summary.getEndInner() != -1) {
                long dur = summary.getEndInner() - summary.getStartInner();
                totalDurInnerAll += dur;
                totalCntInnerAll++;
                if(summary.isSuccess()) {
                    totalDurInnerSuc += dur;
                    totalCntInnerSuc++;
                }
            }

            e++;
        }
        buffer.appendln(sp + "    Average Duration:");
        buffer.appendln(sp + "        Outer All: " + (totalCntOuterAll == 0 ? "N/A" : (double) totalDurOuterAll / totalCntOuterAll));
        buffer.appendln(sp + "        Outer Suc: " + (totalCntOuterSuc == 0 ? "N/A" : (double) totalDurOuterSuc / totalCntOuterSuc));
        buffer.appendln(sp + "        Inner All: " + (totalCntInnerAll == 0 ? "N/A" : (double) totalDurInnerAll / totalCntInnerAll));
        buffer.appendln(sp + "        Inner Suc: " + (totalCntInnerSuc == 0 ? "N/A" : (double) totalDurInnerSuc / totalCntInnerSuc));
    }
}

//package com.company.animation;
//class MoveSteps extends Action {
//
//    MoveSteps() {
//        addInput(new Input(new DataType(Integer.class), "steps", "number of steps"));
//    }
//
//    @Override
//    public void executeInner() {
//        int pos = context.get("pos");
//        pos += params.get("steps");
//        context.put("pos", pos);
//    }
//}
