package odkbuilder;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import odkbuilder.export.ExportContext;
import odkbuilder.export.XLSFormExporter;
import odkbuilder.model.Form;
import odkbuilder.validation.FormValidator;
import odkbuilder.view.MainWindowView;
import odkbuilder.viewmodel.FormViewModel;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                }

                Form form = new Form("Untitled Form", "untitled_form");
                FormValidator validator = new FormValidator();
                ExportContext exportContext = new ExportContext(new XLSFormExporter());

                FormViewModel viewModel =
                        new FormViewModel(form, validator, exportContext);

                MainWindowView window = new MainWindowView(viewModel);
                window.setVisible(true);
            }
        });
    }
}
