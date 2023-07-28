package replete.text.stw;

import java.io.File;

import replete.io.FileUtil;
import replete.text.StringUtil;

public class SectionedTextWriter<T> {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static final String DEFAULT_NEWLINE = System.lineSeparator();

    // Core

    private SectionRenderer[] renderers;
    private String newLine = DEFAULT_NEWLINE;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SectionedTextWriter(SectionRenderer[] renderers) {
        this.renderers = renderers;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getNewLine() {
        return newLine;
    }

    // Mutators

    public SectionedTextWriter<T> setNewLine(String newLine) {
        this.newLine = newLine;
        return this;
    }


    ///////////
    // WRITE //
    ///////////

    public void writeAll(T data, File sectionDir, File allFile) {
        StringBuilder all = new StringBuilder();
        for(SectionRenderer<T> renderer : renderers) {
            String rendered = renderer.render(data);
            String wrapped = wrap(rendered, renderer.getTitle());

            System.out.print(wrapped);
            all.append(wrapped);

            File path = new File(sectionDir, renderer.getFileName());
            FileUtil.writeTextContent(path, wrapped.trim(), false, true);
        }
        FileUtil.writeTextContent(allFile, all.toString(), false, true);
    }

    private String wrap(String rendered, String title) {
        rendered = rendered.trim();
        String mid = "=== Section: " + title + " ===";
        String eq = StringUtil.replicateChar('=', mid.length());
        return
            eq + newLine +
            mid + newLine +
            eq + newLine +
            rendered + newLine + newLine + newLine
        ;
    }
}
