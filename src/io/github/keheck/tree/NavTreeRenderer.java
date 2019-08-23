package io.github.keheck.tree;

import io.github.keheck.window.Icons;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class NavTreeRenderer extends DefaultTreeCellRenderer
{
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        setIcon(getIconFor(value));
        return this;
    }

    private static ImageIcon getIconFor(Object o)
    {
        if(o instanceof AbstractNavTreeNode)
        {
            return ((AbstractNavTreeNode) o).getIcon();
        }

        return Icons.FILE_UNKNOWN;
    }

    @Override
    public Icon getOpenIcon() { return Icons.TREE_COLLAPSE; }

    @Override
    public Icon getClosedIcon() { return Icons.TREE_EXPAND; }
}
