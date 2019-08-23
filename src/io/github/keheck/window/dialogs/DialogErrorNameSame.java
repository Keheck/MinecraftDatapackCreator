package io.github.keheck.window.dialogs;

import javax.swing.*;
import java.awt.*;

public class DialogErrorNameSame extends JDialog
{
    private JButton ok;

    public DialogErrorNameSame(JDialog owner)
    {
        super(owner, "ERROR");
        JLabel label = new JLabel("Ther's already a file with that name!");
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
        setSize(new Dimension(200, 100));
        setupActions();
        setVisible(true);
    }

    private void setupActions()
    {
        ok.addActionListener(e -> dispose());
    }
}
