package io.github.keheck.tree;

import javax.swing.tree.MutableTreeNode;

public class NavTreeFile extends AbstractNavTreeNode
{
    private MutableTreeNode parent;
    private Object user;

    public NavTreeFile(NodeType type, String name) { super(type, name); }

    @Override
    public boolean isLeaf() { return true; }

    @Override
    public void setUserObject(Object object) { this.user = object; }

    @Override
    public void removeFromParent()
    {
        parent.remove(this);
        parent = null;
    }

    @Override
    public void setParent(MutableTreeNode newParent) { this.parent = newParent; }

    @Override
    public void insert(MutableTreeNode child, int index) { }

    @Override
    public void remove(int index) { }

    @Override
    public void remove(MutableTreeNode node) { }
}
