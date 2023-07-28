package replete.numbers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import replete.collections.RHashMap;

public class RandomUtil {
    public static <T> T flip(T... args) {
        Random R = new Random();
        int i = R.nextInt(args.length);
        return args[i];
    }

    public static boolean flip() {
        return flip(0, 1) == 0;
    }

    public static int getRandomWithinRange(int lowIncl, int highNonIncl) {
        return lowIncl + ((int)(new Random().nextDouble() * (highNonIncl - lowIncl)));
    }
    public static long getRandomWithinRange(long lowIncl, long highNonIncl) {
        return lowIncl + ((long)(new Random().nextDouble() * (highNonIncl - lowIncl)));
    }

    public static int getRandomWithinRange(Random rand, int lowIncl, int highNonIncl) {
        return lowIncl + ((int)(rand.nextDouble() * (highNonIncl - lowIncl)));
    }
    public static long getRandomWithinRange(Random rand, long lowIncl, long highNonIncl) {
        return lowIncl + ((long)(rand.nextDouble() * (highNonIncl - lowIncl)));
    }

    public static <T> T chooseFromDistribution(Map<T, Integer> dist) {
        Random R = new Random();
        int value = R.nextInt(100);
        int totalSpan = 0;
        for(T key : dist.keySet()) {
            Integer span = dist.get(key);
            totalSpan += span;
            if(value < totalSpan) {
                return key;
            }
        }
        return null;
    }


    //////////
    // TEST //
    //////////

    private static Map<String, Integer> DEST_GROUPS = new RHashMap<>(
        "group1", 35, "group2", 35, "group3", 20, "group4", 10
    );
    public static void main(String[] args) {
        System.out.println(DEST_GROUPS);
        Map<String, Integer> counts = new HashMap<>();
        int max = 120000;
        for(int i = 0; i < max; i++) {
            String group = chooseFromDistribution(DEST_GROUPS);
            Integer count = counts.get(group);
            if(count == null) {
                count = 0;
            }
            counts.put(group, count + 1);
        }
        for(String k : counts.keySet()) {
            Integer count = counts.get(k);
            System.out.println(k + " " + count + " " + NumUtil.pct(count, max));
        }
//        for(int i = 0; i < 1000; i++) {
//            System.out.println(getRandomWithinRange(0, 10));
//        }
    }
}
