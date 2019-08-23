package io.github.keheck.window.dialogs;

import io.github.keheck.Main;

import javax.swing.*;
import java.awt.*;

public class DialogCompilationSuccess extends JDialog
{
    private JButton ok;

    public DialogCompilationSuccess(String loc)
    {
        super(Main.frame, "Compilation finisehd!");
        JLabel label = new JLabel("Compilation finished successfuly!" + System.lineSeparator() + "Project was saved at: " + loc);
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
