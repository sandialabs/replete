package finio.renderers.path;

import finio.core.KeyPath;

public class JavaKeyPathRenderer implements KeyPathRenderer {
    public String render(KeyPath P) {
        return P.toString();
    }
}
