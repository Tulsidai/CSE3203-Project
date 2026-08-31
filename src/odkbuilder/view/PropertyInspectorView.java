package odkbuilder.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import odkbuilder.model.ChoiceItem;
import odkbuilder.model.ChoiceList;
import odkbuilder.model.FormNode;
import odkbuilder.model.QuestionNode;
import odkbuilder.model.SelectQuestionNode;
import odkbuilder.viewmodel.FormViewModel;

/*
 * The property inspector on the right.
 *
 * Rebuilt whenever a different node gets selected, and which boxes
 * turn up depends on the kind of node. A Date question has no choice
 * list, a Group has no constraint.
 * Showing every box for every node would only confuse the user.
 *
 * One listener serves every text box. Anything typed, applyChanges()
 * reads them all and copies the lot onto the node.
 * Only one place to look if a property ever stops saving.
 */
public class PropertyInspectorView extends JPanel {

    private FormViewModel viewModel;

    // Node being edited right now.
    private FormNode node;

    // True while the boxes are being filled from the model, so that writing
    // does not get mistaken for the user typing.
    private boolean loading = false;

    //The boxes. Some stay null when they do not apply to this node.
    private JTextField nameBox;
    private JTextField labelBox;
    private JTextField hintBox;
    private JCheckBox requiredBox;
    private JTextField defaultBox;
    private JTextField constraintBox;
    private JTextField constraintMessageBox;
    private JTextField relevantBox;
    private JTextField appearanceBox;

    private JPanel fields;
    private GridBagConstraints layout;
    private int row;

    public PropertyInspectorView(FormViewModel viewModel) {
        this.viewModel = viewModel;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Properties"));
        setPreferredSize(new Dimension(330, 100));

        fields = new JPanel(new GridBagLayout());
        add(new JScrollPane(fields), BorderLayout.CENTER);
        showNode(null);
    }

