package replete.xstream;

import replete.text.StringUtil;
import replete.ui.ClassNameSimplifier;

public class XStreamClassNameSimplifier implements ClassNameSimplifier {
    private static final String prefix = "com.thoughtworks.xstream";

    @Override
    public boolean appliesTo(String className) {
        return className.startsWith(prefix);
    }

    @Override
    public String simplifyAndMarkupClassName(String className) {
        String rest = StringUtil.removeStart(className, prefix);
        return "<i>c</i>.<i>t</i>.<b>X</b>" + rest;
    }

}
