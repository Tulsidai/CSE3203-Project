package odkbuilder.model;

import java.util.ArrayList;

/*
 * The composite. The only class that can hold other nodes, so a
 * question can never end up inside another question.
 *
 * A group has-a list of children, and that list is a composition.
 * Delete the group and everything in it goes too.
 */
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

    // For when a control gets dropped on top of an exisiting question.
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

    // moving a node changes the question order in the exported file, so the
    // canvas must be able to do it.
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

    /*
     * First try at drag-to-reorder inside the tree. It moved the node, just
     * never to the place anybody was aiming for. The two buttons won.
     * Leaving it here in case there is time to fix the index.
     *
     * public void moveTo(FormNode node, ContainerNode newParent, int index) {
     *     remove(node);
     *     newParent.addAt(index, node);
     * }
     */

    @Override
    public String getXlsFormType() {
        return "begin group";
    }

    // the row that closes the group off on the survey sheet.
    public String getXlsFormEndType() {
        return "end group";
    }
    @Override
    public String getDisplayType() {
        return "Group";
    }
}
