package io.github.keheck;

import io.github.keheck.project.saveandload.ConfigValue.ValueType;
import io.github.keheck.util.Directories;
import io.github.keheck.util.Log;
import io.github.keheck.util.OSUtils;
import io.github.keheck.window.*;
import io.github.keheck.tree.NavTreeFile;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class Main
{
    public static JFrame frame;
    private static JPanel nav;
    public static JScrollPane editContainer;
    public static JTextArea editArea;
    private static JSplitPane splitPane;
    private static JMenuBar menuBar;
    public static JTree navTree;

    private static boolean killed = false;

    public static HashMap<NavTreeFile, ArrayList<String>> texts = new HashMap<>();
    public static final HashMap<String, ValueType> keyTypes = new HashMap<>();
    public static final HashMap<String, Method> valueSetters = new HashMap<>();

    public static String project;

    public static void main(String[] args)
    {
        System.out.println("Starting init...");
        OSUtils.init();
        Directories.initDirs();

        if(new File(Directories.rootDir, "runtime.txt.lck").exists())
        {
            System.out.println("Instance already running! Commiting ded...");
            killed = true;
            System.exit(1);
        }

        Log.init();
        Icons.init();

        Log.f1("Initialization finished!");
        Log.f2("Logger will be used from here on out...");
        Log.f1("Setting up UI...");

        try { UIManager.setLookAndFeel(new NimbusLookAndFeel()); }
        catch (UnsupportedLookAndFeelException e) { e.printStackTrace(); }

        frame = new JFrame("Minecraft Datapack Creator");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(900, 750));
        frame.setLocationRelativeTo(null);
        menuBar = MainMenu.setupMenu();
        nav = NavigatorPanel.setupNavigatorPanel();
        navTree = new JTree();
        navTree.setModel(new DefaultTreeModel(null));
        nav.add(navTree);
        editContainer = (JScrollPane)EditPanel.setupEditTextArea()[0];
        editArea = (JTextArea)EditPanel.setupEditTextArea()[1];
        nav.setBackground(new Color(255, 255, 255));
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, nav, editContainer);
        splitPane.setEnabled(true);
        splitPane.setOneTouchExpandable(false);
        frame.add(splitPane);
        Tasks.onNewProject("project", "name", null);
        frame.setVisible(true);

        Log.f1("Setup UI!");
        Log.i("Setting up metadata handlers");

        keyTypes.put("keepComments", ValueType.BOOL);
        keyTypes.put("useVanilla", ValueType.BOOL);

        try
        {
            Method methKeepComments = MainMenu.class.getDeclaredMethod("setUseComments", boolean.class);
            Method methUseCommands = MainMenu.class.getDeclaredMethod("setUseVanilla", boolean.class);

            valueSetters.put("keepComments", methKeepComments);
            valueSetters.put("useVanilla", methUseCommands);
        }
        catch (NoSuchMethodException e)
        {
            Log.e("Failed to load method!", e);
            e.printStackTrace();
        }
    }
}
