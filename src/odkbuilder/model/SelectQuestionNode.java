package odkbuilder.model;

import java.util.ArrayList;

public abstract class SelectQuestionNode extends QuestionNode {
    private ArrayList<ChoiceItem> choices = new ArrayList<ChoiceItem>();

    public SelectQuestionNode(String name, String label) {
        super(name, label);
    }

    protected abstract String getSelectKeyword();

    @Override
    public String getXlsFormType() {
        return getSelectKeyword() + " " + getName();
    }

    public ArrayList<ChoiceItem> getChoices() {
        return choices;
    }
    public void addChoice(ChoiceItem c) {
        choices.add(c);
    }
    public void removeChoice(ChoiceItem c) {
        choices.remove(c);
    }
}
