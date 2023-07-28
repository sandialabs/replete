package replete.scripting.rscript.parser.values;

public class ConstantValue extends NodeValue {
    private Object value;

    public ConstantValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        ConstantValue other = (ConstantValue) obj;
        if(value == null) {
            if(other.value != null) {
                return false;
            }
        } else if(!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if(value instanceof String) {
            return "\"" + value + "\"";
        }
        return value.toString();
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    public static class Null {
        public static final Null NULL = new Null();
        private Null() {

        }

        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj) {
                return true;
            }
            if(obj == null) {
                return false;
            }
            if(getClass() != obj.getClass()) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "null";
        }
    }
}
