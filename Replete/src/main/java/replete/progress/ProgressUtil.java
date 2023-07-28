package replete.progress;

import replete.numbers.NumUtil;
import replete.threads.ThreadUtil;

public class ProgressUtil {

    public static void main(String[] args) {

        for(int i = 0; i < 10; i++) {
            String p = NumUtil.pct(i, 10);
            System.out.print("\r");
            System.out.printf("%6s", p);
            ThreadUtil.sleep(2000);
        }

    }

}
