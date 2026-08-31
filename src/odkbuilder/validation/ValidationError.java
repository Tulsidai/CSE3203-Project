package odkbuilder.validation;

import odkbuilder.model.FormNode;

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
