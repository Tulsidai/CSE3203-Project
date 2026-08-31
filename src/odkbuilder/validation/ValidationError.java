package odkbuilder.validation;

import odkbuilder.model.FormNode;

//One problem found in the form. Keeps the node that caused it, so the view can jump the
//user straight there when they click the message.
public class ValidationError {

    private FormNode node;
    private String message;

    public ValidationError(FormNode node, String message) {
        this.node = node;
        this.message = message;
    }

    public FormNode getNode() {
        return node;
    }
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        if (node == null) {
            return message;
        }
        String who = node.getName();
        if (who == null || who.trim().equals("")) {
            who = "(unnamed)";
        }
        return who + ": " + message;
    }
}
