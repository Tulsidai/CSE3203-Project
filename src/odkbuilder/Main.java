package odkbuilder;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import odkbuilder.export.ExportContext;
import odkbuilder.export.XLSFormExporter;
import odkbuilder.model.Form;
import odkbuilder.validation.FormValidator;
import odkbuilder.view.MainWindowView;
import odkbuilder.viewmodel.FormViewModel;

/*
 * Entry point.
 *
 * Builds one object of each layer and hands each one what it depends
 * on. The Form is the model, FormValidator holds the rules,
 * ExportContext holds whichver exporter is picked, the FormViewModel
 * watches the Form and the MainWindowView watches the ViewModel.
 *
 * The Form gets created without the window being mentioned once.
 * Nothing below the ViewModel knows a user interface exists.
 */
public class Main {

    public static void main(String[] args) {

        // Swing wants everything built on its own thread.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                    // default look and feel is fine, carry on
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
