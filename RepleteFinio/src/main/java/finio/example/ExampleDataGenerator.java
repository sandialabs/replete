package finio.example;

import static finio.core.impl.FMap.A;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import finio.core.FConst;
import finio.core.NonTerminal;
import finio.core.impl.FMap;
import finio.core.managed.ImmutableManagedValueManager;
import finio.core.managed.RandomManagedValueManager;
import finio.extractors.JsonExtractor;
import replete.ui.images.concepts.CommonConcepts;
import replete.util.DateUtil;
import replete.util.User;
import replete.web.UrlUtil;

public class ExampleDataGenerator {


    public static NonTerminal createExampleData() {
//        MapManager mgr = new TrivialMapManager();
//        return mgr.createManagedNonTerminal();
//    }
//
//    public static NonTerminal createExampleDataInternal() {

        // Just to have some test data to play around with each time
        // the application is loaded.
        String initialContent =
            "{Colorado: {capital: 'Denver', admission: '38th', @@meta: {@@images: 'pencil.gif'}}, " +
            "'New Mexico': {capital: 'Santa Fe', admission: '47th'}, " +
            "Montana: {capital: 'Helena', admission: '41st'}, " +
            "path0: 'C:\\\\path\\\\here\\\\one; C:\\\\some\\\\other\\\\path; C:\\\\yet\\\\another\\\\path', " +
            "path1: 'C:\\\\path\\\\here\\\\one; C:\\\\some\\\\other\\\\path; C:\\\\yet\\\\another\\\\path', " +
            "path2: 'C:\\\\path\\\\here\\\\one; C:\\\\some\\\\other\\\\path; C:\\\\yet\\\\another\\\\path', " +
            "}";

        JsonExtractor X = new JsonExtractor(initialContent);
        NonTerminal M = X.extract();
        M.putSysMeta(FConst.SYS_IMAGES, CommonConcepts.OPEN);
        M.putSysMeta("time", DateUtil.toLongString(System.currentTimeMillis()));
        M.putSysMeta("source", "JSON Document");
        M.putSysMeta(FConst.SYS_ALT_KEY, initialContent);

        M.put("mv1", new RandomManagedValueManager());
        M.put("mv2", new ImmutableManagedValueManager(FMap.A()));
        M.put("null", null);
        M.put("file", User.getDesktop("Browsers"));
        M.put("url", UrlUtil.url("http://cnn.com"));
        M.put("uri", UrlUtil.uri("http://cnn.com"));
        M.put("rect", new Rectangle(50, 50, 500, 500));
        M.put(new Rectangle(40, 40, 400, 400), 444);
        M.put(new Car(), "car-value");
        M.put("map-like1", createTestJavaMap());

        int[] arr = new int[] {1,6,4,8,2,3,23,64,-2};
        M.put("map-like2", arr);

        List<String> list = new ArrayList<>();
        list.add("Rio");
        list.add("Brasilia");
        list.add("Argentina");
        list.add("Bucharest");
        M.put("map-like3", list);

        byte[] b = new byte[123];
        M.put("bin0", b);

        Byte[] b2 = new Byte[123];
        M.put("bin1", b2);

        M.put("mazda", new Car());

        Map sa = new LinkedHashMap<>();
        sa.put("Executive", "Pretoria");
        sa.put("Legislative", "Cape Town");
        sa.put("Judicial", "Bloemfontein");
        Map m = new LinkedHashMap<>();
        m.put("Colorado", "Denver");
        m.put("New Mexico", "Santa Fe");
        m.put("South Africa", sa);

        M.put("m", m);

        M.put("DiffableMaps", createDiffableMaps());

        return M;
    }

    public static NonTerminal createDiffableMaps() {
        FMap A = A(
            "ARR", new int[] {1, 2, 3},
            "y", "kk",
            "x", true,
            "A", 1L,
            "B", 2,
            "C", A(
                "D", 4,
                "E", 5,
                "M", A(
                    "One", 1,
                    "Two", '2',
                    "Three", 3
                )
            )
        );
        FMap B = A(
            "ARR", new int[] {1, 2, 4},
            "y", "KK",
            "x", 7,
            "A", 10,
            "C", A(
                "D", 40,
                "X", 77,
                "M", A(
                    "One", 1,
                    "Two", "2",
                    "Four", 4
                )
            ),
            "Q", 55,
            "R", A(
                "V", 2
            )
        );
        return A("Left", A, "Right", B);
    }

    public static Map<String, Object> createTestJavaMap() {
        Object[] o2 = new Object[3];
        Object[] o = new Object[3];
        o2[0] = "Hi";
        o2[1] = "There";
        o2[2] = "Dude";       // o; XStream couldn't handle this
        o[0] = 2;
        o[1] = 3;
        o[2] = o2;

        Map<String, Object> m2 = new LinkedHashMap<>();
        m2.put("Denver", 123);
        m2.put("recurse", 55);
        m2.put("Boulder", 323);
        m2.put("Parker", 234);
        m2.put("Durango", UrlUtil.url("http://www.google.com"));

        Map<String, Object> m3 = new LinkedHashMap<>();
        m3.put("San Diego", 25);
        m3.put("Sacramento", 432);

        List<Boolean> bools = new ArrayList<>();
        bools.add(true);
        bools.add(false);
        bools.add(true);
        bools.add(false);
        bools.add(true);

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("Colorado", m2);
        m.put("California", m3);
        m.put("Florida", 43);
        m.put("Iterable", bools);
        m.put("Arrays!", o);
        return m;
    }
}
