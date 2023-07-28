package replete.diff.generic;

import replete.diff.DifferParamsPanel;
import replete.text.StringUtil;
import replete.ui.form2.FieldDescriptor;
import replete.ui.form2.NewRFormPanel;
import replete.ui.lay.Lay;
import replete.ui.text.RTextArea;
import replete.ui.validation.ValidationContext;

public class GenericObjectDifferParamsPanel extends DifferParamsPanel<GenericObjectDifferParams> {


    ////////////
    // FIELDS //
    ////////////

    private static final String SPLIT_REGEX = "\\s*(,|\\r\\n|\\n|\\r)\\s*";

    private RTextArea txtBlacklist;
    private RTextArea txtWhitelist;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public GenericObjectDifferParamsPanel() {
        Lay.BLtg(this,
            "N", Lay.lb("<html>Please enter desired parameters for the generic differ.</html>", "eb=5,bg=100,fg=white"),
            "C", Lay.sp(new InnerFormPanel(), "eb=0")
        );
    }

    @Override
    public GenericObjectDifferParams get() {
        boolean useBl = !StringUtil.isBlank(txtBlacklist.getText());
        boolean useWl = !StringUtil.isBlank(txtWhitelist.getText());

        GenericObjectDifferParams p = new GenericObjectDifferParams()
            .setUseFunctionBlacklist(useBl)
            .setUseFunctionWhitelist(useWl)
        ;

        if(useBl) {
            String[] blacklist = txtBlacklist.getText().split(SPLIT_REGEX);
            for(String term : blacklist) {
                p.addFieldToBlacklist(term);
            }
        }

        if(useWl) {
            String[] whitelist = txtWhitelist.getText().split(SPLIT_REGEX);
            for(String term : whitelist) {
                p.addFieldToWhitelist(term);
            }
        }

        return p;
    }

    @Override
    public void set (GenericObjectDifferParams params) {
        super.set(params);                                 // Very important - saves the tracked ID via lastSetBean

        for(String field : params.getFieldBlacklist()) {
            txtBlacklist.appendln(field);
        }
        for(String field : params.getFieldWhitelist()) {
            txtWhitelist.appendln(field);
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void validateInput(ValidationContext context) {
        boolean useBl = !StringUtil.isBlank(txtBlacklist.getText());
        boolean useWl = !StringUtil.isBlank(txtWhitelist.getText());

        if(useBl) {
            String[] blacklist = txtBlacklist.getText().trim().split(SPLIT_REGEX);
            for(String term : blacklist) {
                if(!StringUtil.matches(term, "^\\w+$", true)) {
                    context.error("Invalid blacklist term", term);
                }
            }
        }

        if(useWl) {
            String[] whitelist = txtWhitelist.getText().trim().split(SPLIT_REGEX);
            for(String term : whitelist) {
                if(!StringUtil.matches(term, "^\\w+$", true)) {
                    context.error("Invalid whitelist term", term);
                }
            }
        }
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class InnerFormPanel extends NewRFormPanel {
        public InnerFormPanel() {
            super(80);
            setMargin(5);
            setInterFieldSpacing(5);
            init();
        }

        @Override
        public void addFields() {

            // Link Threshold
            addField(
                new FieldDescriptor()
                    .setCaption("Blacklist:")
                    .setComponent(Lay.sp(txtBlacklist = Lay.txa()))
                    .setExpandable(true)
                    .setFill(true)
                    .setHelpText("(separate terms by commas, or place terms on separate lines)")
//                    .setHelpStyle(HelpStyle.) // TOdo this
            );

            // Frontier Size
            addField(
                new FieldDescriptor()
                    .setCaption("Whitelist:")
                    .setComponent(Lay.sp(txtWhitelist = Lay.txa()))
                    .setExpandable(true)
                    .setFill(true)
                    .setHelpText("(separate terms by commas, or place terms on separate lines)")
            );
        }
    }
}
