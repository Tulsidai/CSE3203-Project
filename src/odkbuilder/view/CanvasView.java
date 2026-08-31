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

public class CanvasView extends JPanel {
    private FormViewModel viewModel;
    private JTree tree;
    private DefaultTreeModel treeModel;

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

                    FormNode target = null;
                    if (drop.getPath() != null) {
                        target = formNodeIn(drop.getPath());
                    }

                    viewModel.addControl(paletteType.trim(), target);
                    return true;

                } catch (Exception ex) {
                    return false;
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

    private FormNode formNodeIn(TreePath path) {
        DefaultMutableTreeNode swingNode =
                (DefaultMutableTreeNode) path.getLastPathComponent();
        return (FormNode) swingNode.getUserObject();
    }

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

    private void expandAll() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }
}
