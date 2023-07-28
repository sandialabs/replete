package replete.collections.mm;

/**
 * @author Derek Trumbo
 */

public class Member<F> {
    public final F value;
    public final Integer count;
    public Member(F val, Integer cnt) {
        value = val;
        count = cnt;
    }
}
