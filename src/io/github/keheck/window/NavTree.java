package io.github.keheck.window;

import io.github.keheck.Main;
import io.github.keheck.tree.*;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import java.util.ArrayList;
import java.util.Enumeration;

import static io.github.keheck.tree.AbstractNavTreeNode.NodeType.*;

public class NavTree
{
    public static NavTreeFile meta;

    public static void cunstructTree(String projName, String nameSpace)
    {
        if("".equals(nameSpace) || nameSpace == null) nameSpace = projName;
        NavTreeFolder root = new NavTreeFolder(FOLDER, projName);
        JTree tree = Main.navTree;
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setModel(new DefaultTreeModel(root));
        tree.getModel().addTreeModelListener(new NavTreeModelListener());
        tree.addTreeSelectionListener(new NavTreeSelectionListener());
        tree.setCellRenderer(new DefaultTreeRenderer(new NodeIconProvider()));
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            NavTreeFolder minecraft = new NavTreeFolder(NAMESPACE, "minecraft");
            model.insertNodeInto(minecraft, root, 0);
                NavTreeFolder advancementsMC = new NavTreeFolder(FOLDER, "advancements");
                model.insertNodeInto(advancementsMC, minecraft, 0);

                NavTreeFolder loot_tablesMC = new NavTreeFolder(FOLDER, "loot_tables");
                model.insertNodeInto(loot_tablesMC, minecraft, 1);

                NavTreeFolder recipiesMC = new NavTreeFolder(FOLDER, "recipies");
                model.insertNodeInto(recipiesMC, minecraft, 2);

                NavTreeFolder tagsMC = new NavTreeFolder(FOLDER, "tags");
                model.insertNodeInto(tagsMC, minecraft, 3);

            NavTreeFolder name = new NavTreeFolder(NAMESPACE, nameSpace);
            model.insertNodeInto(name, root, 1);
                NavTreeFolder advancements = new NavTreeFolder(FOLDER, "advancements");
                model.insertNodeInto(advancements, name, 0);

                NavTreeFolder functions = new NavTreeFolder(FOLDER, "functions");
                model.insertNodeInto(functions, name, 1);

                NavTreeFolder loot_tables = new NavTreeFolder(FOLDER, "loot_tables");
                model.insertNodeInto(loot_tables, name, 2);

                NavTreeFolder recipies = new NavTreeFolder(FOLDER, "recipies");
                model.insertNodeInto(recipies, name, 3);

                NavTreeFolder structures = new NavTreeFolder(FOLDER, "structures (not supported)");
                model.insertNodeInto(structures, name, 4);

                NavTreeFolder tags = new NavTreeFolder(FOLDER, "tags");
                model.insertNodeInto(tags, name, 5);

            meta = new NavTreeFile(META, "pack");
            model.insertNodeInto(meta, root, 2);

        ArrayList<String> metaText = new ArrayList<>();

        metaText.add("{");
        metaText.add("    \"pack\":");
        metaText.add("    {");
        metaText.add("        \"description\": \"Description here\",");
        metaText.add("        \"pack_format\": 1");
        metaText.add("    }");
        metaText.add("}");

        Main.texts.put(meta, metaText);
        Main.project = projName;
    }

    public static int getChildIndex(Enumeration<TreeNode> others, AbstractNavTreeNode toInsert)
    {
        int index = 0;

        while(others.hasMoreElements())
        {
            AbstractNavTreeNode node = (AbstractNavTreeNode)others.nextElement();
            boolean insertIsLeaf = toInsert.getType().isLeaf();
            boolean nodeIsLeaf = node.getType().isLeaf();

            if(insertIsLeaf)
            {
                if(!nodeIsLeaf)
                    index++;
                else if(node.getName().compareToIgnoreCase(toInsert.getName()) < 0)
                    index++;
                else
                    return index;
            }
            else
            {
                if(nodeIsLeaf)
                    return index;
                else if(node.getName().compareToIgnoreCase(toInsert.getName()) < 0)
                    index++;
                else
                    return index;
            }
        }

        return index;
    }
}
