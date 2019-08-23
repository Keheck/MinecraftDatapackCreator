package io.github.keheck.tree;

import org.jdesktop.swingx.renderer.IconValue;

import javax.swing.*;

public class NodeIconProvider implements IconValue
{
    @Override
    public Icon getIcon(Object value)
    {
        if(value instanceof AbstractNavTreeNode)
        {
            return ((AbstractNavTreeNode)value).getType().getIcon();
        }

        return null;
    }
}
