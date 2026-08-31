package odkbuilder.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class PaletteView extends JPanel {
    public static final String[] CONTROLS = {
        "Text", "Integer", "Decimal", "Date", "Note",
        "Select One", "Select Multiple", "Group", "Repeat"
    };

    private JList<String> list;

    public PaletteView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Palette"));
        setPreferredSize(new Dimension(190, 100));

        DefaultListModel<String> model = new DefaultListModel<String>();
        for (int i = 0; i < CONTROLS.length; i++) {
            model.addElement(CONTROLS[i]);
        }
        list = new JList<String>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setDragEnabled(true);
        list.setSelectedIndex(0);

        add(new JScrollPane(list), BorderLayout.CENTER);
        JLabel help = new JLabel("<html><small>Drag onto the canvas.<br>"
                + "Drop on a Group or Repeat<br>to nest inside it.</small></html>");
        help.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        add(help, BorderLayout.SOUTH);
    }

    public JList<String> getList() {
        return list;
    }
}
