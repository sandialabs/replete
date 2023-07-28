package finio.extractors.jo;


public class IdentityObjectResolver implements ObjectResolver {
    @Override
    public boolean canHandle(Object O) {
        return true;
    }
    @Override
    public Object resolve(Object O) {
        return O;
    }

    @Override
    public String toString() {
        return "Identity Object Resolver";
    }
}
