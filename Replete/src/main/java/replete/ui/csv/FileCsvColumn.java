package replete.ui.csv;

import replete.collections.Pair;
import replete.text.StringUtil;
import replete.text.patterns.PatternInterpretation;
import replete.text.patterns.PatternInterpretationType;
import replete.text.patterns.PatternUtil;

public class FileCsvColumn extends AbstractCommonCsvColumn {

    ////////////
    // FIELDS //
    ////////////

    // TODO - could do a better job on this, particularly if we could figure out more about which cols
    // represent what kind of info.
    public static final PatternInterpretation DEFAULT_PATTERN_INTERPRETATION = new PatternInterpretation()
        .setType(PatternInterpretationType.REGEX)
        .setCaseSensitive(false)
        .setWholeMatch(false)
    ;

    private int fileIndex;  // index of this column in file
    private PatternInterpretation interp;
    String patternRegEx="";

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FileCsvColumn(CsvColumnType type, CsvColumnInfo info) {
        super(type, info);
        fileIndex = ((FileCsvColumnInfo)info).getIndex();
    }

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public boolean validValue(String value) {
        if (patternRegEx=="") {
            return true;
        }
        // saved pattern specifies EXCLUDED values
        return !StringUtil.matches(value, patternRegEx, interp);
    }

    public void setExcludedValues(String patternWithTag) {
        Pair<String, PatternInterpretation> result =
            PatternUtil.parsePatternInterpretationTag(patternWithTag, DEFAULT_PATTERN_INTERPRETATION);
        String patternAny = result.getValue1();
        interp = result.getValue2();
        patternRegEx = PatternUtil.convertToRegex(patternAny, interp);
    }

    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getCellData(Object source) {
        // source should be a list representing a row of data from csv file
        String[] row = (String[]) source;
        if (row.length <= fileIndex) {
            return "";
        }
        return row[fileIndex];
    }

    @Override
    public String toString() {
        return info.getName();
    }
}
