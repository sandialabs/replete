package replete.diff;

public class SimpleComparison extends Comparison {


    ////////////
    // FIELDS //
    ////////////

    private boolean different;
    private String comment;      // e.g. "Left size is '12' and right size is '14'." or "Left and right sizes are different."
    private String leftClue;     // Optional clues represent the actual values that were different to
    private String rightClue;    // give developer option of not placing this info into the comment field.
    // ^Strings are used instead of Objects for clues to ensure simplicity
    //  and serializability.  Two integers should just be converted to these
    //  strings for example.  Right now the clues are to be left null along
    //  with the comment if there is going to be no difference.  If they are
    //  populated at all, even if they are equal, this will currently be
    //  considered a diff.  No equality check between the clues are peformed.
    //  These are clues *for the already verified difference*.


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SimpleComparison(boolean different) {
        this.different = different;
        // All fields start null, implying no difference represented by this object
    }
    public SimpleComparison(boolean different, String comment) {
        this.different = different;
        this.comment = comment;
    }
    public SimpleComparison(boolean different, String leftClue, String rightClue) {
        this.different = different;
        this.leftClue = leftClue;
        this.rightClue = rightClue;
    }
    public SimpleComparison(boolean different, String comment, String leftClue, String rightClue) {
        this.different = different;
        this.comment = comment;
        this.leftClue = leftClue;
        this.rightClue = rightClue;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public boolean isDifferent() {
        return different;
    }
    public String getComment() {
        return comment;
    }
    public String getLeftClue() {
        return leftClue;
    }
    public String getRightClue() {
        return rightClue;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean isDiff() {
        return different;
    }
    @Override
    public void render(StringBuilder buffer, int level) {
        boolean addedText = false;
        if(!isDiff()) {
            buffer.append("(NO DIFFERENCE)");
            addedText = true;
        }
        if(comment != null) {
            if(addedText) {
                buffer.append(" ");
            }
            buffer.append(comment);
            addedText = true;
        }
        if(leftClue != null || rightClue != null) {
            if(addedText) {
                buffer.append(" ");
            }
            buffer.append("[" + leftClue + " vs. " + rightClue + "]");
        }
    }
    @Override
    public String toString() {
        return "SimpleComparison [different=" + different + ", comment=" + comment + ", leftClue=" +
            leftClue + ", rightClue=" + rightClue + "]";
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + (different ? 1231 : 1237);
        result = prime * result + ((leftClue == null) ? 0 : leftClue.hashCode());
        result = prime * result + ((rightClue == null) ? 0 : rightClue.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        SimpleComparison other = (SimpleComparison) obj;
        if(comment == null) {
            if(other.comment != null) {
                return false;
            }
        } else if(!comment.equals(other.comment)) {
            return false;
        }
        if(different != other.different) {
            return false;
        }
        if(leftClue == null) {
            if(other.leftClue != null) {
                return false;
            }
        } else if(!leftClue.equals(other.leftClue)) {
            return false;
        }
        if(rightClue == null) {
            if(other.rightClue != null) {
                return false;
            }
        } else if(!rightClue.equals(other.rightClue)) {
            return false;
        }
        return true;
    }

}
