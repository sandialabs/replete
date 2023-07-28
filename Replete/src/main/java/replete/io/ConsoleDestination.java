package replete.io;

public class ConsoleDestination implements Destination {
    public void begin() throws Exception {}
    public void end() throws Exception {}
    @Override
    public void accept(String line) throws Exception {
        System.out.println(line);
    }
}
