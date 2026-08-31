package odkbuilder.model;

public abstract class FormNode {
    private String name;
    private String label;
    private String hint;

    public FormNode(String name, String label) {
        this.name = name;
        this.label = label;
        this.hint = "";
    }

    public abstract String getXlsFormType();

    public abstract String getDisplayType();

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

    public String getHint() {
        return hint;
    }
    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    public String toString() {
        String shown = label;
        if (shown == null || shown.trim().equals("")) {
            shown = "(no label)";
        }
        return shown + "   [" + getDisplayType() + " : " + name + "]";
    }
}
