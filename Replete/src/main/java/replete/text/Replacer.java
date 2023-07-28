package replete.text;


public interface Replacer {
    String replace(String match, String[] captureGroups);
}
