package finio.examples.rules.model;

import java.util.regex.Pattern;

/*
import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;
*/
import replete.numbers.PositiveNumberRangeList;
import replete.text.StringUtil;

public class Rule {


    ////////////
    // FIELDS //
    ////////////

    private boolean exclusion;
    private String fontName;
    private Float fontSize;
    private Float minFontSize;
    private Float maxFontSize;
    private Boolean bold;
    private Boolean italic;
    private PositiveNumberRangeList pages;
    private Float minX;
    private Float maxX;
    private Float minY;
    private Float maxY;
    private Float maxWidth;
    private Float maxHeight;
    private String pattern;
    private CaptureAs captureAs = CaptureAs.NOTHING;
    private Boolean optional = false;
    private Boolean noTrim = false;
    private Boolean startOnNewLine = false;
    private Boolean disabled = false;

    //private dk.brics.automaton.Automaton dfa = null;

    private Pattern p;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isExclusion() {
        return exclusion;
    }
    public String getFontName() {
        return fontName;
    }
    public Float getMinFontSize() {
        return minFontSize;
    }
    public Float getMaxFontSize() {
        return maxFontSize;
    }
    public Boolean getBold() {
        return bold;
    }
    public Boolean getItalic() {
        return italic;
    }
    public PositiveNumberRangeList getPages() {
        return pages;
    }
    public Float getMinX() {
        return minX;
    }
    public Float getMaxX() {
        return maxX;
    }
    public Float getMinY() {
        return minY;
    }
    public Float getMaxY() {
        return maxY;
    }
    public Float getMaxWidth() {
        return maxWidth;
    }
    public Float getMaxHeight() {
        return maxHeight;
    }
    public String getPattern() {
        return pattern;
    }
    public CaptureAs getCaptureAs() {
        return captureAs;
    }
    public boolean isOptional() {
        return optional != null && optional;
    }
    public boolean isNoTrim() {
        return noTrim != null && noTrim;
    }
    public boolean isStartOnNewLine() {
        return startOnNewLine != null && startOnNewLine;
    }
    public boolean isDisabled() {
        return disabled != null && disabled;
    }

    // Muators (Builder)

