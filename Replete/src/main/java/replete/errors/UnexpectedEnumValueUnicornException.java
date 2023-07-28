package replete.errors;

import replete.util.ReflectionUtil;

// This is the kind of exception that should never, ever
// be thrown.  Specifically, this "enum" version of a
// unicorn exception is only supposed to be used at the
// end of a long list of if(x == Enum.VALUE) checks after
// all "expected" or "known" enum values have been checked
// as in:
//
//    if(value == EnumClass.VALUE_1) {
//        // Code A
//    } else if(value == EnumClass.VALUE_2) {
//        // Code B
//    } else if(value == EnumClass.VALUE_3) {
//        // Code C
//    } else {
//        throw new UnexpectedEnumValueUnicornException(value);
//    }
//
// Where the enum looks like:
//
//    public enum EnumClass {
//        VALUE_1,
//        VALUE_2,
//        VALUE_3
//    }
//
// Why not just have "Code C" in an "} else {" block and
// be done with it?  Because the pattern shown above is
// a lot more explicit and can eliminate some otherwise
// potentially very subtle errors when more values than you
// expect fall into the if block because someone added
// values to an enum but didn't know to visit X, Y, and Z
// places in the code to find out that the enum was being
// used in an if/else block somewhere.

public class UnexpectedEnumValueUnicornException extends UnicornException {
    private Object object;
    public UnexpectedEnumValueUnicornException(Object object) {
        this.object = object;
    }
    @Override
    public String getMessage() {
        if(object == null || !object.getClass().isEnum()) {
            return "<this exception is supposed to be used with non-null enum values only>";
        }
        String name = ReflectionUtil.invoke(object, "name");
        return
            "Unexpected enum value '" + name +
            "' in enum class '" + object.getClass().getName() + "'";
    }
}
