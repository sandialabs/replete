package replete.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class IntegralSpaceSpanPercentages<T extends Number> {


    ////////////
    // FIELDS //
    ////////////

    private BigInteger max;
    private BigInteger min;
    private BigInteger space;
    private BigInteger one = new BigInteger("1");
    private BigInteger two = new BigInteger("2");

    private T[] origVals;
    private BigInteger[] bigVals;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Precondition: vs.length != 0 and no null values and no duplicates.
    public IntegralSpaceSpanPercentages(T[] vs) {
        if(vs[0] instanceof Byte) {
            min = new BigInteger("" + Byte.MIN_VALUE);
            max = new BigInteger("" + Byte.MAX_VALUE);
        } else if(vs[0] instanceof Short) {
            min = new BigInteger("" + Short.MIN_VALUE);
            max = new BigInteger("" + Short.MAX_VALUE);
        } else if(vs[0] instanceof Integer) {
            min = new BigInteger("" + Integer.MIN_VALUE);
            max = new BigInteger("" + Integer.MAX_VALUE);
        } else if(vs[0] instanceof Long) {
            min = new BigInteger("" + Long.MIN_VALUE);
            max = new BigInteger("" + Long.MAX_VALUE);
        } else {
            throw new RuntimeException("Data type not supported.");
        }
        space = max.add(one).multiply(two);

        origVals = vs;
        bigVals = new BigInteger[vs.length];
        for(int v = 0; v < vs.length; v++) {
            bigVals[v] = new BigInteger(vs[v].toString());
        }
    }


    //////////
    // PCTS //
    //////////

    public Map<T, Double> getPercentages() {

        Map<T, BigInteger> diffs = new HashMap<T, BigInteger>();

        for(int i = 0; i < origVals.length; i++) {
            BigInteger diff;
            if(i == 0) {
                if(origVals.length == 1) {
                    diff = space;
                } else {
                    diff = bigVals[i].subtract(min).add(max.subtract(bigVals[bigVals.length - 1])).add(one);
                }
            } else {
                diff = bigVals[i].subtract(bigVals[i - 1]);
            }
            diffs.put(origVals[i], diff);
        }

        Map<T, Double> pctSpan = new HashMap<T, Double>();
        for(int i = 0; i < origVals.length; i++) {
            BigInteger diff = diffs.get(origVals[i]);
            double pct = new BigDecimal(diff).divide(new BigDecimal(space)).doubleValue();
            pctSpan.put(origVals[i], pct);
        }

        return pctSpan;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
//        Long[] vals = new Long[]{Long.MIN_VALUE, Long.MIN_VALUE + 11000009158034000L, 0L, 123094810243L};
        Long[] vals = new Long[]{Long.MAX_VALUE};
//        Byte[] vals = new Byte[] {-1, 127};
//        IntegralSpaceSpanPercentages<Byte> issp = new IntegralSpaceSpanPercentages<Byte>(vals);
        IntegralSpaceSpanPercentages<Long> issp = new IntegralSpaceSpanPercentages<Long>(vals);
        Map<Long, Double> pcts = issp.getPercentages();
        System.out.println(pcts);
    }
}
