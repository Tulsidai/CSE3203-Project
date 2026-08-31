package odkbuilder.model;

public class RepeatNode extends ContainerNode {
    public RepeatNode(String name, String label) {
        super(name, label);
    }

    @Override
    public String getXlsFormType() {
        return "begin repeat";
    }
    @Override
    public String getXlsFormEndType() {
        return "end repeat";
    }
    @Override
    public String getDisplayType() {
        return "Repeat";
    }
}
