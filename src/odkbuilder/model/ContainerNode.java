package odkbuilder.model;

import java.util.ArrayList;

public class ContainerNode extends FormNode {
    private ArrayList<FormNode> children = new ArrayList<FormNode>();

    public ContainerNode(String name, String label) {
        super(name, label);
    }

    public ArrayList<FormNode> getChildren() {
        return children;
    }

    public void add(FormNode node) {
        children.add(node);
        node.setParent(this);
    }

    public void addAt(int index, FormNode node) {
        if (index < 0) {
            index = 0;
        }
        if (index > children.size()) {
            index = children.size();
        }
        children.add(index, node);
        node.setParent(this);
    }

    public void remove(FormNode node) {
        children.remove(node);
        node.setParent(null);
    }
    public int indexOf(FormNode node) {
        return children.indexOf(node);
    }

    public void moveUp(FormNode node) {
        int i = children.indexOf(node);
        if (i > 0) {
            children.remove(i);
            children.add(i - 1, node);
        }
    }
    public void moveDown(FormNode theNode) {
        int pos = children.indexOf(theNode);
        if (pos >= 0 && pos < children.size() - 1) {
            children.remove(pos);
            children.add(pos + 1, theNode);
        }
    }

    @Override
    public String getXlsFormType() {
        return "begin group";
    }

    public String getXlsFormEndType() {
        return "end group";
    }
    @Override
    public String getDisplayType() {
        return "Group";
    }
}
