package replete.diff;

import replete.plugins.PluginManager;
import replete.plugins.test.DiagnosticsPlugin;
import replete.text.StringUtil;

public class Test {

    public static void main(String[] args) {
        DiagnosticsPlugin plugin = new DiagnosticsPlugin(
//            new TestObjDifferGenerator(),
            new SubObjDifferGenerator()
        );
        PluginManager.initialize(plugin);

        SubObjDifferParams s1 = new SubObjDifferParams().setIncludeT(true);

        TestObjDifferParams p1 = new TestObjDifferParams();
        TestObjDifferParams p2 = new TestObjDifferParams().setIncludeY(true);
        TestObjDifferParams p3 = new TestObjDifferParams().setIncludeY(true).setSubObjDifferParams(s1);

        TestObjDiffer[] differs = {
            new TestObjDiffer(p1),
            new TestObjDiffer(p2),
            new TestObjDiffer(p3)
        };

        SubObj subObj = new SubObj(77, 88);
        SubObj subObj2 = new SubObj(77, 99);
        subObj.words.add("aa");
        subObj.words.add("bb");
        subObj.words.add("cc");
        subObj2.words.add("AA");
        subObj2.words.add("bb");
        subObj2.words.add("bb");

        TestObj[][] pairs = {
            {new TestObj(10, 10),         new TestObj(10, 10)},
            {new TestObj(10, 10),         new TestObj(20, 10)},
            {new TestObj(10, 10),         new TestObj(10, 20)},
            {new TestObj(10, 10),         new TestObj(20, 20)},
            {new TestObj(10, 10, subObj), new TestObj(10, 10)},
            {new TestObj(10, 10),         new TestObj(20, 10, subObj)},
            {new TestObj(10, 10, subObj), new TestObj(10, 20, subObj)},
            {new TestObj(10, 10, subObj), new TestObj(20, 20, subObj2)},
        };

        for(TestObjDiffer differ : differs) {
            System.out.println("Differ: " + differ.getClass().getName());
            for(TestObj[] pair : pairs) {
                DiffResult r = differ.diff(pair[0], pair[1]);
                System.out.println("    Pair: " + pair[0] + " & " + pair[1] + "; Is Diff? " + StringUtil.yesNo(r.isDiff()));
                System.out.println(r.getComparison().render(2));
            }
        }
    }
}
