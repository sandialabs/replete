package finio.extractors;

import static finio.core.KeyPath.KP;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.impl.FMap;
import finio.extractors.jo.JavaObjectMapAwareExtractor;
import finio.extractors.jo.JavaObjectReflectionExtractor;
import replete.text.StringUtil;

public class UrlExtractor extends NonTerminalExtractor {


    ////////////
    // FIELDS //
    ////////////

    private URL U;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public UrlExtractor(URL U) {
        this.U = U;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public NonTerminal extractInner() {

        UrlHostInfo info = new UrlHostInfo(U);
        KeyPath P = null;
        switch(info.getType()) {
            case HIERARCHY:  P = KP(U.getHost(), "\\.", true);  break;
            case IP:         P = KP(U.getHost(), "\\.");        break;
            case NETSRV:     P = KP(U.getHost(), "\\.");        break;
        }

        if(U.getPort() != -1) {
            P.append(U.getPort());
        }

        if(!StringUtil.isBlank(U.getPath())) {
            String path = U.getPath();
            if(path.startsWith("/")) {
                path = StringUtil.snip(path, 1);
            }
            P.append(path.split("/"));
        }

        NonTerminal Msuper = FMap.A();

        NonTerminal head = P.toHierarchical(U);
        Msuper.put("Hierarchy", head);

        NonTerminal M = new JavaObjectReflectionExtractor(U).extract();
        Msuper.put("POJO", M);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("protocol", U.getProtocol());
        map.put("host",     U.getHost());
        map.put("port",     U.getPort());
        map.put("path",     U.getPath());
        map.put("query",    U.getQuery());

        NonTerminal M2 = new JavaObjectMapAwareExtractor(map).extract();
        Msuper.put("Simple", M2);
        return Msuper;
    }

    @Override
    protected String getName() {
        return "URL Extractor";
    }
}
