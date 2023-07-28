package finio.extractors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import finio.core.FUtil;
import finio.core.NonTerminal;
import finio.core.impl.FMap;
import finio.plugins.extpoints.StringMatchResult;
import finio.plugins.extpoints.StringPatternMatcher;
import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;
import replete.text.StringUtil;

public class JavaObjectInfoExtractor extends NonTerminalExtractor {


    ////////////
    // FIELDS //
    ////////////

    // Constant

    private static final String STATIC_KEY = "^static";

    // Core

    private Object V;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public JavaObjectInfoExtractor(Object V) {
        this.V = V;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public NonTerminal extractInner() {
        NonTerminal M = createBlankNonTerminal();
        if(FUtil.isNull(V)) {
            return null;  // [OPTION] throw exception
        }
        NonTerminal[] info = getClassInfo(V);
        M.put("Classes", info[0]);
        M.put("Interfaces", info[1]);
        M.put("Value-Specific", getValueSpecific(V));
        return M;
    }

    private NonTerminal[] getClassInfo(Object o) {
        NonTerminal C = createBlankNonTerminal();
        NonTerminal I = createBlankNonTerminal();
        Class<?> c = o.getClass();
        while(c.getSuperclass() != null) {
            Class<?>[] i = c.getInterfaces();
            for(Class<?> cx : i) {
                I.put(cx.getName(), describeClass(cx));
            }
            C.put(c.getName(), describeClass(c));
            c = c.getSuperclass();
        }
        return new NonTerminal[] {C, I};
    }

    private NonTerminal describeClass(Class<?> clazz) {
        NonTerminal M = createBlankNonTerminal();

        Set<String> fNames = new TreeSet<>();
        for(Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String s = field.getName();
            s += " : " + field.getType().getName();
            fNames.add(s);
        }
        if(!fNames.isEmpty()) {
            NonTerminal F = createBlankNonTerminal();
            for(String fName : fNames) {
                F.put(fName, null);
            }
            M.put("Fields", F);
        }

        Set<String> mNames = new TreeSet<>();
        for(Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);
            String s = method.getName() + "(";
            for(Class<?> pType : method.getParameterTypes()) {
                s += pType.getName() + ", ";
            }
            if(s.endsWith(", ")) {
                s = StringUtil.cut(s, 2);
            }
            s += ") : ";
            s += method.getReturnType().getName();
            mNames.add(s);
        }
        if(!mNames.isEmpty()) {
            NonTerminal Q = createBlankNonTerminal();
            for(String mName : mNames) {
                Q.put(mName, new FMap());
            }
            M.put("Methods", Q);
        }

        Class<?>[] i = clazz.getInterfaces();
        if(i.length != 0) {
            NonTerminal I = createBlankNonTerminal();
            for(Class<?> ifc : i) {
                I.put(ifc.getName(), new FMap());
            }
            M.put("Interfaces", I);
        }

        return M;
    }

    private Object getValueSpecific(Object V) {
        NonTerminal M = createBlankNonTerminal();
        M.put("Value", V);

        if(FUtil.isNull(V)) {
            // Does not happen

        } else if(FUtil.isStringOrChar(V)) {
            addStringInfo(V, M);

        } else if(FUtil.isBoolean(V)) {
            // Nothing to provide

        } else if(FUtil.isNumber(V)) {
            addNumberInfo(V, M);

        } else if(FUtil.isNonTerminal(V)) {
            addNonTerminal(V, M);

//        } else if(FUtil.isUnexpandableWarning(V)) {
//            String reason = ((UnexpandableWarning) V).getReason();
//            valueString = StringUtil.cleanHtml("<Unexpandable: " + reason + ">");
//        } else {
//            valueString = StringUtil.cleanHtml(AUtil.toDiagnosticString(V));
        }

//        if(AUtil.isBinary(V)) {
//            return iconBinary;
//        }
//        if(AUtil.isOtherJavaObject(V)) {
//            return iconSemiterminal;
//        }
        return M;
    }

    private void addNonTerminal(Object V, NonTerminal M) {
        NonTerminal N = (NonTerminal) V;
        M.put("Size", N.size());
        // Can identify other things about a non-terminal?
        // Does it look like a:
        //  - Questionnaire
        //  - Doctor's form
        //  - Imported from CSV file
        //  - Degree of value coincidence at depth 1, 2, 3, etc.
        //  - Are values unique?  Can values become keys?
        //  - Are keys compatible with a list?
        //  - Do keys look like a flattened hierarchy?
        //  - ...
    }


    private void addStringInfo(Object V, NonTerminal M) {
        String str = (String) V;
        M.put("Length", str.length());
        M.put("Natural Languages", "en-US (example)");
        M.put("Programming Languages", "JavaScript (example)");
        NonTerminal P = createBlankNonTerminal();
        M.put("Interpretations", P);
        List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(StringPatternMatcher.class);
        if(exts.size() != 0) {
            for(ExtensionPoint ext : exts) {
                final StringPatternMatcher m = (StringPatternMatcher) ext;
                String id = PluginManager.getExtensionId(ext);
                StringMatchResult result = m.match(str);
                if(result != null) {
                    NonTerminal Mdetails = createBlankNonTerminal();
                    P.put(m.getName(), Mdetails);
                    Mdetails.put("id", id);
                    Mdetails.put("probability", result.getProbability());
                    if(result.getDetails() != null) {
                        Mdetails.putAll(result.getDetails());
                    }
                }
            }
        }
    }

    private void addNumberInfo(Object V, NonTerminal M) {
        Number n = (Number) V;

        if(FUtil.isIntegral(V)) {
            long l = n.longValue();
            M.put("Parity", (l % 2 == 0 ? "even" : "odd"));
            if(l > 0) {
                M.put("prime", "UNK");
            }
        }

        String type = "UNK";
        if(FUtil.isIntegral(V)) {
            long l = n.longValue();
            if(l > 0) {
               type = "natural, whole, integer, rational, real";
            } else if(l >= 0) {
                type = "whole, integer, rational, real";
            } else {
                type = "integer, rational, real";
            }
        } else {
            type = "rational ? irrational, real";
        }

        M.put("Type", type);
        M.put("Sign", n.doubleValue() == 0.0 ? "0" : n.doubleValue() > 0.0 ? "+": "-");
        NonTerminal P = createBlankNonTerminal();
        M.put("Interpretations", P);

//        List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(NumberPatternMatcher.class);
//        if(exts.size() != 0) {
//            for(ExtensionPoint ext : exts) {
//                final NumberPatternMatcher m = (NumberPatternMatcher) ext;
//                String id = PluginManager.getExtensionId(ext);
//                StringMatchResult result = m.match(str);
//                if(result != null) {
//                    P.put(m.getName(), "FOUND! (" + id + ")");
//                }
//            }
//        }
    }

    @Override
    protected String getName() {
        return "Java Object Information Extractor";
    }
}
