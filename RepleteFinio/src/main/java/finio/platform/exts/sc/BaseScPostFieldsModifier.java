package finio.platform.exts.sc;

import java.util.Set;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.pop.NonTerminalPopulator;
import finio.extractors.jo.PopulateParamsProvider;
import finio.extractors.jo.PostFieldsModifier;
import replete.scrutinize.core.BaseSc;

public class BaseScPostFieldsModifier implements PostFieldsModifier {

    @Override
    public boolean canHandle(Object O) {
        return O instanceof BaseSc;
    }

    @Override
    public void modify(NonTerminal M, Object O,
                       NonTerminalPopulator populator,
                       Set<Object> visited, KeyPath P,
                       PopulateParamsProvider params) {
        BaseSc sc = (BaseSc) O;
        if(sc.getStaticInfoSzLink() != null) {
            Object Onext = sc.getStaticInfoSzLink().getStaticFields();
            populator.populate(M, Onext, visited, P, params);
        }
    }

    @Override
    public String toString() {
        return "Scrutinization Post Fields Modifier (From Scrutinization)";
    }

}
