package io.github.keheck.window.dialogs;

import io.github.keheck.Main;
import io.github.keheck.Tasks;
import io.github.keheck.tree.AbstractNavTreeNode;
import io.github.keheck.util.Log;

import javax.swing.*;
import java.awt.*;

public class DialogNewFolder extends JDialog
{
    private JTextField field;
    private JButton approve;
    private JButton cancel;
    private AbstractNavTreeNode parent;

    public DialogNewFolder(AbstractNavTreeNode parent)
    {
        super(Main.frame, "New Folder");
        JPanel host = new JPanel();
        add(host);

        Log.i("Opening dialog DialogNewFolder");
        JLabel label = new JLabel("Folder");
        field = new JTextField();
        this.parent = parent;
        approve = new JButton("Ok");
        cancel = new JButton("Cancel");

        GroupLayout layout = new GroupLayout(host);
        host.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(label)
                        .addComponent(field))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(approve)
                        .addComponent(cancel))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(label)
                        .addComponent(field, 30, 30, 30))
                .addGap(70)
                .addGroup(layout.createParallelGroup()
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
        approve.addActionListener(e -> Tasks.createFolder(field.getText(), this, parent));
    }
}
