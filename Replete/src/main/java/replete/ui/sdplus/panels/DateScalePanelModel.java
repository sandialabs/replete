package replete.ui.sdplus.panels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A model that backs a date scale panel.  This model
 * contains all the values relevant to this scale, some
 * derived information from those values, the user's desired
 * filtering of those values, and the date format that should
 * be used to display and validate values.
 *
 * @author Derek Trumbo
 */

public class DateScalePanelModel extends LongScalePanelModel {

    ////////////
    // Fields //
    ////////////

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT =
        new SimpleDateFormat("yyyy/M/d");

    static {
        DEFAULT_DATE_FORMAT.setLenient(false);
    }

    // This field can be considered a UI setting or an
    // intrinsic characteristic of the scale.  This code
    // treats it as the latter instead of a UI setting.
    protected DateFormat dateFormat = DEFAULT_DATE_FORMAT;

    //////////////////
    // Constructors //
    //////////////////

    // Starts with no range subselected or nulls excluded.
    public DateScalePanelModel(String k, String nm, String un, String nt, List<Object> av) {
        super(k, nm, un, nt, av);
    }

    public DateScalePanelModel(String k, String nm, String un, String nt, List<Object> av, double lv, double hv, boolean nls) {
        super(k, nm, un, nt, av, lv, hv, nls);
    }

    //////////////////////////
    // Accessors / Mutators //
    //////////////////////////

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(DateFormat fmt) {
        dateFormat = fmt;
    }

    /////////////////////////////
    // Conversion & Validation //
    /////////////////////////////

    @Override
    protected String convertNumericToString(double val) {
        if(Double.isNaN(val)) {
            return "NaN";
        }

        Date date = new Date((long) val);
        return dateFormat.format(date);
    }

    @Override
    protected double convertStringToNumeric(String val) {
        try {
            return dateFormat.parse(val).getTime();
        } catch(ParseException e) {
            return Double.NaN;  // Should never happen as only called after validation.
        }
    }

    @Override
    protected boolean isValidString(String val) {
        try {

            // Additional regex check should be here because SimpleDateFormat
            // allows this:
            //   "1975/2/15aaa"
            //if(!val.matches("[0-9][0-9][0-9][0-9]/[0-9][0-9]?/[0-9][0-9]?")) {
            //    return false;
            //}
            // Commented because the above wouldn't work for all patterns
            // obviously.  Actually, you could actually use the return
            // value of SimpleDateFormat.toPattern to derive the pattern
            // above to restrict the string syntactically.

            dateFormat.parse(val);

            return true;
        } catch(ParseException e) {
            return false;
        }
    }

    //////////////
    // toString //
    //////////////

    @Override
    protected String internalString() {
        return super.internalString() +
            ", dateFormat=" + (dateFormat instanceof SimpleDateFormat ?
                ((SimpleDateFormat) dateFormat).toPattern() : dateFormat.toString());
    }
}
