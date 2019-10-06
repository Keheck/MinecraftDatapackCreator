package io.github.keheck.tree;

import io.github.keheck.Main;

import java.util.ArrayList;

public class VirtualIfNode extends NavTreeFile
{
    public static int precedingIfs = 0;

    public VirtualIfNode()
    {
        super(NodeType.MCFUNCTION, "if" + precedingIfs);
        precedingIfs++;
    }

    @Override
    public String getPath() { return Main.project + "ifs/" + toString(); }
}
