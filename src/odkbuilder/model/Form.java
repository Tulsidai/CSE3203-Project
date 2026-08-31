package odkbuilder.model;

import java.util.ArrayList;

public class Form extends Observable {
    private String title;
    private String formId;
    private String version;

    private ContainerNode root;

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

    private int countQuestions() {
        int total = 0;
        ArrayList<FormNode> all = collectAllNodes();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i) instanceof QuestionNode) {
                total++;
            }
        }
        return total;
    }
}
