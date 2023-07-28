package replete.io;

public interface Destination {
    void begin() throws Exception;
    void end() throws Exception;
    void accept(String line) throws Exception;
}

