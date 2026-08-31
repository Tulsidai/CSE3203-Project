package odkbuilder.model;

import java.util.ArrayList;

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
