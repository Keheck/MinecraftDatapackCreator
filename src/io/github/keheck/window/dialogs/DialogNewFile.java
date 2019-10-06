package io.github.keheck.window.dialogs;

import io.github.keheck.Main;
import io.github.keheck.Tasks;
import io.github.keheck.tree.AbstractNavTreeNode;
import io.github.keheck.util.Log;

import javax.swing.*;
import java.awt.*;

public class DialogNewFile extends JDialog
{
    private AbstractNavTreeNode.NodeType type;
    private JTextField field;
    private JButton approve;
    private JButton cancel;
    private AbstractNavTreeNode node;

    public DialogNewFile(AbstractNavTreeNode.NodeType type, AbstractNavTreeNode parent)
    {
        super(Main.frame, "New File");
        JPanel panel = new JPanel();
        this.add(panel);
        this.type = type;
        node = parent;

        Log.i("Opening dialog DialogNewFile");
        JLabel label = new JLabel("File name");
        field = new JTextField();

        approve = new JButton("Create");
        cancel = new JButton("Cancel");

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

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
        approve.addActionListener(e -> Tasks.createNewFile(field.getText(), type, node, this));
        cancel.addActionListener(e -> dispose());
    }
}
