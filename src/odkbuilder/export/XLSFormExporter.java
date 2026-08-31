package odkbuilder.export;

import java.io.File;
import java.util.ArrayList;
import odkbuilder.model.ChoiceItem;
import odkbuilder.model.ChoiceList;
import odkbuilder.model.Form;
import odkbuilder.model.FormNode;
import odkbuilder.model.ContainerNode;
import odkbuilder.model.QuestionNode;

public class XLSFormExporter implements FormExporter {
    @Override
    public String getFormatName() {
        return "XLSForm spreadsheet";
    }
    @Override
    public String getFileExtension() {
        return "xlsx";
    }

    @Override
    public void export(Form form, File file) throws Exception {
        XLSXWriter writer = new XLSXWriter();
        writer.addSheet("survey", buildSurveySheet(form));
        writer.addSheet("choices", buildChoicesSheet(form));
        writer.addSheet("settings", buildSettingsSheet(form));
        writer.write(file);
    }

    private ArrayList<String[]> buildSurveySheet(Form form) {
        ArrayList<String[]> rows = new ArrayList<String[]>();

        rows.add(new String[]{
            "type", "name", "label", "hint", "required", "default",
            "constraint", "constraint_message", "relevant", "appearance"
        });

        ContainerNode root = form.getRoot();
        for (int i = 0; i < root.getChildren().size(); i++) {
            writeNode(root.getChildren().get(i), rows);
        }
        return rows;
    }

    private void writeNode(FormNode node, ArrayList<String[]> rows) {
        if (node instanceof ContainerNode) {
            ContainerNode group = (ContainerNode) node;

            rows.add(new String[]{
                group.getXlsFormType(), group.getName(), group.getLabel(),
                group.getHint(), "", "", "", "", "", ""
            });
            for (int i = 0; i < group.getChildren().size(); i++) {
                writeNode(group.getChildren().get(i), rows);
            }
            rows.add(new String[]{
                group.getXlsFormEndType(), group.getName(), "", "", "", "", "", "", "", ""
            });
            return;
        }
        QuestionNode q = (QuestionNode) node;
        rows.add(new String[]{
            q.getXlsFormType(),
            q.getName(),
            q.getLabel(),
            q.getHint(),
            q.isRequired() ? "yes" : "",
            q.getDefaultValue(),
            q.getConstraint(),
            q.getConstraintMessage(),
            q.getRelevant(),
            q.getAppearance()
        });
    }

    private ArrayList<String[]> buildChoicesSheet(Form form) {
        ArrayList<String[]> rows = new ArrayList<String[]>();
        rows.add(new String[]{"list_name", "name", "label"});

        ArrayList<ChoiceList> lists = form.getChoiceLists();
        for (int i = 0; i < lists.size(); i++) {
            ChoiceList list = lists.get(i);
            for (int j = 0; j < list.getChoices().size(); j++) {
                ChoiceItem c = list.getChoices().get(j);
                rows.add(new String[]{list.getListName(), c.getName(), c.getLabel()});
            }
        }
        return rows;
    }

    private ArrayList<String[]> buildSettingsSheet(Form theForm) {
        ArrayList<String[]> rows = new ArrayList<String[]>();
        rows.add(new String[]{"form_title", "form_id", "version"});
        rows.add(new String[]{theForm.getTitle(), theForm.getFormId(), theForm.getVersion()});
        return rows;
    }
}
