package io.github.keheck.project.saveandload;

import io.github.keheck.Main;
import io.github.keheck.window.NavTree;
import io.github.keheck.window.dialogs.DialogLoadProject;
import io.github.keheck.tree.AbstractNavTreeNode.NodeType;
import io.github.keheck.tree.NavTreeFile;
import io.github.keheck.tree.NavTreeFolder;

import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class Load
{
    private static DefaultTreeModel model;
    private static NavTreeFolder rootNode;

    public static void initLoad()
    {
        new DialogLoadProject();
        model = (DefaultTreeModel)Main.navTree.getModel();
    }

    public static void load(File root)
    {
        Main.texts.clear();
        Main.project = root.getName();

        File[] files = root.listFiles();
        try
        {
            if(files != null)
            {
                rootNode = new NavTreeFolder(NodeType.FOLDER, root.getName());
                model.setRoot(rootNode);

                for(File f : files)
                {
                    if(f.isDirectory())
                    {
                        addFolderNode(rootNode, new NavTreeFolder(NodeType.NAMESPACE, f.getName()), f);
                    }
                    else
                    {
                        String[] splitFileName = f.getName().split("\\.");
                        NavTreeFile treeFile = new NavTreeFile(NodeType.getNodeType("." + splitFileName[1]), splitFileName[0]);
                        addFileNode(rootNode, treeFile, f);
                    }
                }
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        model = null;
    }

    private static void addFolderNode(NavTreeFolder parent, NavTreeFolder child, File childDir) throws IOException
    {
        model.insertNodeInto(child, parent, NavTree.getChildIndex(parent.children(), child));

        File[] files = childDir.listFiles();

        if(files != null)
        {
            for(File file : files)
            {
                if(file.isDirectory())
                {
                    addFolderNode(child, new NavTreeFolder(NodeType.FOLDER, file.getName()), file);
                }
                else
                {
                    String[] splitFileName = file.getName().split("\\.");
                    NavTreeFile treeFile = new NavTreeFile(NodeType.getNodeType("." + splitFileName[1]), splitFileName[0]);
                    addFileNode(child, treeFile, file);
                }
            }
        }
    }

    private static void addFileNode(NavTreeFolder parent, NavTreeFile child, File file) throws IOException
    {
        model.insertNodeInto(child, parent, NavTree.getChildIndex(parent.children(), child));
        byte[] data = Files.readAllBytes(file.toPath());
        String[] lines = new String(data).split(System.lineSeparator());
        ArrayList<String> lineList = new ArrayList<>(Arrays.asList(lines));
        Main.texts.put(child, lineList);
        if(child.getType().getExtension().equals(".mcmeta"))
            NavTree.meta = child;
    }
}
