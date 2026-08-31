package odkbuilder.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import odkbuilder.validation.ValidationError;
import odkbuilder.viewmodel.FormViewModel;

public class ValidationSummaryView extends JPanel {
    private FormViewModel viewModel;
    private CanvasView canvas;

    private JLabel heading;
    private DefaultListModel<ValidationError> listModel;
    private JList<ValidationError> list;

    private boolean refreshing = false;

    public ValidationSummaryView(FormViewModel viewModel, CanvasView canvas) {
        this.viewModel = viewModel;
        this.canvas = canvas;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Validation"));
        setPreferredSize(new Dimension(100, 150));

        heading = new JLabel(" ");
        heading.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        add(heading, BorderLayout.NORTH);
        listModel = new DefaultListModel<ValidationError>();
        list = new JList<ValidationError>(listModel);

        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (refreshing || e.getValueIsAdjusting()) {
                    return;
                }
                ValidationError selected = list.getSelectedValue();
                if (selected != null && selected.getNode() != null) {
                    canvas.selectNode(selected.getNode());
                }
            }
        });

        add(new JScrollPane(list), BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        refreshing = true;

        listModel.clear();
        ArrayList<ValidationError> errors = viewModel.getErrors();
        for (int i = 0; i < errors.size(); i++) {
            listModel.addElement(errors.get(i));
        }

        if (errors.isEmpty()) {
            heading.setText("No problems found. The form is ready to export.");
            heading.setForeground(new Color(0, 120, 0));
        } else {
            heading.setText(errors.size() + " problem(s) found. Click one to "
                    + "jump to it on the canvas.");
            heading.setForeground(new Color(170, 0, 0));
        }

        refreshing = false;
    }
}
