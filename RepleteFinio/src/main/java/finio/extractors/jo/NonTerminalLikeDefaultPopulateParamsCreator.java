package finio.extractors.jo;

import replete.plugins.PluginManager;

public class NonTerminalLikeDefaultPopulateParamsCreator implements DefaultPopulateParamsCreator {

    // NOTE: Populate Parameters Change Area - have to update this if a parameter is added.
    @Override
    public PopulateParams create() {
        return
            new PopulateParams()
                .setHostNonTerminalCreator((HostNonTerminalCreator) PluginManager.getExtensionById(FMapHostNonTerminalCreator.class.getName()))
                .setKeyValueIteratorCreator((KeyValueIteratorCreator) PluginManager.getExtensionById(NonTerminalLikeKeyValueIteratorCreator.class.getName()))
                .setKeyResolver((ObjectResolver) PluginManager.getExtensionById(IdentityObjectResolver.class.getName()))
                .setValueResolver((ObjectResolver) PluginManager.getExtensionById(IdentityObjectResolver.class.getName()))
                .setKeyValueRegistrar((KeyValueRegistrar) PluginManager.getExtensionById(DefaultKeyValueRegistrar.class.getName()))
                .setNonTerminalExpansionDecider((NonTerminalExpansionDecider) PluginManager.getExtensionById(NonTerminalLikeNonTerminalExpansionDecider.class.getName()))
                .setRevisitPolicy(RevisitPolicy.NO_DUP_PATH)
                .setPostFieldsModifier((PostFieldsModifier) PluginManager.getExtensionById(NoOpPostFieldsModifier.class.getName()))
                .setRecordJavaSource(true);
    }

    @Override
    public String toString() {
        return "Non-Terminal Like";
    }
}
