package io.github.keheck.tree;

import io.github.keheck.Main;

import java.util.ArrayList;

public class VirtualIfNode extends NavTreeFile
{
    public static int precedingIfs = 0;
    private ArrayList<String> code;
    private String condition;

    public VirtualIfNode(ArrayList<String> code, String condition)
    {
        super(NodeType.MCFUNCTION, "if" + precedingIfs);
        precedingIfs++;
        this.code = code;
        this.condition = condition;
    }

    public ArrayList<String> getCode() { return code; }

    public String getCondition() { return condition; }

    @Override
    public String getPath() { return Main.project + "ifs/" + toString(); }
}
