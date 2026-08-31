package odkbuilder.model;

/*
 * Nobody drops "a question" on the canvas. They drop a Text or a Date.
 * So this one is abstract.
 *
 * name, label and hint already sit on FormNode. Every question shares
 * required, default, constraint, relevant and appearance,
 * so those live here instead of being copied out seven times.
 */
public abstract class QuestionNode extends FormNode {

    private boolean required;
    private String defaultValue;
    private String constraint;
    private String constraintMessage;
    private String relevant;
    private String appearance;
    //TODO: read_only and calculate columns still not done

    public QuestionNode(String name, String label) {
        super(name, label);
        this.required = false;
        this.defaultValue = "";
        this.constraint = "";
        this.constraintMessage = "";
        this.relevant = "";
        this.appearance = "";
    }

    public boolean isRequired() {
        return required;
    }
    public void setRequired(boolean required) {
        this.required = required;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getConstraint() {
        return constraint;
    }
    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }


    public String getConstraintMessage() {
        return constraintMessage;
    }
    public void setConstraintMessage(String constraintMessage) {
        this.constraintMessage = constraintMessage;
    }

    public String getRelevant() {
        return relevant;
    }
    public void setRelevant(String relevant) {
        this.relevant = relevant;
    }
    public String getAppearance() {
        return appearance;
    }
    public void setAppearance(String appearance) {
        this.appearance = appearance;
    }
}
