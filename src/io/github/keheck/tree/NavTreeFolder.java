package io.github.keheck.tree;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

public class NavTreeFolder extends AbstractNavTreeNode
{
    private Vector<TreeNode> children;
    private MutableTreeNode parent;
    private Object user;

    public NavTreeFolder(NodeType type, String name)
    {
        super(type, name);
        children = new Vector<>();
    }

    @Override
    public TreeNode getChildAt(int childIndex) { return children.get(childIndex); }

    @Override
    public int getChildCount() { return children.size(); }

    @Override
    public int getIndex(TreeNode node) { return children.indexOf(node); }

    @Override
    public boolean isLeaf() { return children.size() == 0; }

    @Override
    public Enumeration<TreeNode> children() { return Collections.enumeration(children); }

    @Override
    public void insert(MutableTreeNode child, int index)
    {
        children.insertElementAt(child, index);
        child.setParent(this);
    }

    @Override
    public void remove(int index)
    {
        AbstractNavTreeNode node = (AbstractNavTreeNode)children.remove(index);
        node.setParent(null);
    }

    @Override
    public void remove(MutableTreeNode node) { children.remove(node); }

    @Override
    public void setUserObject(Object object) { this.user = object; }

    @Override
    public void removeFromParent()
    {
        parent.remove(this);
        this.parent = null;
    }

    @Override
    public void setParent(MutableTreeNode newParent) { this.parent = newParent; }
}
