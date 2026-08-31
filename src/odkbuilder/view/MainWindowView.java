package odkbuilder.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import odkbuilder.export.FormExporter;
import odkbuilder.export.ODKXMLExporter;
import odkbuilder.export.XLSFormExporter;
import odkbuilder.model.FormNode;
import odkbuilder.model.FormObserver;
import odkbuilder.viewmodel.FormViewModel;

public class MainWindowView extends JFrame implements FormObserver {
    private FormViewModel viewModel;

    private CanvasView canvas;
    private PropertyInspectorView properties;
    private ValidationSummaryView errors;

    private JTextField titleField;
    private JTextField formIdField;
    private JTextField versionField;

    private FormNode shownNode = null;
    private boolean loadingSettings = false;

    public MainWindowView(FormViewModel viewModel) {
        this.viewModel = viewModel;

        setTitle("ODK Form Builder - CSE 3203 prototype");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setLocationRelativeTo(null);
        canvas = new CanvasView(viewModel);
        properties = new PropertyInspectorView(viewModel);
        errors = new ValidationSummaryView(viewModel, canvas);

        JSplitPane middle = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                canvas, properties);
        middle.setResizeWeight(0.65);

        JSplitPane whole = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                middle, errors);
        whole.setResizeWeight(0.75);

        add(buildTopBar(), BorderLayout.NORTH);
        add(new PaletteView(), BorderLayout.WEST);
        add(whole, BorderLayout.CENTER);

        viewModel.addObserver(this);

        update();
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel();
        bar.setBorder(BorderFactory.createTitledBorder("Form settings and export"));

        titleField = new JTextField(viewModel.getForm().getTitle(), 16);
        formIdField = new JTextField(viewModel.getForm().getFormId(), 12);
        versionField = new JTextField(viewModel.getForm().getVersion(), 5);
        watch(titleField, "title");
        watch(formIdField, "id");
        watch(versionField, "version");

        bar.add(new JLabel("Title:"));
        bar.add(titleField);
        bar.add(new JLabel("Form ID:"));
        bar.add(formIdField);
        bar.add(new JLabel("Version:"));
        bar.add(versionField);

        JButton exportXls = new JButton("Export XLSForm (.xlsx)");
        exportXls.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doExport(new XLSFormExporter());
            }
        });
        JButton exportXml = new JButton("Export ODK XML");
        exportXml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doExport(new ODKXMLExporter());
            }
        });

        bar.add(exportXls);
        bar.add(exportXml);
        return bar;
    }

    private void watch(JTextField field, String which) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                push();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                push();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                push();
            }

            private void push() {
                if (loadingSettings) {
                    return;
                }
                if (which.equals("title")) {
                    viewModel.getForm().setTitle(field.getText());
                } else if (which.equals("id")) {
                    viewModel.getForm().setFormId(field.getText());
                } else {
                    viewModel.getForm().setVersion(field.getText());
                }
            }
        });
    }

    private void doExport(FormExporter exporter) {
        if (viewModel.hasErrors()) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "The form still has " + viewModel.getErrors().size()
                    + " validation problem(s).\nExport anyway?",
                    "Validation", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(safeFileName(viewModel.getForm().getFormId())
                + "." + exporter.getFileExtension()));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        try {
            viewModel.exportForm(exporter, file);
            JOptionPane.showMessageDialog(this,
                    exporter.getFormatName() + " written to:\n" + file.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not write the file:\n" + ex.getMessage(),
                    "Export failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String safeFileName(String raw) {
        if (raw == null || raw.trim().equals("")) {
            return "form";
        }
        return raw.trim().replace(' ', '_');
    }

    @Override
    public void update() {
        canvas.refresh();
        errors.refresh();
        FormNode selected = viewModel.getSelectedNode();
        if (selected != shownNode) {
            shownNode = selected;
            properties.showNode(selected);
        }
        loadingSettings = true;
        if (!titleField.getText().equals(viewModel.getForm().getTitle())) {
            titleField.setText(viewModel.getForm().getTitle());
        }
        loadingSettings = false;
    }
}
