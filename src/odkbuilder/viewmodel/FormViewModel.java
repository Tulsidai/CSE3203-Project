package odkbuilder.viewmodel;

import java.io.File;
import java.util.ArrayList;
import odkbuilder.export.ExportContext;
import odkbuilder.export.FormExporter;
import odkbuilder.model.ChoiceItem;
import odkbuilder.model.ChoiceList;
import odkbuilder.model.DateQuestionNode;
import odkbuilder.model.DecimalQuestionNode;
import odkbuilder.model.Form;
import odkbuilder.model.FormNode;
import odkbuilder.model.FormObserver;
import odkbuilder.model.ContainerNode;
import odkbuilder.model.IntegerQuestionNode;
import odkbuilder.model.NoteQuestionNode;
import odkbuilder.model.Observable;
import odkbuilder.model.RepeatNode;
import odkbuilder.model.SelectMultipleQuestionNode;
import odkbuilder.model.SelectOneQuestionNode;
import odkbuilder.model.TextQuestionNode;
import odkbuilder.validation.FormValidator;
import odkbuilder.validation.ValidationError;

public class FormViewModel extends Observable implements FormObserver {
    private Form form;
    private FormValidator validator;
    private ExportContext exportContext;

    private FormNode selectedNode;
    private ArrayList<ValidationError> errors = new ArrayList<ValidationError>();

    private int nameCounter = 1;

    public FormViewModel(Form form, FormValidator validator, ExportContext exportContext) {
        this.form = form;
        this.validator = validator;
        this.exportContext = exportContext;
        this.form.addObserver(this);
        runValidation();
    }

    @Override
    public void update() {
        runValidation();
        notifyObservers();
    }

    public Form getForm() {
        return form;
    }
    public ArrayList<ValidationError> getErrors() {
        return errors;
    }
    public FormNode getSelectedNode() {
        return selectedNode;
    }
    public ExportContext getExportContext() {
        return exportContext;
    }

    public void setSelectedNode(FormNode node) {
        this.selectedNode = node;
        notifyObservers();
    }

    private void runValidation() {
        errors = validator.validate(form);
    }

    public FormNode addControl(String paletteType, FormNode dropTarget) {
        FormNode newNode = createNode(paletteType);
        if (newNode == null) {
            return null;
        }

        if (dropTarget == null) {
            dropTarget = form.getRoot();
        }

        if (dropTarget instanceof ContainerNode) {
            ((ContainerNode) dropTarget).add(newNode);
        } else {
            ContainerNode parent = dropTarget.getParent();
            if (parent == null) {
                parent = form.getRoot();
                parent.add(newNode);
            } else {
                parent.addAt(parent.indexOf(dropTarget) + 1, newNode);
            }
        }
        selectedNode = newNode;
        form.formChanged();
        return newNode;
    }

    private FormNode createNode(String paletteType) {
        String name = "q" + nameCounter;
        nameCounter++;
        if (paletteType.equals("Text")) {
            return new TextQuestionNode(name, "");
        }
        if (paletteType.equals("Integer")) {
            return new IntegerQuestionNode(name, "");
        }
        if (paletteType.equals("Decimal")) {
            return new DecimalQuestionNode(name, "");
        }
        if (paletteType.equals("Date")) {
            return new DateQuestionNode(name, "");
        }
        if (paletteType.equals("Note")) {
            return new NoteQuestionNode(name, "");
        }
        if (paletteType.equals("Select One")) {
            return new SelectOneQuestionNode(name, "", "");
        }
        if (paletteType.equals("Select Multiple")) {
            return new SelectMultipleQuestionNode(name, "", "");
        }
        if (paletteType.equals("Group")) {
            return new ContainerNode("g" + name, "");
        }
        if (paletteType.equals("Repeat")) {
            return new RepeatNode("r" + name, "");
        }
        return null;
    }

    public void deleteSelected() {
        if (selectedNode == null || selectedNode == form.getRoot()) {
            return;
        }
        ContainerNode parent = selectedNode.getParent();
        if (parent != null) {
            parent.remove(selectedNode);
            selectedNode = null;
            form.formChanged();
        }
    }

    public void moveSelectedUp() {
        if (selectedNode == null || selectedNode.getParent() == null) {
            return;
        }
        selectedNode.getParent().moveUp(selectedNode);
        form.formChanged();
    }
    public void moveSelectedDown() {
        if (selectedNode == null || selectedNode.getParent() == null) {
            return;
        }
        selectedNode.getParent().moveDown(selectedNode);
        form.formChanged();
    }

    public void nodeEdited() {
        form.formChanged();
    }

    public ChoiceList createChoiceList(String listName) {
        ChoiceList existing = form.findChoiceList(listName);
        if (existing != null) {
            return existing;
        }
        ChoiceList list = new ChoiceList(listName);
        form.addChoiceList(list);
        form.formChanged();
        return list;
    }

    public void addChoice(ChoiceList list, String name, String label) {
        list.addChoice(new ChoiceItem(name, label));
        form.formChanged();
    }
    public void removeChoice(ChoiceList lst, ChoiceItem choice) {
        lst.removeChoice(choice);
        form.formChanged();
    }

    public boolean deleteChoiceList(ChoiceList list) {
        if (form.isChoiceListInUse(list.getListName())) {
            return false;
        }
        form.removeChoiceList(list);
        form.formChanged();
        return true;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void exportForm(FormExporter exporter, File file) throws Exception {
        exportContext.setExporter(exporter);
        exportContext.doExport(form, file);
    }
}
