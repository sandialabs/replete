package replete.io.fast;

import java.io.Serializable;

import org.junit.Ignore;

@Ignore
public class PersonSerialized implements Serializable {
    private String name;
    public PersonSerialized() {}
    public PersonSerialized(String name) {
        this.name = name;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        PersonSerialized other = (PersonSerialized) obj;
        if(name == null) {
            if(other.name != null) {
                return false;
            }
        } else if(!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    private Object readResolve() {
        return new Person(name);
    }
}
