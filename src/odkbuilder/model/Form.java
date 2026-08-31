package odkbuilder.model;

import java.util.ArrayList;

/*
 * The whole form. The model in MVVM, and the observable that the
 * ViewModel watches.
 *
 * No Swing anywhere in this package, on purpose. The model and the
 * validaton have to be testable on their own, and that only true if
 * the model does not know a window exists.
 * All it does is call notifyObservers().
 */
public class Form extends Observable {

    private String title;
    private String formId;
    private String version;

    /*
     * Top of the tree. A ContainerNode, so the same add / remove / move code
     * works at the top level and inside a group.
     *
     * The exporters skip its own begin/end rows, because the top level of
     * a form is not wrapped in a group.
     */
    private ContainerNode root;

    // Choice lists belong to the form, not to the questions. Long reason is
    // in SelectQuestionNode.
    private ArrayList<ChoiceList> choiceLists = new ArrayList<ChoiceList>();

    public Form(String title, String formId) {
        this.title = title;
        this.formId = formId;
        this.version = "1";
        this.root = new ContainerNode("root", title);
    }

    public ContainerNode getRoot() {
        return root;
    }
    public String getTitle() {
        return title;
    }
    public String getFormId() {
        return formId;
    }
    public String getVersion() {
        return version;
    }

    //Every setter notifies, so the window redraws as soon as a field change.
    public void setTitle(String title) {
        this.title = title;
        root.setLabel(title);
        notifyObservers();
    }
    public void setFormId(String formId) {
        this.formId = formId;
        notifyObservers();
    }
    public void setVersion(String version) {
        this.version = version;
        notifyObservers();
    }

    /*
     * ViewModel calls this after it change something in the tree.
     *
     * The notify stays here instead of going on every FormNode, which
     * would mean every little question carrying a list of observers it
     * never uses.
     */
    public void formChanged() {
        notifyObservers();
    }


    public ArrayList<ChoiceList> getChoiceLists() {
        return choiceLists;
    }

    public ChoiceList findChoiceList(String listName) {
        for (int i = 0; i < choiceLists.size(); i++) {
            if (choiceLists.get(i).getListName().equals(listName)) {
                return choiceLists.get(i);
            }
        }
        return null;
    }

    public void addChoiceList(ChoiceList list) {
        choiceLists.add(list);
    }
    public void removeChoiceList(ChoiceList lst) {
        choiceLists.remove(lst);
    }

    //before a list gets deleted, check if any question still pointing at
    //it. Only possible because the lists are shared.
    public boolean isChoiceListInUse(String listName) {
        ArrayList<FormNode> all = collectAllNodes();
        for (int i = 0; i < all.size(); i++) {
            FormNode n = all.get(i);
            if (n instanceof SelectQuestionNode) {
                SelectQuestionNode sq = (SelectQuestionNode) n;
                if (sq.getListName() != null && sq.getListName().equals(listName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * Flattens the tree into one list.
     *
     * The validator and both exporters need to visit every node, so the
     * recursion sits here once instead of three copies.
     *
     * The instanceof below is the price of keeping getChildren() off the
     * leaf classes. Worth it, it stays in this one method.
     */
    public ArrayList<FormNode> collectAllNodes() {
        ArrayList<FormNode> out = new ArrayList<FormNode>();
        collect(root, out);
        return out;
    }

    private void collect(FormNode node, ArrayList<FormNode> out) {
        if (node != root) {
            out.add(node);
        }
        if (node instanceof ContainerNode) {
            ContainerNode g = (ContainerNode) node;
            for (int i = 0; i < g.getChildren().size(); i++) {
                collect(g.getChildren().get(i), out);
            }
        }
    }


    // private int countQuestions() {
    //     int total = 0;
    //     ArrayList<FormNode> all = collectAllNodes();
    //     for (int i = 0; i < all.size(); i++) {
    //         if (all.get(i) instanceof QuestionNode) {
    //             total++;
    //         }
    //     }
    //     return total;
    // }
}
