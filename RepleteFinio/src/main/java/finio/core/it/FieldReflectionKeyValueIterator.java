package finio.core.it;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import finio.core.FUtil;
import finio.core.KeyValue;
import finio.core.errors.UnsupportedObjectTypeException;
import finio.core.warnings.FieldAccessWarning;
import replete.util.ReflectionUtil;

public class FieldReflectionKeyValueIterator extends KeyValueIterator {


    ///////////
    // FIELD //
    ///////////

    private Object O;
    private Field[] Fs;
    private int I = 0;
    private Field currentF;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FieldReflectionKeyValueIterator(Object O, boolean includeStatic) {
        if(canHandle(O)) {
            this.O = O;
            Fs = ReflectionUtil.getFields(O);
            if(!includeStatic) {                       // Not needed at this time
                List<Field> nsFs = new ArrayList<>();
                for(Field F : Fs) {
                    if(!Modifier.isStatic(F.getModifiers())) {
                        nsFs.add(F);
                    }
                }
                Fs = nsFs.toArray(new Field[0]);
            }
        } else {
            throw new UnsupportedObjectTypeException("Object non-null.", O);   // NullObjectException ??
        }
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    // Accessors (Computed)

    public Field getCurrentField() {     // Unresolved specific need as of yet
        return currentF;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Iterator

    @Override
    public boolean hasNext() {
        return I < Fs.length;
    }
    @Override
    public KeyValue next() {
        currentF = Fs[I++];
        currentF.setAccessible(true);
        String K = currentF.getName();
        Object V;
        try {
            V = currentF.get(O);
        } catch(Exception e) {
            // Should never happen due to setAccessible
            V = new FieldAccessWarning(O, K, e.getClass().getName() + ";" + e.getMessage());
        }
        return new KeyValue(K, V);
    }

    // KeyValueIterator

    @Override
    public boolean canHandle(Object O) {
        return !FUtil.isNull(O);
    }
}
