package replete.web;


import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import replete.collections.RLinkedHashMap;
import replete.equality.EqualsUtil;
import replete.errors.RuntimeConvertedException;
import replete.extensions.web.Base64;
import replete.io.FileUtil;
import replete.numbers.NumUtil;

/**
 * @author Derek Trumbo
 */

public class UrlUtil {
    public static String getWebContent(String target, String userName, String password, String acceptType) throws IOException {
        URL url = new URL(target);
        URLConnection cxn = url.openConnection();
        if(acceptType != null) {
            cxn.setRequestProperty("Accept", acceptType);
        }
        if(userName != null && password != null) {
            cxn.setRequestProperty("Authorization", "Basic " + Base64.encodeString(userName + ":" + password));
        }
        cxn.connect();
        return FileUtil.getTextContent(cxn.getInputStream());
    }
    public static String enc(String q) {
        try {
            return URLEncoder.encode(q, "UTF-8");
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }
    public static URL url(String url) {
        try {
            return new URL(url);
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }
    public static URL url(URL base, String path) {
        try {
            return new URL(base, path);
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }
    public static URI uri(String uri) {
        try {
            return new URI(uri);
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }
    public static boolean isUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public static String getHostUri(String urlStr) {
        URI uri = UrlUtil.uri(urlStr);
        String host = uri.getHost();
        return host;
    }

    public static String getHostUrl(String urlStr) {
        URL url = UrlUtil.url(urlStr);
        String host = url.getHost();
        return host;
    }

    public static String getHostStringOnly(String url) {
        if(url == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }
        int uLen = url.length();
        int prIndex = url.indexOf("://");
        if(prIndex == -1) {
            throw new IllegalArgumentException("no protocol: " + url);
        }
        if(url.substring(0, prIndex).trim().isEmpty()) {
            throw new IllegalArgumentException("no protocol: " + url);
        }

        // Calculate indicies of primary character markers.
        int paIndex = url.indexOf("/", prIndex + 3);
        int qIndex  = url.indexOf('?', paIndex + 1);
        int fIndex  = url.indexOf('#', Math.max(paIndex, qIndex) + 1);

        // Find authority
        int authEnd = NumUtil.smallestNonNegative(paIndex, qIndex, fIndex, uLen);
        String auth = url.substring(prIndex + 3, authEnd);
        if(auth.isEmpty()) {
            throw new IllegalArgumentException("no authority: " + url);
        }

        int atIndex = auth.indexOf('@');
        String hostPort = atIndex == -1 ? auth : auth.substring(atIndex + 1);

        // Split hostPort into host and port
        int portColon = hostPort.indexOf(':');
        String host = portColon == -1 ? hostPort : hostPort.substring(0, portColon);
        if(host.isEmpty()) {
            throw new IllegalArgumentException("no host: " + url);
        }

        return host;
    }

    public static RUrl parseUrl(String url) {
        if(url == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }

        int uLen = url.length();
        int prIndex = url.indexOf("://");
        if(prIndex == -1) {
            throw new IllegalArgumentException("no protocol: " + url);
        }
        if(url.substring(0, prIndex).trim().isEmpty()) {
            throw new IllegalArgumentException("no protocol: " + url);
        }

        RUrl rUrl = new RUrl();

        String protocol = url.substring(0, prIndex);
        rUrl.setProtocol(protocol);

        // Calculate indicies of primary character markers.
        int paIndex = url.indexOf("/", prIndex + 3);
        int qIndex  = url.indexOf('?', paIndex + 1);
        int fIndex  = url.indexOf('#', Math.max(paIndex, qIndex) + 1);

        // Find authority
        int authEnd = NumUtil.smallestNonNegative(paIndex, qIndex, fIndex, uLen);
        String auth = url.substring(prIndex + 3, authEnd);
        if(auth.isEmpty()) {
            throw new IllegalArgumentException("no authority: " + url);
        }

        // Split authority into user/password and host/port
        int atIndex = auth.indexOf('@');
        String userPw = atIndex == -1 ? null : auth.substring(0, atIndex);
        String hostPort = atIndex == -1 ? auth : auth.substring(atIndex + 1);

        if(userPw != null) {
            int pwColon = userPw.indexOf(':');
            String user = pwColon == -1 ? userPw : userPw.substring(0, pwColon);
            String pw = pwColon == -1 ? null : userPw.substring(pwColon + 1);
            rUrl.setUser(user);
            rUrl.setPassword(pw);
        }

        // Split hostPort into host and port
        int portColon = hostPort.indexOf(':');
        String host = portColon == -1 ? hostPort : hostPort.substring(0, portColon);
        String port = portColon == -1 ? null : hostPort.substring(portColon + 1);
        if(host.isEmpty()) {
            throw new IllegalArgumentException("no host: " + url);
        }

        rUrl.setHost(host);
        rUrl.setPort(port);

        if(paIndex != -1) {
            int pathEnd = NumUtil.smallestNonNegative(qIndex, fIndex, uLen);
            String path = url.substring(paIndex + 1, pathEnd);
            rUrl.setPath(path);
        }
        if(qIndex != -1) {
            int queryEnd = NumUtil.smallestNonNegative(fIndex, uLen);
            String query = url.substring(qIndex + 1, queryEnd);
            rUrl.setQuery(query);
        }
        if(fIndex != -1) {
            int fragEnd = NumUtil.smallestNonNegative(uLen);
            String fragment = url.substring(fIndex + 1, fragEnd);
            rUrl.setFragment(fragment);
        }

        return rUrl;
    }

    public static String validateHostPort(String url) {
        if(url == null) {
            return "cannot be null";
        }
        int colon = url.indexOf(':');
        if(colon == -1) {
            return "invalid host/port format";
        }
        String host = url.substring(0, colon);
        if(host.trim().isEmpty()) {
            return "no host provided";
        }
        String port = url.substring(colon + 1);
        if(port.trim().equals("")) {
            return "no port number provided";
        }
        String compPattern = "[a-zA-Z0-9]([a-zA-Z0-9-]+[a-zA-Z0-9]|[a-zA-Z0-9])?";
        String hostPattern = compPattern + "(\\." + compPattern + ")*";
        if(!host.matches(hostPattern)) {
            return "invalid host";
        }
        if(!NumUtil.isInt(port)) {
            return "port number must be an integer";
        }
        if(NumUtil.i(port) <= 0) {
            return "invalid port number";
        }
        return null;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
//        System.out.println(url("HTTP://cnn.com").getProtocol());
        Map<String, String> map = new RLinkedHashMap<>(
            "", "invalid host/port format",
            "abc", "invalid host/port format",
            "abc:", "no port number provided",
            "abc:-10", "invalid port number",
            ":aaa", "no host provided",
            ":10", "no host provided",
            "10:10", null,
            "abc:10", null,
            "abc-:a", "invalid host",
            "abc-:10", "invalid host",
            "a-h:10", null,
            "a-h.h.j:10", null,
            "a-h.:10", "invalid host",
            "proxy.sandia.gov:80", null
        );
        for(String k : map.keySet()) {
            String expected = map.get(k);
            String actual = validateHostPort(k);
            if(!EqualsUtil.equals(expected, actual)) {
                System.out.println("Invalid " + k);
                System.out.println(expected);
                System.out.println("was " + actual);
            }
        }
//        String[] parsedUrls = {
//            "http://h:99/?q=9&w=3#3434"
//        };
//
//        for(String pUrl : parsedUrls) {
//            System.out.println("URL=" + pUrl);
//            try {
//                RUrl rUrl = parseUrl(pUrl);
//                System.out.println("  PRS=" + rUrl);
//                System.out.println("  AFT=" + rUrl.getAfterHostPort());
//                System.out.println("  UP =" + rUrl.getUserPassword());
//                System.out.println(rUrl.toString2());
//                if(!pUrl.equals(rUrl.toString())) {
//                    System.out.println("--DIFFERENT!--");
//                }
//            } catch(Exception e) {
//                System.out.println("  ERR=" + e.getMessage());
//                e.printStackTrace();
//            }
//        }
//
//        if(true) {
//            return;
//        }

//        String[] urls = {
//            "abc://com",
//            "http://com",
//            "http://cnn.com",
//            "http://cnn.com:8080",
//            "http://cnn.com:/",
//            "http://cnn.com/a",
//            "http://cnn.com?a",
//            "http://cnn.com#a",
//            "http://user:pw@cnn.com#a",
//            "abc://user:pw@cnn.com#a",
//            "http://sub.sub%cnn.com",
//            null,
//            "",
//            "puppy",
//            "   ://cnn.com",
//            "http://",
//            "http://?#",
//        };

//        IntSummaryStats statsR = new IntSummaryStats();
//        IntSummaryStats statsU = new IntSummaryStats();
//        IntSummaryStats statsD = new IntSummaryStats();
//        for(int i = 0; i < 100000; i++) {
//            boolean oneDiff = false;
//            for(String url : urls) {
//                String rHost, uHost, dHost;
//                try {
//                    long T = System.nanoTime();
//                    rHost = getHostUri(url);
////                    statsR.add((int) (System.nanoTime() - T));
//                } catch(Exception e) {
//                    rHost = "<UError: " + e.getMessage() + ">";
//                }
//                try {
//                    long T = System.nanoTime();
//                    uHost = getHostUrl(url);
////                    statsU.add((int) (System.nanoTime() - T));
//                } catch(Exception e) {
//                    uHost = "<UError: " + e.getMessage() + ">";
//                }
//                try {
//                    long T = System.nanoTime();
//                    dHost = getHostStringOnly(url);
////                    statsD.add((int) (System.nanoTime() - T));
//                } catch(Exception e) {
//                    dHost = "<DError: " + e.getMessage() + ">";
//                }
//
//                if(true) {
//                    boolean same = uHost.equals(dHost);
//                    String s = same ? "Same!" : "Diff :(";
//                    System.out.println("URL: {" + url + "}" +
//                        "\n    R[" + rHost +
//                        "]\n    U[" + uHost +
//                        "]\n    D[" + dHost + "]\n    " +
//                        s);
//                    if(!same) {
//                        oneDiff = true;
//                    }
//                }
//            }
//            if(oneDiff) {
//                System.out.println("One Different!");
//            }
//        }

//        System.out.println(statsR);
//        System.out.println(statsU);
//        System.out.println(statsD);
//        double ratio1 = statsR.getAverage() / statsD.getAverage();
//        System.out.println("URI over Str: " + ratio1);
//        double ratio2 = statsU.getAverage() / statsD.getAverage();
//        System.out.println("URL over Str: " + ratio2);
    }
}
