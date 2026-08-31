package odkbuilder.model;

/*
 * Abstract. The user picks Select One or Select Multiple, never a
 * plain "select".
 *
 * The big decision here: this class holds the name of a choice list,
 * not the choices. The real ChoiceList objects belong to the Form.
 *
 * A survey reuses one yes/no list across dozens of questions.
 * If every SelectQuestionNode owned a copy, fixing one label means editing
 * it in every question, and the choices sheet would repeat the same
 * list over and over.
 *
 * XLSForm already works this way. The survey sheet says
 * "select_one yes_no" and the choices sheet defines yes_no once.
 */
public abstract class SelectQuestionNode extends QuestionNode {

    private String listName;

    public SelectQuestionNode(String name, String label, String listName) {
        super(name, label);
        this.listName = listName;
    }

    // "select_one" or "select_multiple". Subclass fills it in.
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
