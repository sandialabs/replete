package replete.cli.bash;

import static replete.cli.bash.BashCommandLineParser.BKSL;
import static replete.cli.bash.BashCommandLineParser.DBLQ;
import static replete.cli.bash.BashCommandLineParser.SNGQ;
import static replete.cli.bash.BashCommandLineParser.SPC;

public enum ParseState {
    SPACE("" + DBLQ + SNGQ + SPC + BKSL),
    FREE("" + DBLQ + SNGQ + SPC + BKSL),
    DOUBLE_QUOTES("" + DBLQ + BKSL),
    SINGLE_QUOTES("");

    private String escapeChars;
    private ParseState(String escapeChars) {
        this.escapeChars = escapeChars;
    }
    public String getEscapeChars() {
        return escapeChars;
    }
}
