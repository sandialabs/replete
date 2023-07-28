package replete.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import replete.errors.RuntimeConvertedException;

public class StringFilterer {


    ////////////
    // FIELDS //
    ////////////

    private boolean inverse = false;
    private Pattern pattern;
    boolean matchWholeLine = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StringFilterer(String pattern) {
        this(Pattern.compile(pattern));
    }
    public StringFilterer(Pattern pattern) {
        this.pattern = pattern;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isInverse() {
        return inverse;
    }
    public boolean isMatchWholeLine() {
        return matchWholeLine;
    }

    // Mutators

    public StringFilterer setInverse(boolean inverse) {
        this.inverse = inverse;
        return this;
    }
    public StringFilterer setMatchWholeLine(boolean matchWholeLine) {
        this.matchWholeLine = matchWholeLine;
        return this;
    }


    ////////////
    // FILTER //
    ////////////

    public String filter(File file) {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringBuilder buffer = new StringBuilder();
            while((line = reader.readLine()) != null) {
                checkAppendLine(line, buffer);
            }
            return buffer.toString();
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }
    public String filter(String s) {
        try {
            BufferedReader reader = new BufferedReader(new StringReader(s));
            String line;
            StringBuilder buffer = new StringBuilder();
            while((line = reader.readLine()) != null) {
                checkAppendLine(line, buffer);
            }
            return buffer.toString();
        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }
    public String[] filter(String[] lines) {
        List<String> buffer = new ArrayList<>();
        for(String line : lines) {
            checkAppendLine(line, buffer);
        }
        return buffer.toArray(new String[0]);
    }

    private void checkAppendLine(String line, StringBuilder buffer) {
        boolean applies = checkLineApplies(line);
        if(checkLineAdd(applies)) {
            buffer.append(line);
            buffer.append('\n');
        }
    }
    private void checkAppendLine(String line, List<String> buffer) {
        boolean applies = checkLineApplies(line);
        if(checkLineAdd(applies)) {
            buffer.add(line);
        }
    }

    private boolean checkLineApplies(String line) {
        Matcher matcher = pattern.matcher(line);
        boolean applies = matchWholeLine ? matcher.matches() : matcher.find();
        return applies;
    }
    private boolean checkLineAdd(boolean applies) {
        return inverse && !applies || !inverse && applies;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        String[] lines = new String[] {
            "derek",
            "trumbo",
            "trumbotrumbo"
        };
        StringFilterer filterer = new StringFilterer("trumbo")
            .setMatchWholeLine(true)
            .setInverse(true)
        ;
        String[] result = filterer.filter(lines);
        System.out.println(Arrays.toString(result));

        String lines2 = "derek\ntrumbo\ntrumbotrumbo";
        StringFilterer filterer2 = new StringFilterer("trumb")
            .setMatchWholeLine(true)
            .setInverse(false)
        ;
        String result2 = filterer2.filter(lines2);
        System.out.println(result2);
}
}
