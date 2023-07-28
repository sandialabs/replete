package replete.web;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;


// TODO: Think about where URL validation happens in the web crawler currently
// and make sure it is reasonable.  I would think that only valid URLs are
// passed to this class.  Could convert main into JUnit tests.

public class UrlHostInfo implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private UrlHostType type;

    // 3 possible types: only one of these is non-null.
    // TODO: should ip be String[] too?  or hierParts be String? why different?
    private String ip;            // http://124.62.12.180[:port]/path/to/resource.html
                                  //   holds -> 124.62.12.180
    private String networkServer; // http://bobsmachine[:port]/path/to/resource.html
                                  //   holds -> bobsmachine
    private String[] hierParts;   // http://[[abc.]www.]name.com[:port]/path/to/resource.html
                                  //   holds -> [[abc.]www.]name.com
    private String host;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public UrlHostInfo(String url) {
        URL url2 = UrlUtil.url(url);
        host = url2.getHost();

        // Could have even better validation.
        if(host.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}")) {
            type = UrlHostType.IP;
            ip = host;

        // Performs no validation. What about http://bobs$machine/path ?
        } else if(host.indexOf(".") == -1) {
            type = UrlHostType.NETSRV;
            networkServer = host;

        // Performs no validation. What about http://1234/path ?
        } else {
            type = UrlHostType.HIERARCHY;
            hierParts = host.split("\\.");
        }
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public UrlHostType getType() {
        return type;
    }
    public String getIp() {
        return ip;
    }
    public String getNetworkServerName() {
        return networkServer;
    }
    public String[] getHierarchy() {
        return hierParts;
    }
    public String getHost() {
        return host;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        String ret = type.toString() + "(";
        switch(type) {
            case IP: ret += ip; break;
            case NETSRV: ret += networkServer; break;
            case HIERARCHY: ret += Arrays.toString(hierParts); break;
        }
        return ret + ")";
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws MalformedURLException {
        test("HIERARCHY([cnn, com])", "http://cnn.com");
        test("HIERARCHY([www, cnn, com])", "http://www.cnn.com:8080/hello");
        test("HIERARCHY([wwwcnn, com])", "ftp://wwwcnn.com/doesnt/matter");
        test("HIERARCHY([a, b, c, d, e])", "https://a.b.c.d.e/?num=99");
        test("NETSRV(bobsmachine)", "http://bobsmachine/network/webpage");
        test("NETSRV(bobs-machine)", "https://bobs-machine/network/webpage");
        test("IP(127.32.43.100)", "http://127.32.43.100/network/webpage");
        test("IP(127.32.43.100)", "http://abc/network/webpage");

        // TODO: Many more tests can be added.  Make sure conforms
        // with validation principles and that invalid URLs don't
        // fall into the wrong category. http://-a?  http://123?
        // http://en.wikipedia.org/wiki/Domain_Name_System  # Domain name syntax
    }

    private static void test(String expected, String input) throws MalformedURLException {
        UrlHostInfo info = new UrlHostInfo(input);
        String actual = info.toString();
        System.out.print(expected + " <==> " + input);
        if(expected.equals(actual)) {
            System.out.println(" -- PASSED");
        } else {
            System.out.println("-- FAILED!  Was: " + actual);
            throw new RuntimeException("Failed!");
        }
    }
}