    public Rule setExclusion(boolean exclusion) {
        this.exclusion = exclusion;
        return this;
    }
    public Rule setFontName(String fontName) {
        this.fontName = fontName;
        return this;
    }
    public Rule setMinFontSize(Float fontSize) {
        minFontSize = fontSize;
        return this;
    }
    public Rule setMaxFontSize(Float fontSize) {
        maxFontSize = fontSize;
        return this;
    }
    public Rule setBold(Boolean bold) {
        this.bold = bold;
        return this;
    }
    public Rule setItalic(Boolean italic) {
        this.italic = italic;
        return this;
    }
    public Rule setPages(PositiveNumberRangeList pages) {
        this.pages = pages;
        return this;
    }
    public Rule setMinX(Float minX) {
        this.minX = minX;
        return this;
    }
    public Rule setMaxX(Float maxX) {
        this.maxX = maxX;
        return this;
    }
    public Rule setMinY(Float minY) {
        this.minY = minY;
        return this;
    }
    public Rule setMaxY(Float maxY) {
        this.maxY = maxY;
        return this;
    }
    public Rule setMaxWidth(Float maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }
    public Rule setMaxHeight(Float maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }
    public Rule setCaptureAs(CaptureAs captureAs) {
        this.captureAs = captureAs;
        return this;
    }
    public Rule setOptional(Boolean optional) {
        this.optional = optional;
        return this;
    }
    public Rule setNoTrim(Boolean noTrim) {
        this.noTrim = noTrim;
        return this;
    }
    public Rule setStartOnNewLine(Boolean startOnNewLine) {
        this.startOnNewLine = startOnNewLine;
        return this;
    }
    public Rule setDisabled(Boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public Rule setPattern(String pattern) {
        this.pattern = pattern;
        p = Pattern.compile("^(" + pattern + ").*");
        /*
        try {
            dfa = new RegExp(pattern, RegExp.ALL).toAutomaton();
        } catch (Exception e) {
            dfa = null;
        }
        */
        return this;
    }

    ///////////
    // MATCH //
    ///////////
    /*
    public RuleMatchResult match(final List<PdfElement> stream) {
        //return slowMatch(stream);
        //if (dfa == null) {
        return slowMatch(stream);
        //} else {
            //            return fastMatch(stream);
        //}
    }
    */
    /*
    private RuleMatchResult fastMatch(List<PdfElement> stream) {
        boolean matches = true;
        boolean matchedLast = false;
        int startPage = 0;

        int longestAcceptIndex = -1;
        int longestAcceptOffset = -1;
        int totalBytes = 0;
        int oldBytes = 0;

        dk.brics.automaton.RunAutomaton dfaRunner = new RunAutomaton(dfa);
        int currState = dfaRunner.getInitialState();
        if (currState == -1) {
            return new RuleMatchResult(false, "", startPage);
        }
        toEnd:
        for (int i = 0; i < stream.size(); i++) {
            PdfElement current = stream.get(i);
            if(startPage == 0) {
                startPage = current.getPage();
            }
            if(minFontSize != null) {
                matches = matches && current.getFontSize() >= minFontSize;
            }
            if(maxFontSize != null) {
                matches = matches && current.getFontSize() <= maxFontSize;
            }
            if(bold != null && matches) {
                matches = matches && current.isBold() == bold;
            }
            if(italic != null && matches) {
                matches = matches && current.isItalic() == italic;
            }
            if(pages != null && matches) {
                matches = matches && pages.contains(current.getPage());
            }
            if(minX != null && !matchedLast && matches) {
                matches = matches && current.getX() >= minX;
            }
            if(maxX != null && !matchedLast && matches) {
                matches = matches && current.getX() <= maxX;
            }
            if(minY != null && !matchedLast && matches) {
                matches = matches && current.getY() >= minY;
            }
            if(maxY != null && !matchedLast && matches) {
                matches = matches && current.getY() <= maxY;
            }
            if(fontName != null && matches) {
                matches = matches && current.getFontName().equals(fontName);
            }

            if(matches) {
                String txt = current.getText();
                for (int j = 0; j < txt.length(); j++) {
                    totalBytes += 1;
                    currState = dfaRunner.step(currState, txt.charAt(j));
                    if (currState == -1) {
                        break toEnd;
                    } else if (dfaRunner.isAccept(currState)) {
                        //We want to get the longest chunk of text that our
                        //regex will accept.
                        longestAcceptIndex = i;
                        longestAcceptOffset = j;
                        oldBytes = totalBytes;
                    }
                }
            } else {
                break;
            }
            matchedLast = true;
        }
        if (longestAcceptIndex >= 0) {
            StringBuilder b = new StringBuilder();
            //Probably a stupid micro optimization, but it is 2 oclock on a friday.
            b.ensureCapacity(oldBytes);
            for (int i = 0; i < longestAcceptIndex; i++) {
                b.append(stream.get(i).getText());
            }
            for (int i = 0; i < longestAcceptOffset + 1; i++) {
                b.append(stream.get(longestAcceptIndex).getText().charAt(i));
            }
            return new RuleMatchResult(true, b.toString(), startPage);
        }
        return new RuleMatchResult(false, "", startPage);
    }
    */

    // Doesn't reference 'exclusion' or 'disabled' itself
//    public RuleMatchResult match(List<PdfElement> stream, int start) {
//        boolean matches = true;
//        boolean matchedLast = false;
//        StringBuilder matchedStringBuilder = new StringBuilder();
//        int startPage = 0;
//        int i = start;
//
//        Float firstY = null;
//        Float firstX = null;
//        if (stream.size() - start > 0) {
//            firstY = stream.get(start).getY();
//            firstX = stream.get(start).getX();
//        }
//        if(isStartOnNewLine() && start != 0 && stream.get(start).getY() == stream.get(start - 1).getY()) {
//            return new RuleMatchResult(false, "", startPage, -1, -1);
//        }
//        while(i < stream.size()) {
//            PdfElement current = stream.get(i);
//            if(startPage == 0) {
//                startPage = current.getPage();
//            }
//            if(minFontSize != null) {
//                matches = matches && current.getFontSize() >= minFontSize;
//            }
//            if(maxFontSize != null) {
//                matches = matches && current.getFontSize() <= maxFontSize;
//            }
//            if(bold != null && matches) {
//                matches = matches && current.isBold() == bold;
//            }
//            if(italic != null && matches) {
//                matches = matches && current.isItalic() == italic;
//            }
//            if(pages != null && matches) {
//                matches = matches && pages.contains(current.getPage());
//            }
//            if(minX != null && !matchedLast && matches) {
//                matches = matches && current.getX() >= minX;
//            }
//            if(maxX != null && !matchedLast && matches) {
//                matches = matches && current.getX() <= maxX;
//            }
//            if(minY != null && !matchedLast && matches) {
//                matches = matches && current.getY() >= minY;
//            }
//            if(maxY != null && !matchedLast && matches) {
//                matches = matches && current.getY() <= maxY;
//            }
//            if(fontName != null && matches) {
//                matches = matches && current.getFontName().equals(fontName);
//            }
//            if (maxHeight != null) {
//                matches = matches && maxHeight + firstY >= current.getY();
//            }
//            if (maxWidth != null) {
//                matches = matches && maxWidth + firstX >= current.getX();
//            }
//            if(matches) {
//                matchedStringBuilder.append(current.getText());
//            } else if(matchedLast) {
//                Matcher matcher = p.matcher(matchedStringBuilder);
//                if(matcher.matches()) {
//                    return new RuleMatchResult(true, matcher.group(1), startPage, start, i);
//                } else {
//                    return new RuleMatchResult(false, "", startPage, -1, -1);
//                }
//            } else {
//                return new RuleMatchResult(false, "", startPage, -1, -1);
//            }
//            matchedLast = matches;
//            i++;
//        }
//        if(matchedLast) {
//            Matcher matcher = p.matcher(matchedStringBuilder);
//            if(matcher.matches()) {
//                return new RuleMatchResult(true, matcher.group(1), startPage, start, i - 1);
//            }
//        }
//        return new RuleMatchResult(false, "", startPage, -1, -1);
//    }


    //////////
    // MISC //
    //////////

    public String toSimpleString() {
        String SEP = ", ";
        String ret = "";

        if(isDisabled()) {
            ret += "[DIS]";
        }
        if(exclusion) {
            ret += "[EXCL]";
        }
        if(fontName != null) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += StringUtil.max(fontName, 7);
        }
        if(minFontSize != null) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += "MnFS" + minFontSize;
        }
        if(maxFontSize != null) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += "MxFS" + maxFontSize;
        }
        if(bold != null) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += (bold) ? "B" : "~B";
        }
        if(italic != null) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += (italic) ? "I" : "~I";
        }
        if(pages != null) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += "Pg" + pages;
        }
        if(minX != null) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += "MnX" + minX;
        }
        if(maxX != null) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += "MxX" + maxX;
        }
        if(minY != null) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += "MnY" + minY;
        }
        if(maxY != null) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += "MxY" + maxY;
        }
        if(maxWidth != null) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += "MxW" + maxWidth;
        }
        if(maxHeight != null) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += "MxH" + maxHeight;
        }
        if(pattern != null) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += pattern;
        }
        if(captureAs != null && !captureAs.equals(CaptureAs.NOTHING)) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += "Cap" + captureAs;
        }
        if(isOptional()) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += "{OPT}";
        }
        if(isNoTrim()) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += "{NOTR}";
        }
        if(isStartOnNewLine()) {
            if(!ret.isEmpty()) {
                ret += SEP;
            }
            ret += "{STNL}";
        }
        if(ret.isEmpty()) {
            ret = "<blank>";
        }
        return ret;
    }

    public Rule copy() {
        Rule copy = new Rule()
            .setExclusion(exclusion)
            .setFontName(fontName)
            .setMinFontSize(minFontSize)
            .setMaxFontSize(maxFontSize)
            .setBold(bold)
            .setItalic(italic)
            .setPages(pages)
            .setMinX(minX)
            .setMaxX(maxX)
            .setMinY(minY)
            .setMaxY(maxY)
            .setMaxWidth(maxWidth)
            .setMaxHeight(maxHeight)
            .setPattern(pattern)
            .setCaptureAs(captureAs)
            .setOptional(optional)
            .setNoTrim(noTrim)
            .setStartOnNewLine(startOnNewLine)
            .setDisabled(disabled);

        return copy;
    }

    private Object readResolve() {
        if(captureAs == null) {
            captureAs = CaptureAs.NOTHING;
        }
        if(optional == null) {
            optional = false;
        }
        if(noTrim == null) {
            noTrim = false;
        }
        if(startOnNewLine == null) {
            startOnNewLine = false;
        }
        if(disabled == null) {
            disabled = false;
        }
        if(fontSize != null) {
            minFontSize = maxFontSize = fontSize;
            fontSize = null;
        }
        setPattern(pattern);
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "Rule{" + toSimpleString() + "}";
//        return "Rule [fontName=" + fontName + ", fontSize=" + fontSize + ", bold=" + bold +
//                        ", italics=" + italic + ", page=" + page + ", minX=" + minX + ", maxX=" + maxX +
//                        ", minWidth=" + minWidth + ", maxWidth=" + maxWidth + ", pattern=" + pattern + "]"; //needs captureAs too
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (new Boolean(exclusion).hashCode());
        result = prime * result + ((bold == null) ? 0 : bold.hashCode());
        result = prime * result + ((fontName == null) ? 0 : fontName.hashCode());
        result = prime * result + ((minFontSize == null) ? 0 : minFontSize.hashCode());
        result = prime * result + ((maxFontSize == null) ? 0 : maxFontSize.hashCode());
        result = prime * result + ((italic == null) ? 0 : italic.hashCode());
        result = prime * result + ((maxHeight == null) ? 0 : maxHeight.hashCode());
        result = prime * result + ((minX == null) ? 0 : minX.hashCode());
        result = prime * result + ((maxX == null) ? 0 : maxX.hashCode());
        result = prime * result + ((minY == null) ? 0 : minY.hashCode());
        result = prime * result + ((maxY == null) ? 0 : maxY.hashCode());
        result = prime * result + ((maxWidth == null) ? 0 : maxWidth.hashCode());
        result = prime * result + ((pages == null) ? 0 : pages.hashCode());
        result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
        //result = prime * result + ((dfa == null) ? 0 : dfa.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Rule other = (Rule) obj;
        if(exclusion != other.exclusion) {
            return false;
        }
        /*
        if ((other.dfa == null && this.dfa != null) || (other.dfa != null && this.dfa == null) ) {
            return false;
        }
        if (other.dfa != null && !other.dfa.equals(dfa)) {
            return false;
        }
        */
        if (bold == null) {
            if (other.bold != null) {
                return false;
            }
        } else if (!bold.equals(other.bold)) {
            return false;
        }
        if (fontName == null) {
            if (other.fontName != null) {
                return false;
            }
        } else if (!fontName.equals(other.fontName)) {
            return false;
        }
        if (minFontSize == null) {
            if (other.minFontSize != null) {
                return false;
            }
        } else if (!minFontSize.equals(other.minFontSize)) {
            return false;
        }
        if (maxFontSize == null) {
            if (other.maxFontSize != null) {
                return false;
            }
        } else if (!maxFontSize.equals(other.maxFontSize)) {
            return false;
        }
        if (italic == null) {
            if (other.italic != null) {
                return false;
            }
        } else if (!italic.equals(other.italic)) {
            return false;
        }
        if (maxHeight == null) {
            if (other.maxHeight != null) {
                return false;
            }
        } else if (!maxHeight.equals(other.maxHeight)) {
            return false;
        }
        if (minX == null) {
            if (other.minX != null) {
                return false;
            }
        } else if (!minX.equals(other.minX)) {
            return false;
        }
        if (maxX == null) {
            if (other.maxX != null) {
                return false;
            }
        } else if (!maxX.equals(other.maxX)) {
            return false;
        }
        if (minY == null) {
            if (other.minY != null) {
                return false;
            }
        } else if (!minY.equals(other.minY)) {
            return false;
        }
        if (maxY == null) {
            if (other.maxY != null) {
                return false;
            }
        } else if (!maxY.equals(other.maxY)) {
            return false;
        }
        if (maxWidth == null) {
            if (other.maxWidth != null) {
                return false;
            }
        } else if (!maxWidth.equals(other.maxWidth)) {
            return false;
        }
        if (pages == null) {
            if (other.pages != null) {
                return false;
            }
        } else if (!pages.equals(other.pages)) {
            return false;
        }
        if (pattern == null) {
            if (other.pattern != null) {
                return false;
            }
        } else if (!pattern.equals(other.pattern)) {
            return false;
        }
        return true;
    }


    //////////
    // TEST //
    //////////

//    public static void main(String[] args) {
//        List<PdfElement> stream = new ArrayList<>();
//        stream.add(new PdfElement("a", "Times", 18.0f, true, true, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement(".", "Times", 18.0f, true, true, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement(" ", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("h", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("e", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("l", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("l", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("o", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement(" ", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("d", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("o", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("g", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//        stream.add(new PdfElement("!", "Times", 12.0f, false, false, 10.0f, 10.0f, 10.0F, 10.0F, 1));
//
//        Rule rule = new Rule()
//            .setBold(true)
//            .setItalic(true)
//            .setMinFontSize(18.0F)
//            .setMaxFontSize(18.0F)
//            .setPattern("a\\.")
//        ;
//
//    }
}
