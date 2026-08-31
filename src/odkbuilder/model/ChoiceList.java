package odkbuilder.model;

import java.util.ArrayList;

/*
 * A named list, like "yes_no" holding Yes and No.
 *
 * ChoiceList to ChoiceItem is composition. Kill the list and the choices
 * dead too.
 *
 * SelectQuestionNode to ChoiceList is only association. Delete a question
 * and the list must stay, plenty other questions still using it.
 */
public class ChoiceList {

    private String listName;
    private ArrayList<ChoiceItem> choices = new ArrayList<ChoiceItem>();

    public ChoiceList(String listName) {
        this.listName = listName;
    }

    public String getListName() {
        return listName;
    }
    public void setListName(String listName) {
        this.listName = listName;
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

    @Override
    public String toString() {
        return listName;
    }
}
