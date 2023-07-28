package replete.jgraph.test;

import replete.pipeline.AbstractAtomicStage;
import replete.pipeline.events.ParameterChangeEvent;
import replete.progress.FractionProgressMessage;
import replete.progress.IndeterminateProgressMessage;
import replete.progress.PercentProgressMessage;
import replete.threads.ThreadUtil;

public class SleepStage extends AbstractAtomicStage {

    ////////////
    // FIELDS //
    ////////////

    private int duration;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SleepStage(String name) {
        super(name);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        int prev = this.duration;
        this.duration = duration;
        setDirty(true);
        fireParameterChangeNotifier(new ParameterChangeEvent(this, "duration", prev, duration));
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void init() {}   // No input/output descriptors

    @Override
    protected void executeInner() {
        for(int i = 0; i < duration; i++) {
            ThreadUtil.sleep(1);

            if(duration == 2000) {
                publishProgress(
                    new FractionProgressMessage("Sleeping", "Quick", i, duration));

            } else if(duration == 2001) {
                publishProgress(
                    new PercentProgressMessage("Sleeping", "Quick", (int) (i * 100.0 / duration)));

            } else {
                publishProgress(
                    new IndeterminateProgressMessage("Sleeping", "Quick"));
            }
        }
    }

    @Override
    public String toString() {
        return "SleepStage [id=" + id + ", name=" + name + "]";
    }
}
