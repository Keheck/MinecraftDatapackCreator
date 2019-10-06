package io.github.keheck.window.dialogs;

import io.github.keheck.Main;
import io.github.keheck.Tasks;
import io.github.keheck.util.Log;

import javax.swing.*;
import java.awt.*;

public class DialogNewProject extends JDialog
{
    private JTextField projName;
    private JTextField nameName;
    private JButton approve;
    private JButton cancel;

    public DialogNewProject()
    {
        super(Main.frame, "New Project");
        JPanel panel = new JPanel();
        this.add(panel);

        Log.i("Opening dialog DialogNewProject");

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel projLabel = new JLabel("Project Name");
        JLabel nameLabel = new JLabel("Namespace");

        projName = new JTextField();
        nameName = new JTextField();

        approve = new JButton("Create");
        cancel = new JButton("Cancel");

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(approve)
                                .addComponent(cancel))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(projLabel)
                                .addComponent(projName))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(nameLabel)
                                .addGap(13)
                                .addComponent(nameName))
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(projLabel)
                                .addComponent(projName, 30, 30, 30))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(nameLabel)
                                .addComponent(nameName, 30, 30, 30))
                        .addGap(30)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(approve)
                                .addComponent(cancel))
        );

        setMinimumSize(new Dimension(400, 200));
        setResizable(false);
        setLocationRelativeTo(null);
        setupActions();
        setVisible(true);
    }

    private void setupActions()
    {
        cancel.addActionListener(e -> dispose());
        approve.addActionListener(e -> Tasks.onNewProject(projName.getText(), nameName.getText(), this));
    }
}
