package odkbuilder.model;

public abstract class SelectQuestionNode extends QuestionNode {
    private String listName;

    public SelectQuestionNode(String name, String label, String listName) {
        super(name, label);
        this.listName = listName;
    }

    protected abstract String getSelectKeyword();

    @Override
    public String getXlsFormType() {
        return getSelectKeyword() + " " + listName;
    }

    public String getListName() {
        return listName;
    }
    public void setListName(String listName) {
        this.listName = listName;
    }
}
