package io.github.keheck;

import io.github.keheck.project.compiling.Compiler;
import io.github.keheck.util.Log;
import io.github.keheck.util.Validator;
import io.github.keheck.window.NavTree;
import io.github.keheck.window.dialogs.*;
import io.github.keheck.tree.AbstractNavTreeNode;
import io.github.keheck.tree.NavTreeFile;
import io.github.keheck.tree.NavTreeFolder;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;

import static io.github.keheck.tree.AbstractNavTreeNode.NodeType;

public class Tasks
{
    public static void onNewProject(String projName, String nameSpace, DialogNewProject project)
    {
        new Thread(() ->
        {
            String myProjName = projName;
            String myNamespace = nameSpace;

            if(myProjName == null) myProjName = "";
            if(myNamespace == null) myNamespace = "";

            if(!myProjName.matches("[a-z0-9_]+"))
            {
                new DialogErrorNameInvalid(myProjName, project);
                return;
            }

            if(!myNamespace.matches("[a-z0-9_]+"))
            {
                new DialogErrorNameInvalid(myNamespace, project);
                return;
            }

            NavTree.cunstructTree(projName, myNamespace);
            if(project != null)
                project.dispose();
        }, "Tree Constructor").start();
    }

    public static Thread constructDynamicDialog()
    {
        return new DialogUpdater();
    }

    public static void initNewFile(NodeType type)
    {
        AbstractNavTreeNode node = (AbstractNavTreeNode)Main.navTree.getLastSelectedPathComponent();
        NodeType parentType = node.getType();

        if(parentType != NodeType.FOLDER && parentType != NodeType.NAMESPACE)
            node = (AbstractNavTreeNode)node.getParent();

        new DialogNewFile(type, node);
    }

    public static void createNewFile(String fileName, NodeType fileType, AbstractNavTreeNode parent, DialogNewFile dialog)
    {
        Log.f1("Attempting to create new file");

        if(Validator.isValidString(fileName, fileType))
        {
            Enumeration<TreeNode> nodes = parent.children();

            while(nodes.hasMoreElements())
            {
                TreeNode node = nodes.nextElement();

                if(node instanceof AbstractNavTreeNode && ((AbstractNavTreeNode) node).getType() == fileType)
                {
                    if(((AbstractNavTreeNode)node).getName().equalsIgnoreCase(fileName) && ((AbstractNavTreeNode) node).getType() == fileType)
                    {
                        new DialogErrorNameSame(dialog);
                        return;
                    }
                }
            }

            NavTreeFile file = new NavTreeFile(fileType, fileName);
            ((DefaultTreeModel)Main.navTree.getModel()).insertNodeInto(file, parent, NavTree.getChildIndex(parent.children(), file));
            dialog.setVisible(false);
            Main.texts.put(file, new ArrayList<>());
        }
        else
        {
            new DialogErrorNameInvalid(fileName, dialog);
        }
    }

    public static void createNamespace(String folderName, JDialog dialog)
    {
        Log.f1("Attempting to create new namespace");

        if(Validator.isValidString(folderName, NodeType.NAMESPACE))
        {
            if(Validator.isValidNamespace(folderName))
            {
                AbstractNavTreeNode parent = (AbstractNavTreeNode)Main.navTree.getModel().getRoot();
                Enumeration<TreeNode> children = parent.children();

                while(children.hasMoreElements())
                {
                    AbstractNavTreeNode node = (AbstractNavTreeNode)children.nextElement();

                    if(node.getName().equalsIgnoreCase(folderName) && !node.getType().isLeaf())
                    {
                        new DialogErrorNameSame(dialog);
                        return;
                    }
                }

                NavTreeFolder child = new NavTreeFolder(NodeType.NAMESPACE, folderName);
                ((DefaultTreeModel)Main.navTree.getModel()).insertNodeInto(child, parent, NavTree.getChildIndex(parent.children(), child));

                dialog.setVisible(false);
            }
            else
            {
                new DialogErrorNamespaceReserved(folderName, dialog);
            }
        }
        else
        {
            new DialogErrorNameInvalid(folderName, dialog);
        }
    }

    public static void createFolder(String folderName, JDialog dialog, AbstractNavTreeNode parent)
    {
        Log.f1("Attempting to create new folder");

        if(Validator.isValidString(folderName, NodeType.FOLDER))
        {
            Enumeration<TreeNode> children = parent.children();

            while(children.hasMoreElements())
            {
                AbstractNavTreeNode node = (AbstractNavTreeNode)children.nextElement();

                if(node.getName().equalsIgnoreCase(folderName) && !node.getType().isLeaf())
                {
                    new DialogErrorNameSame(dialog);
                    return;
                }
            }

            NavTreeFolder child = new NavTreeFolder(NodeType.FOLDER, folderName);
            ((DefaultTreeModel)Main.navTree.getModel()).insertNodeInto(child, parent, NavTree.getChildIndex(parent.children(), child));

            dialog.setVisible(false);
        }
        else
        {
            new DialogErrorNameInvalid(folderName, dialog);
        }
    }

    public static void delete(AbstractNavTreeNode node, JDialog dialog)
    {
        ((DefaultTreeModel)Main.navTree.getModel()).removeNodeFromParent(node);
        dialog.dispose();
    }

    public static void compile(File destination)
    {
        new Thread(() -> Compiler.init(destination), "Compiler Thread").start();
    }

    public static class DialogUpdater extends Thread
    {
        private DialogDynamic dialog = new DialogDynamic();

        private DialogUpdater()
        {
            super("Dialog updater");
        }

        @Override
        public void run()
        {
            while (true)
            {
                try { Thread.sleep(1000); }
                catch (InterruptedException e)
                {
                    dialog.dispose();
                    break;
                }

                dialog.cycle();
            }
        }

        public void updateDialog(String newTask)
        {
            dialog.updateTask(newTask);
        }
    }
}
