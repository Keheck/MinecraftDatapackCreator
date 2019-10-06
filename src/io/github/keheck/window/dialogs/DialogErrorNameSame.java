package io.github.keheck.window.dialogs;

import io.github.keheck.util.Log;

import javax.swing.*;
import java.awt.*;

public class DialogErrorNameSame extends JDialog
{
    private JButton ok;

    public DialogErrorNameSame(JDialog owner)
    {
        super(owner, "ERROR");
        JPanel host = new JPanel();
        this.add(host);

        Log.i("Opening dialog DialogErrorNameSame");
        ok = new JButton("Ok");
        JLabel label = new JLabel("There's already a file with that name!");

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
        setSize(new Dimension(200, 100));
        setupActions();
        setVisible(true);
    }

    private void setupActions()
    {
        ok.addActionListener(e -> dispose());
    }
}
