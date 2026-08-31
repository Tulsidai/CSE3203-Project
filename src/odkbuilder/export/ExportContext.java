package odkbuilder.export;

import java.io.File;
import odkbuilder.model.Form;

/*
 * The context object. Holds whichever exporter is selected, runs it.
 *
 * The ViewModel talks to this class only, so a third format later
 * (CSV say) changes nothing in the ViewModel or the view.
 */
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
