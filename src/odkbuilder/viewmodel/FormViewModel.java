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

/*
 * The ViewModel in MVVM. Sits between the window and the model and is
 * the only class that knows both sides.
 *
 * No Swing imports, so all of this can be tested by calling methods
 * and reading the answers back.
 *
 * It implements FormObserver so it hears the Form change, and extends
 * Observable so the window hears it change.
 * Model to ViewModel to View, and the Form never hear about the window.
 */
public class FormViewModel extends Observable implements FormObserver {

    private Form form;
    private FormValidator validator;
    private ExportContext exportContext;

    private FormNode selectedNode;
    private ArrayList<ValidationError> errors = new ArrayList<ValidationError>();

    // So new controls get a name nobody using yet.
    private int nameCounter = 1;

    public FormViewModel(Form form, FormValidator validator, ExportContext exportContext) {
        this.form = form;
        this.validator = validator;
        this.exportContext = exportContext;
        this.form.addObserver(this); // subscribe to the model
        runValidation();
    }

    /*
     * The Form calls this when it changes.
     *
     * Revalidation happens here and not in every single method, so it is
     * impossible to edit the form and forget to check it.
     */
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


    /*
     * Called when something gets dropped from the palette.
     *
     * dropTarget is whatever node was under the mouse. Group or repeat,
     * the new control goes inside it. A question, it goes in that
     * question parent, right below it.
     * That is what people expect when they drop one row on another.
     */
    public FormNode addControl(String paletteType, FormNode dropTarget) {
        FormNode newNode = createNode(paletteType);
        if (newNode == null) {
            return null;
        }

        // Dropped on nothing at all, so it belongs at the top level.
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

    /*
     * builds the right object for whatever palette row got dragged.
     *
     * The one place in the program that names the concrete node classes.
     * Everywhere else works through FormNode.
     *
     * TODO: this if-chain grows with every new control type.
     * One more and it becomes a proper factory.
     */
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
        return null; //palette text nobody recognises, so build nothing
    }

    // Root cannot be deleted. It is the form itself.
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

    /*
     * Property inspector calls this after an edit.
     *
     * notifyObservers() stays out of the setters on FormNode on purpose.
     * The model would fire dozens of times while somebody still typing.
     */
    public void nodeEdited() {
        form.formChanged();
    }


    // hand back the existing one if the name is taken. Two lists with the
    // same name would break the choices sheet.
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

    /*
     * refuses to delete a shared list while a question still pointing at
     * it. Without the shared-list design this check could not even exist,
     * there would be no one list to check against.
     */
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

    /*
     * Swaps the exporter in and runs it.
     * The ViewModel never names a file format itself.
     */
    public void exportForm(FormExporter exporter, File file) throws Exception {
        exportContext.setExporter(exporter);
        exportContext.doExport(form, file);
    }
}
