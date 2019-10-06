package io.github.keheck.window.dialogs;

import io.github.keheck.Main;
import io.github.keheck.project.saveandload.Load;
import io.github.keheck.util.Directories;
import io.github.keheck.util.Log;

import javax.swing.*;
import java.awt.*;

public class DialogLoadProject extends JDialog
{
    private JFileChooser fileChooser;

    public DialogLoadProject()
    {
        super(Main.frame, "Load project...");

        Log.i("Showing dialog DialogLoadProject");
        fileChooser = new JFileChooser(Directories.projectsDir);
        fileChooser.setFileFilter(new DialogLoadProjFilter());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JPanel panel = new JPanel();
        panel.add(fileChooser);
        add(panel);

        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600, 400));
        setResizable(false);
        setupActions();
        setVisible(true);
    }

    private void setupActions()
    {
        fileChooser.addActionListener(e ->
        {
            if(e.getActionCommand().equals("ApproveSelection"))
                Load.load(fileChooser.getSelectedFile());
            setVisible(false);
        });
    }
}
