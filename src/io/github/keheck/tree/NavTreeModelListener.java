package io.github.keheck.tree;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class NavTreeModelListener implements TreeModelListener
{
    @Override
    public void treeNodesChanged(TreeModelEvent e)
    {

    }

    @Override
    public void treeNodesInserted(TreeModelEvent e)
    {
        AbstractNavTreeNode node = (AbstractNavTreeNode)e.getChildren()[e.getChildren().length-1];
        node.setParent((AbstractNavTreeNode)e.getPath()[e.getPath().length-1]);
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e)
    {
        AbstractNavTreeNode node = (AbstractNavTreeNode)e.getChildren()[e.getChildren().length-1];
        node.setParent(null);
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e)
    {

    }
}
