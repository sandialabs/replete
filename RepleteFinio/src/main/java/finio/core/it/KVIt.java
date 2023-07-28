package finio.core.it;

import java.util.Iterator;

import finio.core.KeyValue;

public class KVIt extends It<KeyValue> {
    public KVIt(Iterator<KeyValue> it) {
        super(it);
    }
}
