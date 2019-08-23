package io.github.keheck.window.dialogs;

import io.github.keheck.Main;
import io.github.keheck.util.Directories;

import javax.swing.*;
import java.awt.*;

public class DialogCompilationFail extends JDialog
{
    private JButton ok;

    public DialogCompilationFail()
    {
        super(Main.frame, "Compilation finisehd!");
        JLabel label = new JLabel("Compilation has failed! View the error log at " + System.lineSeparator() + Directories.rootDir.getAbsolutePath() + "/error.txt for more information");
        ok = new JButton("Ok");
        JPanel host = new JPanel();
        this.add(host);

        GroupLayout layout = new GroupLayout(host);
        host.setLayout(layout);

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(label)
                .addComponent(ok));

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(label)
                .addComponent(ok));

        setLocationRelativeTo(null);
        setResizable(false);
        setSize(new Dimension(400, 200));
        setupActions();
        setVisible(true);
    }

    private void setupActions() { ok.addActionListener(e -> dispose()); }
}
