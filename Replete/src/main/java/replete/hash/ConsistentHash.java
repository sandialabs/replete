package replete.hash;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import replete.numbers.IntegralSpaceSpanPercentages;

public class ConsistentHash<K,V> {


    ////////////
    // FIELDS //
    ////////////

    private HashFunction<K> hashFunction;
    private SortedMap<Long, V> circle = new TreeMap<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConsistentHash(HashFunction<K> hashFunction) {
        this.hashFunction = hashFunction;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Set<V> getValues() {
        return new HashSet<V>(circle.values());
    }

    // Accessors (Computed)

    public boolean hasValue(V value) {
        return this.circle.values().contains(value);
    }

    public V find(K key) {
        if(circle.isEmpty()) {
            return null;
        }
        long hash = hashFunction.hash(key);
        if(!circle.containsKey(hash)) {
            SortedMap<Long,V> tailMap = circle.tailMap(hash);
            if(tailMap.isEmpty()) {
                hash = circle.firstKey();
            } else {
                hash = tailMap.firstKey();
            }
        }
        return circle.get(hash);
    }

    public Map<V, Double> getValueSpanPercentages() {
        Long[] hashValues = circle.keySet().toArray(new Long[0]);
        IntegralSpaceSpanPercentages<Long> spanCalculator = new IntegralSpaceSpanPercentages<Long>(hashValues);
        Map<Long, Double> hashPctSpan = spanCalculator.getPercentages();
        Map<V, Double> valueSpanTotals = new HashMap<>();
        for(Long hash : hashPctSpan.keySet()) {
            V val = circle.get(hash);
            double prev;
            if(!valueSpanTotals.containsKey(val)) {
                prev = 0.0;
            } else {
                prev = valueSpanTotals.get(val);
            }
            valueSpanTotals.put(val, prev + hashPctSpan.get(hash));
        }
        return valueSpanTotals;
    }

    // Mutators

    public void add(K key, V value) {
        circle.put(hashFunction.hash(key), value);
    }
    public void remove(K key) {
        circle.remove(hashFunction.hash(key));
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        ConsistentHash<String, String> c = new ConsistentHash<String, String>(new Md5HashFunction());

        String[] keys = new String[] {"xaaa", "Santa Fe"};
        for(String key : keys) {
            for(int i = 0; i < 2/*00*/; i++) {
                c.add(key + ":" + i, key);
            }
        }

        System.out.println(c.getValueSpanPercentages());

        // Code that empiracally tests the percentages calculated.
        int MAX = 1000000;
        Map<String, Integer> counts = new HashMap<>();
        Random r = new Random();
        for(int i = 0; i < MAX; i++) {
            String s = c.find(r.nextInt() + "");
            if(!counts.containsKey(s)) {
                counts.put(s, 1);
            } else {
                counts.put(s, 1 + counts.get(s));
            }
        }
        for(String key : keys) {
            if(!counts.containsKey(key)) {
                System.out.println(key + " = 0 (0%)");
            } else {
                System.out.println(key + " = " + counts.get(key) + "(" + ((double)counts.get(key) / MAX * 100) + "%)");
            }
        }
    }
}
