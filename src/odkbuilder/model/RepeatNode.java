package odkbuilder.model;

/*
 * A repeat is a group. It holds children, things nest in it.
 * The only difference is the two words it writes on the survey sheet.
 * So it extends ContainerNode instead of copying the children list out again.
 */
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
