package io.github.keheck.tree;

import io.github.keheck.window.Icons;

import javax.swing.*;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public abstract class AbstractNavTreeNode implements MutableTreeNode, Cloneable, Serializable
{
    private NodeType type;
    private AbstractNavTreeNode parent;
    private String name;

    AbstractNavTreeNode(NodeType type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public NodeType getType() { return type; }

    public String getName() { return name; }

    void setParent(AbstractNavTreeNode parent) { this.parent = parent; }

    ImageIcon getIcon() { return type.icon; }

    @Override
    public TreeNode getChildAt(int childIndex) { return null; }

    @Override
    public TreeNode getParent() { return parent; }

    @Override
    public boolean getAllowsChildren() { return !type.isLeaf; }

    @Override
    public Enumeration<TreeNode> children() { return Collections.emptyEnumeration(); }

    @Override
    public int getChildCount() { return 0; }

    @Override
    public int getIndex(TreeNode node) { return 0; }

    @Override
    public String toString() { return name + type.getExtension(); }

    public String getPath()
    {
        AbstractNavTreeNode treeNode = this;

        ArrayList<AbstractNavTreeNode> nodes = new ArrayList<>();

        nodes.add(treeNode);
        treeNode = (AbstractNavTreeNode)treeNode.getParent();

        while (treeNode != null)
        {
            nodes.add(0, treeNode);
            treeNode = (AbstractNavTreeNode)treeNode.getParent();
        }

        String[] path = new String[nodes.size()];

        for(int i = 0; i < path.length; i++)
        {
            path[i] = nodes.get(i).toString();
        }

        return String.join("/", path);
    }

    public enum NodeType
    {
        JSON(Icons.FILE_JSON, true, ".json"),
        NAMESPACE(Icons.FILE_NAMESPACE, false),
        MCFUNCTION(Icons.FILE_MCFUNC, true, ".mcfunction"),
        META(Icons.FILE_METADATA, true, ".mcmeta"),
        FOLDER(Icons.FILE_FOLDER, false),
        UNKNOWN(Icons.FILE_UNKNOWN, true);

        private ImageIcon icon;
        private boolean isLeaf;
        private String extension;

        NodeType(ImageIcon icon, boolean isLeaf) { this(icon, isLeaf, ""); }

        NodeType(ImageIcon icon, boolean isLeaf, String extension)
        {
            this.icon = icon;
            this.isLeaf = isLeaf;
            this.extension = extension;
        }

        public static NodeType getNodeType(String extension)
        {
            switch(extension)
            {
                default:
                    return UNKNOWN;
                case ".json":
                    return JSON;
                case ".mcfunction":
                    return MCFUNCTION;
                case ".mcmeta":
                    return META;
            }
        }

        public boolean isLeaf() { return isLeaf; }

        public ImageIcon getIcon() { return icon; }

        public String getExtension() { return extension; }
    }
}
