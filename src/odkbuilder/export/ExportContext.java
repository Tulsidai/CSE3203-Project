package odkbuilder.export;

import java.io.File;
import odkbuilder.model.Form;

public class ExportContext {
    private FormExporter exporter;

    public ExportContext(FormExporter exporter) {
        this.exporter = exporter;
    }

    public void setExporter(FormExporter exporter) {
        this.exporter = exporter;
    }
    public FormExporter getExporter() {
        return exporter;
    }

    public void doExport(Form form, File file) throws Exception {
        exporter.export(form, file);
    }
}
