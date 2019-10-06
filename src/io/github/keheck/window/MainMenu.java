package io.github.keheck.window;

import io.github.keheck.Main;
import io.github.keheck.Tasks;
import io.github.keheck.project.saveandload.Load;
import io.github.keheck.project.saveandload.Save;
import io.github.keheck.window.dialogs.*;
import io.github.keheck.tree.AbstractNavTreeNode;

import javax.swing.*;

public class MainMenu
{
    private static JMenuBar menuBar;

    private static JMenu file;
    private static JMenuItem newProject;
    private static JMenuItem exportProject;
    private static JMenuItem saveProject;
    private static JMenuItem loadProject;

    private static JMenu newFile;
    private static JMenuItem newFunc;
    private static JMenuItem newJson;
    private static JMenuItem newNamespace;
    private static JMenuItem newFolder;
    private static JMenuItem deleteFile;

    public static JMenu settings;
    public static JCheckBoxMenuItem commandsOnly;
    public static JCheckBoxMenuItem keepComments;

    public static JMenuBar setupMenu()
    {
        JFrame frame = Main.frame;

        menuBar = new JMenuBar();

            file = new JMenu("File");

                newProject = new JMenuItem("New Project");
                newProject.setIcon(Icons.PROJECT_NEW);
            file.add(newProject);

                loadProject = new JMenuItem("Load Project");
                loadProject.setIcon(Icons.PROJECT_LOAD);
            file.add(loadProject);

                exportProject = new JMenuItem("Compile Project");
                exportProject.setIcon(Icons.PROJECT_EXPORT);
            file.add(exportProject);

                saveProject = new JMenuItem("Save Project");
                saveProject.setIcon(Icons.PROJECT_SAVE);
            file.add(saveProject);

            file.addSeparator();

                newFile = new JMenu("New");
                newFile.setIcon(Icons.FILE_NEWFILE);

                    newFunc = new JMenuItem("MCFunction-File");
                    newFunc.setIcon(Icons.FILE_MCFUNC);
                newFile.add(newFunc);

                    newJson = new JMenuItem("JSON-File");
                    newJson.setIcon(Icons.FILE_JSON);
                newFile.add(newJson);

                    newNamespace = new JMenuItem("Namespace");
                    newNamespace.setIcon(Icons.FILE_NAMESPACE);
                newFile.add(newNamespace);

                    newFolder = new JMenuItem("Folder");
                    newFolder.setIcon(Icons.FILE_FOLDER);
                newFile.add(newFolder);

                deleteFile = new JMenuItem("Delete");
                deleteFile.setIcon(Icons.FILE_REMOVEFILE);
            file.add(deleteFile);
            file.add(newFile);

            settings = new JMenu("Project Settings");
                commandsOnly = new JCheckBoxMenuItem("Use Minecraft commands only");
                settings.add(commandsOnly);

                keepComments = new JCheckBoxMenuItem("Keep comments after compilation");
                settings.add(keepComments);

        menuBar.add(file);
        menuBar.add(settings);

        frame.setJMenuBar(menuBar);

        setupActions();

        return menuBar;
    }

    public static void setUseComments(boolean value) { keepComments.setState(value); }

    public static void setUseVanilla(boolean value) { commandsOnly.setState(value); }

    private static void setupActions()
    {
        newProject.addActionListener(e -> new DialogNewProject());
        newFunc.addActionListener(e -> Tasks.initNewFile(AbstractNavTreeNode.NodeType.MCFUNCTION));
        newJson.addActionListener(e -> Tasks.initNewFile(AbstractNavTreeNode.NodeType.JSON));
        newNamespace.addActionListener(e -> new DialogNewNamespace());
        newFolder.addActionListener(e -> new DialogNewFolder((AbstractNavTreeNode)Main.navTree.getLastSelectedPathComponent()));
        deleteFile.addActionListener(e -> new DialogDeleteFile((AbstractNavTreeNode)Main.navTree.getLastSelectedPathComponent()));
        saveProject.addActionListener(e -> Save.save(Main.project));
        loadProject.addActionListener(e -> Load.initLoad());
        exportProject.addActionListener(e -> new DialogCompileProject());
    }
}
