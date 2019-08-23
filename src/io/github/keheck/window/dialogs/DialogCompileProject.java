package io.github.keheck.window.dialogs;

import io.github.keheck.Main;
import io.github.keheck.Tasks;
import io.github.keheck.project.saveandload.Load;
import io.github.keheck.util.Directories;

import javax.swing.*;
import java.awt.*;

public class DialogCompileProject extends JDialog
{
    private JFileChooser fileChooser;

    public DialogCompileProject()
    {
        super(Main.frame, "Compile project...");
        fileChooser = new JFileChooser(Directories.mcDir);
        fileChooser.setFileFilter(new DialogComileProjFilter());
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
        fileChooser.addActionListener(e -> {
            if(e.getActionCommand().toLowerCase().contains("approve"))
                //System.out.println(fileChooser.getSelectedFile());
                Tasks.compile(fileChooser.getSelectedFile());
            setVisible(false);
        });
    }
}
