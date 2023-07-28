package finio.ui.actions.imprt;

import finio.core.FConst;
import finio.core.NonTerminal;
import finio.extractors.CsvExtractor;
import finio.extractors.JsonExtractor;
import finio.extractors.NonTerminalExtractor;
import finio.extractors.XmlExtractor;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.multidlg.FileInputSourcePanel;
import finio.ui.multidlg.InputBundle;
import finio.ui.multidlg.InputBundleValidator;
import finio.ui.multidlg.MultiInputChooserDialog;
import finio.ui.multidlg.WebInputSourcePanel;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;
import replete.ui.GuiUtil;
import replete.ui.fc.RFileChooser;
import replete.ui.fc.RFilterBuilder;
import replete.util.DateUtil;

public class ImportTextWorker extends FWorker<InputBundle[], NonTerminal[]> {


    //////////
    // ENUM //
    //////////

    public enum Format {
        CSV, JSON, XML
    }


    ////////////
    // FIELDS //
    ////////////

    private String filterName;
    private String filterExt;
    private Format format;
    private InputBundleValidator validator;
    private SelectionContext C;
    private NonTerminal Mcontext;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ImportTextWorker(AppContext ac, WorldContext wc, String name, String filterName,
                            String filterExt, Format format,
                            InputBundleValidator validator) {
        super(ac, wc, name);
        this.filterName = filterName;
        this.filterExt = filterExt;
        this.format = format;
        this.validator = validator;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected InputBundle[] gather() {

        MultiInputChooserDialog dialog =
            new MultiInputChooserDialog(
                ac.getWindow(), getName());
        dialog.setValidator(validator);

        RFileChooser chooser = RFileChooser.getChooser(true);
        RFileChooser.whiteCombos(chooser);
        GuiUtil.addMnemonics(chooser, RFileChooser.basicCaptions);
        new RFilterBuilder(chooser, false)
            .append(filterName, filterExt)
            .appendAcceptAllFilter();

        FileInputSourcePanel pnlFileInput = dialog.getInputPanel(FileInputSourcePanel.class);
        pnlFileInput.setChooser(chooser);

        WebInputSourcePanel pnlWebInput = dialog.getInputPanel(WebInputSourcePanel.class);
        pnlWebInput.setHttpRequestOptions(ac.getConfig().getRequestOptions());
        pnlWebInput.addRecentLink("http://rss.slashdot.org/Slashdot/slashdot");
        pnlWebInput.addRecentLink("http://www.whatismyip.com/");
        pnlWebInput.addRecentLink("http://ip.jsontest.com/");
        pnlWebInput.addRecentLink("http://headers.jsontest.com/");
        pnlWebInput.addRecentLink("http://date.jsontest.com/");
        pnlWebInput.addRecentLink("http://echo.jsontest.com/red/bad/green/good");
        pnlWebInput.addRecentLink("http://www.procato.com/my+headers/");

        dialog.setVisible(true);

        if(dialog.getResult() == MultiInputChooserDialog.ACCEPT) {

            // Grab appropriate context from the tree.
            C = wc.getWorldPanel().getSelectedView().getSelectedValue();
            Mcontext = (NonTerminal) C.getV();
            return dialog.getDataBundles();
        }

        return null;
    }

    @Override
    protected boolean proceed(InputBundle[] gathered) {
        return gathered != null;
    }

    @Override
    protected NonTerminal[] background(InputBundle[] bundles) throws Exception {
        NonTerminal[] Ms = new NonTerminal[bundles.length];
        int i = 0;
        for(InputBundle bundle : bundles) {
            NonTerminal M = null;
            if(bundle.getNT() == null) {
                String text = bundle.getText();
                NonTerminalExtractor X = null;
                switch(format) {
                    case CSV:  X = new CsvExtractor(text);  break;
                    case JSON: X = new JsonExtractor(text); break;
                    case XML:  X = new XmlExtractor(text);  break;
                }
                M = X.extract();
                M.putSysMeta("time", DateUtil.toLongString(System.currentTimeMillis()));
                M.putSysMeta("source", format.name() + " Document");
                M.putSysMeta(FConst.SYS_ALT_KEY, text);
            } else {
                M = bundle.getNT();
            }

            String Kproposed = (String) Mcontext.getNextAvailableKey(getName() + "-");
            String K = getDesiredKeyName(Kproposed, Mcontext);
            Mcontext.put(K, M);
            select(
                new SelectRequest()
                    .setContext(C)
                    .setAction(SelectAction.CHILD)
                    .setArgs(K)
            );
            expand(
                new ExpandRequest()
                    .setContext(C)
                    .setAction(SelectAction.CHILD)
                    .setArgs(K)
            );

            Ms[i++] = M;
        }
        return Ms;
    }

    @Override
    public String getActionVerb() {
        return "importing text content";
    }
}
