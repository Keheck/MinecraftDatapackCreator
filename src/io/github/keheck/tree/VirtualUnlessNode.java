package io.github.keheck.tree;

import io.github.keheck.Main;

import java.util.ArrayList;

public class VirtualUnlessNode extends NavTreeFile
{
    public static int precedingUnlesss = 0;

    public VirtualUnlessNode()
    {
        super(NodeType.MCFUNCTION, "unless" + precedingUnlesss);
        precedingUnlesss++;
    }

    @Override
    public String getPath() { return Main.project + "unlesss/" + toString(); }
}
