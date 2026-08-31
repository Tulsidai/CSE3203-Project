package odkbuilder.export;

import java.io.File;
import odkbuilder.model.Form;

/*
 * The strategy interface for exporting.
 *
 * Two formats, both real classes behind here. The rest of the system
 * calls export() without knowing which one plugged in.
 */
public interface FormExporter {

    void export(Form form, File file) throws Exception;

    //Shows on the button, and used for the file chooser filter.
    String getFormatName();
    String getFileExtension();
}
