package finio.platform.exts.sc;

import finio.core.impl.FMap;
import finio.extractors.jo.ObjectResolver;
import replete.scrutinize.core.IBaseSc;
import replete.scrutinize.core.ScFieldResult;

public class FieldResultResolver implements ObjectResolver {
    @Override
    public boolean canHandle(Object O) {
        return O instanceof ScFieldResult;
    }
    @Override
    public Object resolve(Object O) {
        ScFieldResult result = (ScFieldResult) O;
        if(result.getException() != null) {
            return "<Error: " + result.getException().getClass().getSimpleName() + ">";
        }
        Object value = result.getValue();
        if(value instanceof IBaseSc) {
            return value;
        }
        FMap m = new FMap();
        m.putSysValue(result.getValue());
        m.put("className", result.getClassName());
        return m;
    }

    @Override
    public String toString() {
        return "Field Result Object Resolver (From Scrutinization)";
    }
}
