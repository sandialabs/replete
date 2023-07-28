package replete.ui.sdplus.subsel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import replete.ui.sdplus.MatchType;
import replete.ui.sdplus.ScaleSetPanelModel;
import replete.ui.sdplus.panels.ScalePanelModel;


/**
 * Provides an optimized way to decide which data elements
 * from the scale set panel model have been "selected" by
 * the user given their selection in the scale panels'
 * filter controls.  It does this by maintaining a boolean
 * table of which data values from the model have most
 * recently passed or failed the user's filter criteria.
 * Then, each time the user changes a given scale panel's
 * filter criteria, only that scale's data values are
 * revalidated for acceptance.  The subselector operates
 * on a subset of scales from the model depending on the
 * provided context.  Only those scales that are logically
 * related because they are backed by the same data source
 * should be operated by by a given subselector.
 *
 * @author Derek Trumbo
 */

public class Subselector {

    ////////////
    // Fields //
    ////////////

    protected ScaleSetPanelModel model;
    protected SubselectionContext context;

    // Which keys are being operated on by the subselector.
    protected String[] contextKeys;
    protected int contextKeyCount;

    // Number of data elements for the context.  All scales
    // must have the same number of data elements in them.
    protected int elementCount;

    // Acceptance table and total accepted counts.
    protected boolean[][] accepted;
    protected int[] numScalesAccepted;

    protected boolean acceptedTableValid = false;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public Subselector(ScaleSetPanelModel m, SubselectionContext ctx) {
        model = m;
        context = ctx;
        reinitialize();
    }

    ////////////////
    // Acceptance //
    ////////////////

    public void reinitialize() {
        List<String> contextKeysList = new ArrayList<String>();
        String[] allKeys = model.getScaleKeys();
        for(String key : allKeys) {
            if(context.existsInContext(key)) {
                contextKeysList.add(key);
            }
        }
        contextKeys = contextKeysList.toArray(new String[0]);
        contextKeyCount = contextKeys.length;

        elementCount = getContextElementCount();

        accepted = new boolean[elementCount][contextKeyCount];
        numScalesAccepted = new int[elementCount];

        invalidate();
    }

    // Signals that the entire table needs to be rebuilt instead
    // of just being able to revalidate a single column.
    public void invalidate() {
        acceptedTableValid = false;
    }

    protected int getContextElementCount() {
        int elemCount = -1;
        for(String key : contextKeys) {
            if(elemCount == -1) {
                elemCount = model.getDataElementCount(key);
            } else if(elemCount != model.getDataElementCount(key)) {
                throw new RuntimeException("All scales in the subselection context must be backed " +
                    "by the same number of data elements. Scale '" + key + "' is backed by " +
                    model.getDataElementCount(key) + " data elements instead of " + elemCount + ".");
            }
        }
        return elemCount;
    }

    // Could be changed to return List<Integer> if desired.
    public int[] findSubselectedElements(MatchType matchType, String keyScaleChanged) {

        // If the accepted table isn't valid, or a single key was
        // not provided, build the entire acceptance table by
        // validating every value in the model.
        if(!acceptedTableValid || keyScaleChanged == null) {
            buildAcceptedTable();
            acceptedTableValid = true;

        } else {

            // If the accepted table has already been constructed
            // and there is a valid scale key given which represents
            // a single scale that was edited to cause this 'find',
            // attempt a quicker table update.
            updateAcceptedTable(keyScaleChanged);
        }

        // Now that the acceptance table has been built, decide
        // which data elements pass by counting the number of
        // values that were accepted.
        List<Integer> elemList = new ArrayList<Integer>();
        for(int row = 0; row < accepted.length; row++) {
            if(matchType == MatchType.INTERSECTION) {
                if(numScalesAccepted[row] == contextKeyCount) {
                    elemList.add(row);
                }
            } else {
                if(numScalesAccepted[row] != 0) {
                    elemList.add(row);
                }
            }
        }

        return toPrimitive(elemList.toArray(new Integer[0]));
    }

    protected void buildAcceptedTable() {
        for(int element = 0; element < elementCount; element++) {
            int numScalesAccp = 0;
            for(int k = 0; k < contextKeyCount; k++) {
                String key = contextKeys[k];
                ScalePanelModel spm = model.getScalePanelModel(key);
                Object value = model.getValue(key, element);
                boolean accp = spm.isAcceptedValue(value);
                accepted[element][k] = accp;
                if(accp) {
                    numScalesAccp++;
                }
            }
            numScalesAccepted[element] = numScalesAccp;
        }
    }

    protected void updateAcceptedTable(String keyScaleChanged) {

        // Find the index of the key in the context.  This will be
        // the index needed to index into the accepted table.
        int k;
        for(k = 0; k < contextKeyCount; k++) {
            if(contextKeys[k].equals(keyScaleChanged)) {
                break;
            }
        }

        if(k == contextKeyCount) {
            throw new RuntimeException("Changed key not in context.");
        }

        ScalePanelModel spm = model.getScalePanelModel(keyScaleChanged);
        for(int element = 0; element < elementCount; element++) {
            Object value = model.getValue(keyScaleChanged, element);
            boolean newAccp = spm.isAcceptedValue(value);
            boolean oldAccp = accepted[element][k];
            if(newAccp != oldAccp) {
                accepted[element][k] = newAccp;
                if(newAccp) {
                    numScalesAccepted[element]++;
                } else {
                    numScalesAccepted[element]--;
                }
            }
        }
    }

    protected int[] toPrimitive(Integer[] array) {
        if(array == null) {
            return null;
        }
        int[] result = new int[array.length];
        for(int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    //////////////
    // toString //
    //////////////

    // For debugging
    public String toString(MatchType matchType) {
        String res = "Subselector Information\n";
        res += " - Context Keys (" + contextKeyCount + "):\n";
        res += "    " + Arrays.toString(contextKeys) + "\n";
        res += " - Acceptance Table:\n";
        for(int r = 0; r < accepted.length; r++) {
            res += "    Elem #" + (r + 1) + ": ";
            for(int c = 0; c < accepted[r].length; c++) {
                res += accepted[r][c] ? "1 " : "0 ";
            }
            res += "= " + numScalesAccepted[r] + " total";
            if(matchType == MatchType.INTERSECTION) {
                if(numScalesAccepted[r] == contextKeyCount) {
                    res += " [accepted (selected)]";
                }
            } else {
                if(numScalesAccepted[r] != 0) {
                    res += " [accepted (selected)]";
                }
            }

            res += "\n";
        }
        res = res.substring(0, res.length() - 1);
        return res;
    }
}
