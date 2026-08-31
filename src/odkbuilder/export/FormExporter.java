package odkbuilder.export;

import java.io.File;
import odkbuilder.model.Form;

public interface FormExporter {
    void export(Form form, File file) throws Exception;

    String getFormatName();
    String getFileExtension();
}
