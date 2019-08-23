package io.github.keheck.util;

import io.github.keheck.tree.AbstractNavTreeNode.NodeType;

public class Validator
{
    public static boolean isValidString(String toTest, NodeType type)
    {
        return toTest.matches("[a-z0-9_]+");
    }

    public static boolean between(int min, int val, int max) { return min <= val && val <= max; }

    public static boolean isValidNamespace(String nameSpace)
    {
        switch(nameSpace.toLowerCase().trim())
        {
            case "if":
            case "for":
            case "while":
            case "else":
                return false;
            default:
                return true;
        }
    }
}
