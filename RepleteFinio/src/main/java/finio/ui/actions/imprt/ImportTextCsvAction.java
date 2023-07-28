package finio.ui.actions.imprt;

import java.util.ArrayList;
import java.util.List;

import finio.core.FConst;
import finio.core.NonTerminal;
import finio.extractors.CsvExtractor;
import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.multidlg.InputBundle;
import finio.ui.multidlg.InputBundleValidationProblem;
import finio.ui.multidlg.InputBundleValidator;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.ToolBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;
import replete.util.DateUtil;

public class ImportTextCsvAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new ImportTextWorker(ac, ac.getSelectedWorld(), "Import CSV Document",
                    "CSV Documents (*.csv)", "csv", ImportTextWorker.Format.CSV,
                    new InputBundleValidator() {
                        public InputBundleValidationProblem[] validate(InputBundle[] bundles) {
                            List<InputBundleValidationProblem> problems = new ArrayList<>();
                            for(InputBundle bundle : bundles) {
                                try {
                                    String text = bundle.getText();
                                    CsvExtractor X = new CsvExtractor(text);
                                    NonTerminal M = X.extract();
                                    M.putSysMeta("time", DateUtil.toLongString(System.currentTimeMillis()));
                                    M.putSysMeta("source", "CSV Document");
                                    M.putSysMeta(FConst.SYS_ALT_KEY, text);
                                    bundle.setNonTerminal(M);
                                } catch(Exception e) {
                                    problems.add(new InputBundleValidationProblem(bundle, e));
                                }
                            }
                            return problems.toArray(new InputBundleValidationProblem[0]);
                        }
                    });
            }
        };

        AActionValidator validator = new AActionValidator(ac)
            .setTerminalAllowed(false)
            .setSingleSelect()
        ;

        map.createAction("import-csv", listener, validator)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("import")
                    .setText("From CSV...")
                    .setSepGroup("import-text")
                    .setIcon(CommonConcepts.CSV))
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setPath("import")
                    .setText("From CSV...")
                    .setSepGroup("import-text")
                    .setIcon(CommonConcepts.CSV))
            .addDescriptor(
                new ToolBarActionDescriptor()
                    .setGroup("import2")
                    .setToolTipText("Import from CSV...")
                    .setIcon(CommonConcepts.CSV));

    }

}
