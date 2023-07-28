package replete.ui.sp;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;

public class RulerModel {

    // NOTE: This class could also have options for toggling the
    // visibility of each category (as opposed to clearing them
    // from the model).

    // NOTE: This class could also have options to specify the Z-order
    // for the icons with respect to other icons to make sure smaller
    // icons are on top of larger icons so both can be seen at least
    // somewhat.

    // NOTE: This class could also contain a mapping of line number
    // range to RangeHighlightDescriptor so that even the text of the line numbers can
    // have a custom RangeHighlightDescriptor.

    // NOTE: Instead of using IconDescriptor & RangeHighlightDescriptor for terminal maps
    // of the below data structures, you could invent additional
    // objects to carry along additional information like tool tip,
    // etc.


    ////////////
    // FIELDS //
    ////////////

    Map<String, Map<LineNumberRange, IconDescriptor>> icons =
        new HashMap<String, Map<LineNumberRange, IconDescriptor>>();
    Map<String, Map<LineNumberRange, RangeHighlightDescriptor>> rangeHighlights =
        new HashMap<String, Map<LineNumberRange, RangeHighlightDescriptor>>();


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier changeNotifier = new ChangeNotifier(this);
    public void addChangeListener(ChangeListener listener) {
        changeNotifier.addListener(listener);
    }
    private void fireChangeNotifier() {
        changeNotifier.fireStateChanged();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Map<String, Map<LineNumberRange, IconDescriptor>> getIcons() {
        return icons;
    }
    public Map<String, Map<LineNumberRange, RangeHighlightDescriptor>> getRangeHighlights() {
        return rangeHighlights;
    }

    // Mutators

    // Add a given icon for a given category for a line or for a range of lines.
    // NOTE: This currently adds a range without regard to overlapping ranges.
    // Ranges are not merged for identical categories or icons.  This behavior
    // could conceivably be improved.
    public IconDescriptor addIcon(String category, int line, ImageIcon icon) {
        return addIcon(category, new LineNumberRange(line), new IconDescriptor(icon));
    }
    public IconDescriptor addIcon(String category, LineNumberRange lines, ImageIcon icon) {
        return addIcon(category, lines, new IconDescriptor(icon));
    }
    public IconDescriptor addIcon(String category, int line, IconDescriptor iconDescriptor) {
        return addIcon(category, new LineNumberRange(line), iconDescriptor);
    }
    public IconDescriptor addIcon(String category, LineNumberRange lines, IconDescriptor iconDescriptor) {
        Map<LineNumberRange, IconDescriptor> catIcons = icons.get(category);
        if(catIcons == null) {
            catIcons = new TreeMap<LineNumberRange, IconDescriptor>();
            icons.put(category, catIcons);
        }
        iconDescriptor.setCategory(category);
        iconDescriptor.setLines(lines);
        catIcons.put(lines, iconDescriptor);
        fireChangeNotifier();
        return iconDescriptor;
    }

    // Clear all the icons for a given category for a line or within a range of lines.
    // NOTE: This currently only clears a range if that *exact* range exists
    // in the model.  Theoretically, better semantics of this method would
    // involve carving up any overlapping ranges that exist in the model.
    public void clearIcon(String category, int line) {
        clearIcon(category, new LineNumberRange(line));
    }
    public void clearIcon(String category, LineNumberRange lines) {
        Map<LineNumberRange, IconDescriptor> catIcons = icons.get(category);
        if(catIcons != null) {
            if(catIcons.remove(lines) != null) {
                fireChangeNotifier();
            }
        }
    }

    // Clear all icons for a given category.
    public void clearIcons(String category) {
        if(icons.remove(category) != null) {
            fireChangeNotifier();
        }
    }

    // Clear all icons.
    public void clearIcons() {
        int preSize = icons.size();
        icons.clear();
        if(preSize != 0) {
            fireChangeNotifier();
        }
    }

    // Add a given range highlight for a given category for a line or for a range of lines.
    // NOTE: This currently adds a range without regard to overlapping ranges.
    // Ranges are not merged for identical categories or icons.  This behavior
    // could conceivably be improved.
    public RangeHighlightDescriptor addRangeHighlight(String category, int line, Color color) {
        return addRangeHighlight(category, new LineNumberRange(line), new RangeHighlightDescriptor(color));
    }
    public RangeHighlightDescriptor addRangeHighlight(String category, LineNumberRange lines, Color color) {
        return addRangeHighlight(category, lines, new RangeHighlightDescriptor(color));
    }
    public RangeHighlightDescriptor addRangeHighlight(String category, int line, RangeHighlightDescriptor RangeHighlightDescriptor) {
        return addRangeHighlight(category, new LineNumberRange(line), RangeHighlightDescriptor);
    }
    public RangeHighlightDescriptor addRangeHighlight(String category, LineNumberRange lines, RangeHighlightDescriptor rhDescriptor) {
        Map<LineNumberRange, RangeHighlightDescriptor> catRangeHighlights = rangeHighlights.get(category);
        if(catRangeHighlights == null) {
            catRangeHighlights = new TreeMap<LineNumberRange, RangeHighlightDescriptor>();
            rangeHighlights.put(category, catRangeHighlights);
        }
        rhDescriptor.setCategory(category);
        rhDescriptor.setLines(lines);
        catRangeHighlights.put(lines, rhDescriptor);
        fireChangeNotifier();
        return rhDescriptor;
    }

    // Clear all the icons for a given category for a line or within a range of lines.
    // NOTE: This currently only clears a range if that *exact* range exists
    // in the model.  Theoretically, better semantics of this method would
    // involve carving up any overlapping ranges that exist in the model.
    public void clearRangeHighlight(String category, int line) {
        clearRangeHighlight(category, new LineNumberRange(line));
    }
    public void clearRangeHighlight(String category, LineNumberRange lines) {
        Map<LineNumberRange, RangeHighlightDescriptor> catRangeHighlights = rangeHighlights.get(category);
        if(catRangeHighlights != null) {
            if(catRangeHighlights.remove(lines) != null) {
                fireChangeNotifier();
            }
        }
    }

    // Clear all range highlights for a given category.
    public void clearRangeHighlights(String category) {
        if(rangeHighlights.remove(category) != null) {
            fireChangeNotifier();
        }
    }

    // Clear all range highlights.
    public void clearRangeHighlights() {
        int preSize = rangeHighlights.size();
        rangeHighlights.clear();
        if(preSize != 0) {
            fireChangeNotifier();
        }
    }
}
