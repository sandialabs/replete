package finio.ui.bytes;

import java.net.URL;

public class UrlSource extends Source {
    URL url;
    public UrlSource(URL url) {
        super();
        this.url = url;
    }
    @Override
    public String toString() {
        return "From Web: " + url;
    }
}