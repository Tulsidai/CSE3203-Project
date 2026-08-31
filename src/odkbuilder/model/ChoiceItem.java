package odkbuilder.model;

// One row on the choices sheet. name is what gets saved,
// label is what the enumerator reads.
public class ChoiceItem {

    private String name;
    private String label;

    public ChoiceItem(String name, String label) {
        this.name = name;
        this.label = label;
    }

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

    @Override
    public String toString() {
        return label + "  (" + name + ")";
    }
}
