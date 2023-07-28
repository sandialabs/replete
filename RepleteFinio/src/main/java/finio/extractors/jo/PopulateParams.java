package finio.extractors.jo;


public class PopulateParams {


    ///////////
    // FIELD //
    ///////////

    protected HostNonTerminalCreator hostNonTerminalCreator;
    protected KeyValueIteratorCreator keyValueIteratorCreator;
    protected ObjectResolver keyResolver;
    protected ObjectResolver valueResolver;
    protected KeyValueRegistrar keyValueRegistrar;
    protected NonTerminalExpansionDecider nonTerminalExpansionDecider;
    protected RevisitPolicy revisitPolicy;
    protected PostFieldsModifier postFieldsModifier;
    protected Boolean recordJavaSource;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public HostNonTerminalCreator getHostNonTerminalCreator() {
        return hostNonTerminalCreator;
    }
    public KeyValueIteratorCreator getKeyValueIteratorCreator() {
        return keyValueIteratorCreator;
    }
    public ObjectResolver getKeyResolver() {
        return keyResolver;
    }
    public ObjectResolver getValueResolver() {
        return valueResolver;
    }
    public KeyValueRegistrar getKeyValueRegistrar() {
        return keyValueRegistrar;
    }
    public NonTerminalExpansionDecider getNonTerminalExpansionDecider() {
        return nonTerminalExpansionDecider;
    }
    public RevisitPolicy getRevisitPolicy() {
        return revisitPolicy;
    }
    public PostFieldsModifier getPostFieldsModifier() {
        return postFieldsModifier;
    }
    public Boolean isRecordJavaSource() {
        return recordJavaSource;
    }

    // Mutators

    public PopulateParams setHostNonTerminalCreator(HostNonTerminalCreator hostNonTerminalCreator) {
        this.hostNonTerminalCreator = hostNonTerminalCreator;
        return this;
    }
    public PopulateParams setKeyValueIteratorCreator(KeyValueIteratorCreator keyValueIterator) {
        keyValueIteratorCreator = keyValueIterator;
        return this;
    }
    public PopulateParams setKeyResolver(ObjectResolver keyResolver) {
        this.keyResolver = keyResolver;
        return this;
    }
    public PopulateParams setValueResolver(ObjectResolver valueResolver) {
        this.valueResolver = valueResolver;
        return this;
    }
    public PopulateParams setKeyValueRegistrar(KeyValueRegistrar keyValueRegistrar) {
        this.keyValueRegistrar = keyValueRegistrar;
        return this;
    }
    public PopulateParams setNonTerminalExpansionDecider(NonTerminalExpansionDecider nonTerminalExpansionDecider) {
        this.nonTerminalExpansionDecider = nonTerminalExpansionDecider;
        return this;
    }
    public PopulateParams setRevisitPolicy(RevisitPolicy revisitPolicy) {
        this.revisitPolicy = revisitPolicy;
        return this;
    }
    public PopulateParams setPostFieldsModifier(PostFieldsModifier postFieldsModifier) {
        this.postFieldsModifier = postFieldsModifier;
        return this;
    }
    public PopulateParams setRecordJavaSource(Boolean recordJavaSource) {
        this.recordJavaSource = recordJavaSource;
        return this;
    }

    // Overlay

    // NOTE: Populate Parameters Change Area - have to update this if a parameter is added.
    public void overlay(PopulateParams params) {
        if(params.hostNonTerminalCreator != null) {
            hostNonTerminalCreator = params.hostNonTerminalCreator;
        }
        if(params.keyValueIteratorCreator != null) {
            keyValueIteratorCreator = params.keyValueIteratorCreator;
        }
        if(params.keyResolver != null) {
            keyResolver = params.keyResolver;
        }
        if(params.valueResolver != null) {
            valueResolver = params.valueResolver;
        }
        if(params.keyValueRegistrar != null) {
            keyValueRegistrar = params.keyValueRegistrar;
        }
        if(params.nonTerminalExpansionDecider != null) {
            nonTerminalExpansionDecider = params.nonTerminalExpansionDecider;
        }
        if(params.revisitPolicy != null) {
            revisitPolicy = params.revisitPolicy;
        }
        if(params.postFieldsModifier != null) {
            postFieldsModifier = params.postFieldsModifier;
        }
        if(params.recordJavaSource != null) {
            recordJavaSource = params.recordJavaSource;
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // NOTE: Populate Parameters Change Area - have to update this if a parameter is added.
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PopulateParams [hostNonTerminalCreator=");
        builder.append(hostNonTerminalCreator.getClass().getName());
        builder.append(", keyValueIteratorCreator=");
        builder.append(keyValueIteratorCreator.getClass().getName());
        builder.append(", keyResolver=");
        builder.append(keyResolver.getClass().getName());
        builder.append(", valueResolver=");
        builder.append(valueResolver.getClass().getName());
        builder.append(", keyValueRegistrar=");
        builder.append(keyValueRegistrar.getClass().getName());
        builder.append(", nonTerminalExpansionDecider=");
        builder.append(nonTerminalExpansionDecider.getClass().getName());
        builder.append(", revisitPolicy=");
        builder.append(revisitPolicy);
        builder.append(", postFieldsModifier=");
        builder.append(postFieldsModifier.getClass().getName());
        builder.append(", recordJavaSource=");
        builder.append(recordJavaSource);
        builder.append("]");
        return builder.toString();
    }

    // NOTE: Populate Parameters Change Area - have to update this if a parameter is added.
    public void check() {
        if(hostNonTerminalCreator == null) {
            ex("host non-terminal creator");
        }
        if(keyValueIteratorCreator == null) {
            ex("key-value iterator creator");
        }
        if(keyResolver == null) {
            ex("key resolver");
        }
        if(valueResolver == null) {
            ex("value resolver");
        }
        if(keyValueRegistrar == null) {
            ex("key-value registrar");
        }
        if(nonTerminalExpansionDecider == null) {
            ex("non-terminal expansion decider");
        }
        if(revisitPolicy == null) {
            ex("revisit strategy");
        }
        if(postFieldsModifier == null) {
            ex("post fields modifier");
        }
        if(recordJavaSource == null) {
            ex("record java source");
        }
    }

    private void ex(String param) {
        throw new RuntimeException("Invalid populate parameter: " + param + " is null");
    }
}
