package io.github.keheck.tree;

import io.github.keheck.Main;

import java.util.ArrayList;

public class VirtualUnlessNode extends NavTreeFile
{
    public static int precedingUnlesss = 0;
    private ArrayList<String> code;
    private String condition;

    public VirtualUnlessNode(ArrayList<String> code, String condition)
    {
        super(NodeType.MCFUNCTION, "unless" + precedingUnlesss);
        precedingUnlesss++;
        this.code = code;
        this.condition = condition;
    }

    public ArrayList<String> getCode() { return code; }

    public String getCondition() { return condition; }

    @Override
    public String getPath() { return Main.project + "unlesss/" + toString(); }
}
