package odkbuilder.model;

/*
 * Component of the Composite pattern.
 * Base type for anything on the canvas.
 *
 * A form is a tree. A group holds questions, and a group can hold
 * another group, so everything needs one common type.
 *
 * This is version 1 of the pattern. The component does not declare
 * add() and remove(), they only live on ContainerNode.
 * So a leaf and a composite cannot be swapped without an instanceof
 * check first, and that check shows up in the tree model, the
 * validator and both exporters.
 *
 * A Date question cannot hold anything. Put add() here and every leaf
 * carries a method it can never use.
 */
public abstract class FormNode {

    private String name;
    private String label;
    private String hint;

    //Link back up the tree, so the association is navigable both ways.
    //Needed so when a control gets dropped on top of a question, the
    //group that question living in can be found.
    private ContainerNode parent;

    public FormNode(String name, String label) {
        this.name = name;
        this.label = label;
        this.hint = "";
    }

    // goes in the "type" column of the survey sheet. Every node answers different, so
    // abstract.
    public abstract String getXlsFormType();

    // What shows on the canvas.
    public abstract String getDisplayType();


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public String getHint() {
        return hint;
    }
    public void setHint(String hint) {
        this.hint = hint;
    }

    public ContainerNode getParent() {
        return parent;
    }
    public void setParent(ContainerNode parent) {
        this.parent = parent;
    }

    // JTree calls this to draw the row.
    @Override
    public String toString() {
        String shown = label;
        if (shown == null || shown.trim().equals("")) {
            shown = "(no label)";
        }
        return shown + "   [" + getDisplayType() + " : " + name + "]";
    }
}
