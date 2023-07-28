package finio.extractors;

import finio.core.NonTerminal;
import finio.extractors.jo.JavaObjectUberExtractor;
import finio.extractors.jo.NonTerminalLikeDefaultPopulateParamsCreator;
import finio.extractors.jo.PopulateParams;
import finio.extractors.jo.PopulateParamsProvider;
import finio.platform.exts.sc.BaseScPostFieldsModifier;
import finio.platform.exts.sc.FieldResultResolver;
import replete.scrutinize.core.Scrutinizer;
import replete.scrutinize.wrappers.ScrutinizationSc;

public class LocalScrutinizationExtractor extends NonTerminalExtractor {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public LocalScrutinizationExtractor() {}


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public NonTerminal extractInner() {
        Scrutinizer.initialize();
        ScrutinizationSc S = new ScrutinizationSc();

        try {
            S.load();

            PopulateParams params = new NonTerminalLikeDefaultPopulateParamsCreator().create()
                .setValueResolver(new FieldResultResolver())
                .setPostFieldsModifier(new BaseScPostFieldsModifier());

            PopulateParamsProvider provider = new PopulateParamsProvider(params);
            JavaObjectUberExtractor X = new JavaObjectUberExtractor(S, provider);

            return X.extract();
        } catch(Exception e) {
            throw new RuntimeException("Local Scrutinization Failed", e);
        }
    }

    @Override
    protected String getName() {
        return "Local Scrutinization Extractor";
    }
}
