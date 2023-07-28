package replete.ui.params.hier.test;

import java.net.URL;

import replete.web.UrlUtil;


public class PortRule extends Rule {


    ////////////
    // FIELDS //
    ////////////

    public static final int DEFAULT_HTTP_PORT = 80;
    public static final int DEFAULT_HTTPS_PORT = 443;

    public int port;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PortRule(int port) {
        this.port = port;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean appliesTo(String urlStr) {
        URL url = UrlUtil.url(urlStr);
        int p = url.getPort();
        if(url.getProtocol().equals("http")) {
            if(p == -1) {
                p = DEFAULT_HTTP_PORT;
            }
        } else if(url.getProtocol().equals("https")) {
            if(p == -1) {
                p = DEFAULT_HTTPS_PORT;
            }
        }
        return p == port;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        test(54, "http://www.cnn.com:54", true);
        test(80, "http://www.cnn.com:80", true);
        test(80, "http://www.cnn.com", true);
        test(80, "http://www.cnn.com:17", false);
        test(17, "http://www.cnn.com:80", false);
        test(80, "https://www.cnn.com:80", true);
        test(80, "https://www.cnn.com", false);
        test(443, "https://www.cnn.com:443", true);
        test(443, "https://www.cnn.com", true);
        test(443, "https://www.cnn.com:55", false);
        test(55, "https://www.cnn.com:443", false);
    }

    private static void test(int port, String url, boolean applies) {
        PortRule pr = new PortRule(port);
        if(applies != pr.appliesTo(url)) {
            throw new RuntimeException(port + " / " + url);
        }
    }
}
