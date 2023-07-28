package luceo.actions.trivial;

import luceo.actions.Action;
import luceo.actions.ExecuteMessageLevel;
import replete.numbers.RandomUtil;
import replete.threads.ThreadUtil;

public class TrivialAction extends Action {

    @Override
    protected void executeInner() {
        int secs = RandomUtil.getRandomWithinRange(1, 4);
        System.out.println("Trivial (wait " + secs + " secs)");
        int millis = secs * 1000;
        ThreadUtil.sleep(millis);

        if(RandomUtil.flip()) {
            addMessage(ExecuteMessageLevel.TRACE, "some trace");
        }
        if(RandomUtil.flip()) {
            addMessage(ExecuteMessageLevel.ERROR, "some error",
                new RuntimeException("some random error " + RandomUtil.getRandomWithinRange(0, 100)));
        }
        if(RandomUtil.getRandomWithinRange(0, 5) == 0) {
            throw new ArithmeticException("test arithmetic error");
        }
    }
}
