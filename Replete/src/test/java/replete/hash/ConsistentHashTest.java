package replete.hash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

public class ConsistentHashTest {

    @Test
    public void testAdd() {
        Md5HashFunction hashFunction = new Md5HashFunction();
        ConsistentHash<String, Integer> hash =
            new ConsistentHash<String, Integer>(hashFunction);
        TreeMap<Long, Integer> expected =
            new TreeMap<Long, Integer>();
        for(int i = 0; i < 100; i++)
        {
            String key = "Key String " + i;
            hash.add(key, i);
            expected.put(hashFunction.hash(key), i);
        }

        for(int i = 0; i < 100; i++)
        {
            String key = "Key String " + i;
            assertEquals(i, (int)hash.find(key));
            key = "Another String " + i;
            long h = hashFunction.hash(key);
            if(!expected.containsKey(h))
            {
                SortedMap<Long,Integer> tailMap = expected.tailMap(h);
                if(tailMap.isEmpty())
                {
                    h = expected.firstKey();
                }
                else
                {
                    h = tailMap.firstKey();
                }
            }
            assertEquals(expected.get(h), hash.find(key));
        }
    }

    @Test
    public void testRemove()
    {
        Md5HashFunction hashFunction = new Md5HashFunction();
        ConsistentHash<String, Integer> hash =
            new ConsistentHash<String, Integer>(hashFunction);
        for(int i = 0; i < 100; i++)
        {
            String key = "Key String " + i;
            hash.add(key, i);
        }
        for(int i = 0; i < 10; i++)
        {
            String key = "Key String " + i;
            hash.remove(key);
        }
        for(int i = 0; i < 10; i++)
        {
            String key = "Key String " + i;
            assertFalse(i == (int)hash.find(key));
        }
        for(int i = 10; i < 100; i++)
        {
            String key = "Key String " + i;
            assertEquals(i, (int)hash.find(key));
        }
    }

    @Test
    public void testHasValue()
    {
        Md5HashFunction hashFunction = new Md5HashFunction();
        ConsistentHash<String, Integer> hash =
            new ConsistentHash<String, Integer>(hashFunction);
        for(int i = 0; i < 100; i++)
        {
            String key = "Key String " + i;
            hash.add(key, i);
        }
        for(int i = 0; i < 100; i++)
        {
            assertTrue(hash.hasValue(i));
        }
        for(int i = 100; i < 200; i++)
        {
            assertFalse(hash.hasValue(i));
        }
    }

    @Test
    public void testGetValues()
    {
        Md5HashFunction hashFunction = new Md5HashFunction();
        ConsistentHash<String, Integer> hash =
            new ConsistentHash<String, Integer>(hashFunction);
        HashSet<Integer> expected = new HashSet<Integer>();
        for(int i = 0; i < 100; i++)
        {
            String key = "Key String " + i;
            hash.add(key, i);
            expected.add(i);
        }
        Set<Integer> result = hash.getValues();
        assertEquals(100, result.size());
        for(int value : result)
        {
            assertTrue(expected.contains(value));
        }
    }

}
