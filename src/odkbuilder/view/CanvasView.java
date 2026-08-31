package odkbuilder.view;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import odkbuilder.model.FormNode;
import odkbuilder.model.ContainerNode;
import odkbuilder.viewmodel.FormViewModel;

/*
 * The design canvas.
 *
 * A JTree, because the form is a tree. It shows the nesting and the
 * question order at the same time.
 *
 * Swing has its own node class and each one here carries the matching
 * FormNode inside as its user object. So there are two trees, the real
 * one in the model and a copy Swing draws.
 * The copy gets thrown away and rebuilt every time something changes,
 * so the two cannot drift apart.
 *
 * this panel talks to the ViewModel only. It never creates a question.
 */
public class CanvasView extends JPanel {

    private FormViewModel viewModel;
    private JTree tree;
    private DefaultTreeModel treeModel;

    // Set during a redraw, so the panel's own changes are not read as user clicks.
    private boolean refreshing = false;

    public CanvasView(FormViewModel viewModel) {
        this.viewModel = viewModel;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Canvas"));
        treeModel = new DefaultTreeModel(buildBranch(viewModel.getForm().getRoot()));
        tree = new JTree(treeModel);
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        setUpDropping();
        setUpSelection();

        add(new JScrollPane(tree), BorderLayout.CENTER);
        add(buildButtonBar(), BorderLayout.SOUTH);
        expandAll();
    }

    // Copies one branch of the form into Swing nodes. Calls itself for groups, which is
    // how the nesting comes across however deep it go. instanceof again, because only
    // ContainerNode has getChildren().
    private DefaultMutableTreeNode buildBranch(FormNode node) {
        DefaultMutableTreeNode swingNode = new DefaultMutableTreeNode(node);

        if (node instanceof ContainerNode) {
            ContainerNode group = (ContainerNode) node;
            for (int i = 0; i < group.getChildren().size(); i++) {
                swingNode.add(buildBranch(group.getChildren().get(i)));
            }
        }
        return swingNode;
    }

    // Three buttons, three one-line calls into the ViewModel. No logic here.
    private JPanel buildButtonBar() {
        JPanel bar = new JPanel();

        JButton up = new JButton("Move Up");
        up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewModel.moveSelectedUp();
            }
        });
        JButton down = new JButton("Move Down");
        down.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewModel.moveSelectedDown();
            }
        });

        JButton delete = new JButton("Delete");
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewModel.deleteSelected();
            }
        });
        bar.add(up);
        bar.add(down);
        bar.add(delete);
        return bar;
    }

    /*
     * Takes a drop from the palette.
     * The palette sends plain text like "Select One" and the ViewModel
     * decides what to build out of it.
     */
    private void setUpDropping() {
        tree.setDropMode(DropMode.ON);
        tree.setTransferHandler(new TransferHandler() {

            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDrop()
                        && support.isDataFlavorSupported(DataFlavor.stringFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }
                try {
                    String paletteType = (String) support.getTransferable()
                            .getTransferData(DataFlavor.stringFlavor);
                    JTree.DropLocation drop =
                            (JTree.DropLocation) support.getDropLocation();

                    // Null path means they dropped on empty space below the
                    // tree, and the ViewModel treats that as the top level.
                    FormNode target = null;
                    if (drop.getPath() != null) {
                        target = formNodeIn(drop.getPath());
                    }

                    viewModel.addControl(paletteType.trim(), target);
                    return true;

                } catch (Exception ex) {
                    return false; // bad drop, just refuse it
                }
            }
        });
    }


    private void setUpSelection() {
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (refreshing) {
                    return;
                }
                TreePath path = tree.getSelectionPath();
                if (path == null) {
                    viewModel.setSelectedNode(null);
                } else {
                    viewModel.setSelectedNode(formNodeIn(path));
                }
            }
        });
    }

    // digs the FormNode back out of a Swing node.
    private FormNode formNodeIn(TreePath path) {
        DefaultMutableTreeNode swingNode =
                (DefaultMutableTreeNode) path.getLastPathComponent();
        return (FormNode) swingNode.getUserObject();
    }

    /*
     * called whenever the ViewModel reports a change.
     *
     * The whole Swing tree gets built again instead of working out which
     * one node moved. A form has tens of nodes, not thousands, so the
     * simple way is fast enough and harder to get wrong.
     */
    public void refresh() {
        refreshing = true;
        treeModel.setRoot(buildBranch(viewModel.getForm().getRoot()));
        expandAll();

        FormNode selected = viewModel.getSelectedNode();
        if (selected == null) {
            tree.clearSelection();
        } else {
            selectNode(selected);
        }

        refreshing = false;
    }


    // Finds the Swing node holding this FormNode and highlights it.
    public void selectNode(FormNode wanted) {
        DefaultMutableTreeNode root =
                (DefaultMutableTreeNode) treeModel.getRoot();
        DefaultMutableTreeNode found = search(root, wanted);

        if (found != null) {
            TreePath path = new TreePath(found.getPath());
            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);
        }
    }

    // == on purpose, not equals(). It has to be the one same object, and two
    // different questions can easily carry the same name while the user is
    // still fixing them up.
    private DefaultMutableTreeNode search(DefaultMutableTreeNode branch, FormNode wanted) {
        if (branch.getUserObject() == wanted) {
            return branch;
        }
        for (int i = 0; i < branch.getChildCount(); i++) {
            DefaultMutableTreeNode child =
                    (DefaultMutableTreeNode) branch.getChildAt(i);
            DefaultMutableTreeNode found = search(child, wanted);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    // Row count grows as rows open up, so this keeps catching the new ones. looks
    // like a bug, is not.
    private void expandAll() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }
}
