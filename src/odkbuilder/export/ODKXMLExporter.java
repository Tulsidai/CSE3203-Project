package odkbuilder.export;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import odkbuilder.model.ChoiceItem;
import odkbuilder.model.ChoiceList;
import odkbuilder.model.Form;
import odkbuilder.model.FormNode;
import odkbuilder.model.ContainerNode;
import odkbuilder.model.QuestionNode;
import odkbuilder.model.SelectOneQuestionNode;
import odkbuilder.model.SelectQuestionNode;

public class ODKXMLExporter implements FormExporter {
    @Override
    public String getFormatName() {
        return "ODK XML form definition";
    }
    @Override
    public String getFileExtension() {
        return "xml";
    }

    @Override
    public void export(Form form, File file) throws Exception {
        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\"?>\n");
        xml.append("<h:html xmlns=\"http://www.w3.org/2002/xforms\"");
        xml.append(" xmlns:h=\"http://www.w3.org/1999/xhtml\">\n");
        xml.append("<h:head>\n");
        xml.append("<h:title>").append(clean(form.getTitle())).append("</h:title>\n");
        xml.append("<model>\n");

        xml.append("<instance>\n");
        xml.append("<data id=\"").append(clean(form.getFormId())).append("\">\n");
        writeInstance(form.getRoot(), xml);
        xml.append("</data>\n");
        xml.append("</instance>\n");

        writeBinds(form, xml);
        xml.append("</model>\n");
        xml.append("</h:head>\n");
        xml.append("<h:body>\n");
        writeBody(form, form.getRoot(), xml);
        xml.append("</h:body>\n");
        xml.append("</h:html>\n");

        FileOutputStream out = new FileOutputStream(file);
        out.write(xml.toString().getBytes("UTF-8"));
        out.close();
    }

    private void writeInstance(ContainerNode parent, StringBuilder xml) {
        for (int i = 0; i < parent.getChildren().size(); i++) {
            FormNode node = parent.getChildren().get(i);

            if (node instanceof ContainerNode) {
                xml.append("<").append(node.getName()).append(">\n");
                writeInstance((ContainerNode) node, xml);
                xml.append("</").append(node.getName()).append(">\n");
            } else {
                xml.append("<").append(node.getName()).append("/>\n");
            }
        }
    }

    private void writeBinds(Form form, StringBuilder xml) {
        ArrayList<FormNode> nodes = form.collectAllNodes();

        for (int i = 0; i < nodes.size(); i++) {
            FormNode node = nodes.get(i);
            if (!(node instanceof QuestionNode)) {
                continue;
            }
            QuestionNode question = (QuestionNode) node;

            xml.append("<bind nodeset=\"/data/").append(question.getName()).append("\"");
            xml.append(" type=\"").append(xmlType(question)).append("\"");

            if (question.isRequired()) {
                xml.append(" required=\"true()\"");
            }
            if (!question.getConstraint().trim().equals("")) {
                xml.append(" constraint=\"").append(clean(question.getConstraint())).append("\"");
            }
            if (!question.getRelevant().trim().equals("")) {
                xml.append(" relevant=\"").append(clean(question.getRelevant())).append("\"");
            }
            xml.append("/>\n");
        }
    }

    private String xmlType(QuestionNode question) {
        String type = question.getXlsFormType();

        if (type.equals("integer")) {
            return "int";
        }
        if (type.equals("decimal")) {
            return "decimal";
        }
        if (type.equals("date")) {
            return "date";
        }
        return "string";
    }

    private void writeBody(Form form, ContainerNode parent, StringBuilder xml) {
        for (int i = 0; i < parent.getChildren().size(); i++) {
            FormNode node = parent.getChildren().get(i);

            if (node instanceof ContainerNode) {
                xml.append("<group>\n");
                xml.append("<label>").append(clean(node.getLabel())).append("</label>\n");
                writeBody(form, (ContainerNode) node, xml);
                xml.append("</group>\n");

            } else if (node instanceof SelectQuestionNode) {
                writeSelect(form, (SelectQuestionNode) node, xml);

            } else {
                xml.append("<input ref=\"/data/").append(node.getName()).append("\">\n");
                xml.append("<label>").append(clean(node.getLabel())).append("</label>\n");
                xml.append("</input>\n");
            }
        }
    }

    private void writeSelect(Form form, SelectQuestionNode question, StringBuilder xml) {
        String tag = "select";
        if (question instanceof SelectOneQuestionNode) {
            tag = "select1";
        }

        xml.append("<").append(tag).append(" ref=\"/data/")
           .append(question.getName()).append("\">\n");
        xml.append("<label>").append(clean(question.getLabel())).append("</label>\n");

        ChoiceList list = form.findChoiceList(question.getListName());
        if (list != null) {
            for (int i = 0; i < list.getChoices().size(); i++) {
                ChoiceItem choice = list.getChoices().get(i);
                xml.append("<item>\n");
                xml.append("<label>").append(clean(choice.getLabel())).append("</label>\n");
                xml.append("<value>").append(clean(choice.getName())).append("</value>\n");
                xml.append("</item>\n");
            }
        }
        xml.append("</").append(tag).append(">\n");
    }

    private String clean(String text) {
        if (text == null) {
            return "";
        }
        String out = text.replace("&", "&amp;");
        out = out.replace("<", "&lt;");
        out = out.replace(">", "&gt;");
        out = out.replace("\"", "&quot;");
        return out;
    }
}