    // builds the panel over again for whatever just got selected.
    public void showNode(FormNode selected) {
        loading = true;
        this.node = selected;
        clearBoxes();
        startLayout();

        if (selected == null) {
            addWide(new JLabel("<html><i>Select something on the canvas.</i></html>"));
            finishLayout();
            loading = false;
            return;
        }

        addWide(new JLabel("<html><b>" + selected.getDisplayType() + "</b></html>"));

        nameBox = addTextBox("Name", selected.getName());
        labelBox = addTextBox("Label", selected.getLabel());
        hintBox = addTextBox("Hint", selected.getHint());

        // everything under here is question only. A group has no default value or constraint,
        // so groups and repeats stop right there.
        if (selected instanceof QuestionNode) {
            QuestionNode question = (QuestionNode) selected;
            requiredBox = new JCheckBox("Required");
            requiredBox.setSelected(question.isRequired());
            requiredBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    applyChanges();
                }
            });
            addWide(requiredBox);

            defaultBox = addTextBox("Default value", question.getDefaultValue());
            constraintBox = addTextBox("Constraint", question.getConstraint());
            constraintMessageBox = addTextBox("Constraint message",
                    question.getConstraintMessage());
            relevantBox = addTextBox("Relevance", question.getRelevant());
            appearanceBox = addTextBox("Appearance", question.getAppearance());
        }

        if (selected instanceof SelectQuestionNode) {
            addChoiceListSection((SelectQuestionNode) selected);
        }

        finishLayout();
        loading = false;
    }

    // Copies whatever in the boxes onto the node, then tells the ViewModel the edit done so it
    // can revalidate and redraw.
    private void applyChanges() {
        if (loading || node == null) {
            return;
        }

        node.setName(nameBox.getText());
        node.setLabel(labelBox.getText());
        node.setHint(hintBox.getText());

        if (node instanceof QuestionNode) {
            QuestionNode question = (QuestionNode) node;
            question.setRequired(requiredBox.isSelected());
            question.setDefaultValue(defaultBox.getText());
            question.setConstraint(constraintBox.getText());
            question.setConstraintMessage(constraintMessageBox.getText());
            question.setRelevant(relevantBox.getText());
            question.setAppearance(appearanceBox.getText());
        }
        viewModel.nodeEdited();
    }

    // Null them out before a rebuild, else applyChanges() could still be
    // holding a box that no longer on screen.
    private void clearBoxes() {
        nameBox = null;
        labelBox = null;
        hintBox = null;
        requiredBox = null;
        defaultBox = null;
        constraintBox = null;
        constraintMessageBox = null;
        relevantBox = null;
        appearanceBox = null;
    }


    /*
     * The drop-down lists every choice list already on the form, so
     * reusing yes_no across twenty questions is one click instead of
     * typing the choices out again.
     */
    private void addChoiceListSection(SelectQuestionNode question) {

        addWide(new JLabel(" "));
        addWide(new JLabel("<html><b>Choice list</b></html>"));

        // editable, so you can type a brand new list name too.
        JComboBox<String> listBox = new JComboBox<String>();
        listBox.setEditable(true);
        for (int i = 0; i < viewModel.getForm().getChoiceLists().size(); i++) {
            listBox.addItem(viewModel.getForm().getChoiceLists().get(i).getListName());
        }
        listBox.setSelectedItem(question.getListName());
        addRow("List name", listBox);
        JButton useList = new JButton("Use this list");
        useList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object picked = listBox.getSelectedItem();
                if (picked == null || picked.toString().trim().equals("")) {
                    return;
                }
                String listName = picked.toString().trim();
                viewModel.createChoiceList(listName);
                question.setListName(listName);
                viewModel.nodeEdited();
                showNode(question); // redraw so the choices show up
            }
        });
        addWide(useList);

        ChoiceList list = viewModel.getForm().findChoiceList(question.getListName());
        DefaultListModel<ChoiceItem> choiceModel = new DefaultListModel<ChoiceItem>();
        if (list != null) {
            for (int i = 0; i < list.getChoices().size(); i++) {
                choiceModel.addElement(list.getChoices().get(i));
            }
        }
        JList<ChoiceItem> choiceView = new JList<ChoiceItem>(choiceModel);
        JScrollPane scroller = new JScrollPane(choiceView);
        scroller.setPreferredSize(new Dimension(200, 110));
        addWide(scroller);

        JPanel buttons = new JPanel();

        JButton addChoice = new JButton("Add choice");
        addChoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (list == null) {
                    JOptionPane.showMessageDialog(PropertyInspectorView.this,
                            "Type a list name first, then press \"Use this list\".");
                    return;
                }
                String value = JOptionPane.showInputDialog(PropertyInspectorView.this,
                        "Stored value (no spaces):");
                if (value == null || value.trim().equals("")) {
                    return;
                }
                String text = JOptionPane.showInputDialog(PropertyInspectorView.this,
                        "Label the enumerator reads:");
                if (text == null) {
                    return;
                }
                viewModel.addChoice(list, value.trim(), text);
                showNode(question);
            }
        });

        JButton removeChoice = new JButton("Remove");
        removeChoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (list != null && choiceView.getSelectedValue() != null) {
                    viewModel.removeChoice(list, choiceView.getSelectedValue());
                    showNode(question);
                }
            }
        });

        buttons.add(addChoice);
        buttons.add(removeChoice);
        addWide(buttons);

        // Warn them, because sharing cuts both ways.
        if (list != null && viewModel.getForm().isChoiceListInUse(list.getListName())) {
            addWide(new JLabel("<html><small>This list is shared. Editing it changes"
                    + "<br>every question that uses it.</small></html>"));
        }
    }


    // Whatever changes, all the boxes get reread, so exactly one
    // method writes to the model.
    private DocumentListener sharedListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            applyChanges();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            applyChanges();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            applyChanges();
        }
    };

    private JTextField addTextBox(String caption, String value) {
        JTextField box = new JTextField(value == null ? "" : value);
        box.getDocument().addDocumentListener(sharedListener);
        addRow(caption, box);
        return box;
    }

    private void startLayout() {
        fields.removeAll();
        row = 0;
        layout = new GridBagConstraints();
        layout.insets = new Insets(3, 4, 3, 4);
        layout.fill = GridBagConstraints.HORIZONTAL;
        layout.anchor = GridBagConstraints.WEST;
    }

    //Caption in column 0, box in column 1.
    private void addRow(String caption, JComponent box) {
        layout.gridx = 0;
        layout.gridy = row;
        layout.gridwidth = 1;
        layout.weightx = 0;
        fields.add(new JLabel(caption), layout);
        layout.gridx = 1;
        layout.weightx = 1;
        fields.add(box, layout);

        row++;
    }

    // Straight across both columns. Reset gridwidth after, or
    // every row after this one comes out wide too.
    private void addWide(JComponent box) {
        layout.gridx = 0;
        layout.gridy = row;
        layout.gridwidth = 2;
        layout.weightx = 1;
        fields.add(box, layout);
        layout.gridwidth = 1;
        row++;
    }

    //empty panel at the bottom soaks up the spare height and pushes
    //everything else up to the top.
    private void finishLayout() {
        layout.gridx = 0;
        layout.gridy = row;
        layout.gridwidth = 2;
        layout.weighty = 1;
        layout.fill = GridBagConstraints.BOTH;
        fields.add(new JPanel(), layout);

        fields.revalidate();
        fields.repaint();
    }
}
