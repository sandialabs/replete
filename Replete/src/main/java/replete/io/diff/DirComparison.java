package replete.io.diff;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import replete.collections.Triple;
import replete.io.FileUtil;
import replete.text.RStringBuilder;
import replete.text.StringUtil;

public class DirComparison extends ContentComparison {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static final String PREFIX_PATH_LEFT_ONLY           = "[<-]";     // toString constants
    private static final String PREFIX_PATH_RIGHT_ONLY          = "[->]";
    private static final String PREFIX_PATHS_MATCH_CONTENT_DIFF = "[~~]";
    private static final String PREFIX_PATHS_MATCH_CONTENT_SAME = "[==]";

    // Core

    private List<Triple<File, File, PathComparison>> results = new ArrayList<>();


    ///////////////
    // ACCESSORS //
    ///////////////

    public List<Triple<File, File, PathComparison>> getResults() {
        return results;
    }

    // Computed

    public int getDiffCountLocal() {
        int diff = 0;
        for(Triple<File, File, PathComparison> result : results) {
            if(result.getValue2() == null) {
                diff++;
            } else if(result.getValue1() == null) {
                diff++;
            } else {
                PathComparison pComp2 = result.getValue3();
                if(pComp2.isDiff()) {
                    diff++;
                }
            }
        }
        return diff;
    }
    public int getDiffCountGlobal() {
        int diff = 0;
        for(Triple<File, File, PathComparison> result : results) {
            if(result.getValue2() == null) {
                diff++;
            } else if(result.getValue1() == null) {
                diff++;
            } else {
                PathComparison pComp2 = result.getValue3();
                if(pComp2.isDiffCase()) {
                    diff++;
                } else if(pComp2.getLeftType() != pComp2.getRightType()) {
                    diff++;
                } else if(pComp2.getContentComparison() != null) {
                    if(pComp2.getContentComparison() instanceof FileComparison) {
                        if(((FileComparison) pComp2.getContentComparison()).isDiff()) {
                            diff++;
                        }
                    } else {
                        diff += ((DirComparison) pComp2.getContentComparison()).getDiffCountGlobal();
                    }
                }
            }
        }
        return diff;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean isDiff() {
        for(Triple<File, File, PathComparison> result : results) {
            if(/*result.getValue1() == null || result.getValue2() == null ||*/ result.getValue3().isDiff()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringInner(0, new ComparisonRenderOptions());
    }
    public String toString(int level) {
        return toStringInner(level, new ComparisonRenderOptions());
    }
    public String toString(ComparisonRenderOptions options) {
        return toStringInner(0, options);
    }
    public String toString(int level, ComparisonRenderOptions options) {
        return toStringInner(level, options);
    }

    private String toStringInner(int level, ComparisonRenderOptions options) {
        RStringBuilder buffer = new RStringBuilder();
        toStringInner(buffer, this, level, options);
        return buffer.toString().trim();
    }
    private void toStringInner(RStringBuilder buffer, DirComparison dirDiff, int level, ComparisonRenderOptions options) {
        String sp0 = StringUtil.spaces(level * 4);
        String sp1 = StringUtil.spaces((level + 1) * 4);

        List<Triple<File, File, PathComparison>> results = new ArrayList<>(dirDiff.results);
        results.sort((r1, r2) -> {
            File path1 = r1.getValue1() == null ? r1.getValue2() : r1.getValue1();
            File path2 = r2.getValue1() == null ? r2.getValue2() : r2.getValue1();
            if(options.getSortType() == SortType.ALPHA_IC) {
                return path1.getName().compareToIgnoreCase(path2.getName());
            }
            return path1.getName().compareTo(path2.getName());
        });

        String l = StringUtil.cleanNull(options.getLeftLabel());
        String r = StringUtil.cleanNull(options.getRightLabel());
        int max = StringUtil.maxLength(Arrays.asList(l, r));

        boolean levelHasLabels = false;
        if(max > 0) {
            for(Triple<File, File, PathComparison> result : results) {
                if(result.getValue2() == null || result.getValue1() == null) {
                    levelHasLabels = true;
                    break;
                }
            }
        }

        for(Triple<File, File, PathComparison> result : results) {
            if(result.getValue2() == null) {

                String src = null;
                if(max > 0) {
                    if(l.isEmpty()) {
                        src = StringUtil.spaces(max + 1);
                    } else {
                        src = String.format("%-" + (max + 1) + "s", l + "|");
                    }
                }

                appendOnlyIn(buffer, sp0, PREFIX_PATH_LEFT_ONLY, src, result.getValue1());
            } else if(result.getValue1() == null) {

                String src = null;
                if(max > 0) {
                    if(r.isEmpty()) {
                        src = StringUtil.spaces(max + 1);
                    } else {
                        src = String.format("%-" + (max + 1) + "s", r + "|");
                    }
                }

                appendOnlyIn(buffer, sp0, PREFIX_PATH_RIGHT_ONLY, src, result.getValue2());
            } else {
                PathComparison pComp = result.getValue3();
                boolean pDiff = pComp.isDiff();
                if(options.isIncludeSame() || pDiff) {
                    buffer.append(sp0);
                    if(pDiff) {
                        buffer.append(PREFIX_PATHS_MATCH_CONTENT_DIFF);
                    } else {
                        buffer.append(PREFIX_PATHS_MATCH_CONTENT_SAME);
                    }

                    if(levelHasLabels) {
                        buffer.append(StringUtil.spaces(max + 1));
                    }

                    buffer.append(' ');
                    buffer.append(result.getValue1().getName());
                    if(result.getValue1().isDirectory()) {
                        buffer.append("/");
                    }

                    if(pComp.isDiffCase() || pComp.getLeftType() != pComp.getRightType()) {
                        buffer.append(" vs. " + result.getValue2().getName());
                        if(result.getValue2().isDirectory()) {
                            buffer.append("/");
                        }
                    }
                    buffer.append(":");

                    String pDiffStr = pComp.toString();
                    if(!pDiffStr.isEmpty()) {
                        buffer.append(' ');
                        buffer.append(pDiffStr);
                    }

                    if(pComp.getContentComparison() instanceof FileComparison) {
                        FileComparison fComp = (FileComparison) pComp.getContentComparison();
                        if(!fComp.isDiff()) {
                            if(options.isIncludeSame()) {
                                buffer.append(" SAME");
                            }
                        } else {
                            buffer.append(" ");
                            buffer.append(fComp.toString());
                        }
                        buffer.appendln();
                        if(fComp.isDiff()) {
                            // TODO: Someday add file magic to this
                            if(!result.getValue1().getName().endsWith(".jar") && !result.getValue2().getName().endsWith(".jar")) {
                                String s1 = FileUtil.getTextContent(result.getValue1(), true);
                                String s2 = FileUtil.getTextContent(result.getValue2(), true);
                                String sd = StringUtil.diff(s1, s2, 80);   // TODO: How can this return null for two files with diff MD5s?
                                if(sd == null) {
                                    sd = "(No Diff)";
                                }
                                buffer.appendln(StringUtil.indent(sd.trim(), sp1));
                            }
                        }

                    } else if(pComp.getContentComparison() instanceof DirComparison) {
                        DirComparison dComp = (DirComparison) pComp.getContentComparison();
                        if(!dComp.isDiff()) {
                            if(options.isIncludeSame()) {
                                buffer.append(" SAME");
                            }
                        } else {
                            buffer.append(" " + dComp.getDiffCountLocal() + " Differences (" + dComp.getDiffCountGlobal() + " Total)");
                        }
                        buffer.appendln();
                        toStringInner(buffer, dComp, level + 1, options);

                    // ContentDiff can be null if one was a file and one was a directory
                    } else {
                        buffer.appendln();
                    }
                }
            }
        }
//        buffer.appendln(sp0 + "--" + results.size() + " total--");
    }

    private static void appendOnlyIn(RStringBuilder buffer, String sp0, String prefix, String src, File file) {
        buffer.append(sp0);
        buffer.append(prefix);
        if(src != null) {
            buffer.append(src);
        }
        buffer.append(" ");
        buffer.append(file.getName());
        if(file.isDirectory()) {
            buffer.append("/");
        }
        buffer.appendln();
    }
}
