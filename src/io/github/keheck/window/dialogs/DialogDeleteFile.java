package io.github.keheck.window.dialogs;

import io.github.keheck.Main;
import io.github.keheck.Tasks;
import io.github.keheck.tree.AbstractNavTreeNode;
import io.github.keheck.util.Log;

import javax.swing.*;
import java.awt.*;

public class DialogDeleteFile extends JDialog
{
    private JButton approve;
    private JButton cancel;
    private AbstractNavTreeNode node;

    public DialogDeleteFile(AbstractNavTreeNode node)
    {
        super(Main.frame, "Delete");
        Log.i("Opening dialog DialogDeleteFile");
        JLabel delete = new JLabel("You are about to delete: ");
        JLabel file = new JLabel(node.toString());
        JLabel question = new JLabel("Are you sure? (Children will be deleted as well)");

        approve = new JButton("Ok");
        cancel = new JButton("Cancel");
        this.node = node;

        file.setForeground(new Color(0xFF0000));

        JPanel host = new JPanel();
        add(host);

        GroupLayout layout = new GroupLayout(host);
        host.setLayout(layout);

        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(delete)
                        .addComponent(file))
                .addComponent(question)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(approve)
                        .addComponent(cancel))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(delete)
                        .addComponent(file))
                .addComponent(question)
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
        approve.addActionListener(e -> Tasks.delete(node, this));
        cancel.addActionListener(e -> dispose());
    }
}
