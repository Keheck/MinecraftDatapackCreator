package io.github.keheck.window.dialogs;

import io.github.keheck.Main;
import io.github.keheck.util.Log;

import javax.swing.*;
import java.awt.*;

public class DialogDynamic extends JDialog
{
    private JLabel label;
    private JLabel task;
    private String currentTask;

    public DialogDynamic()
    {
        super(Main.frame, "Compiling...");
        Log.i("Opening dialog DialogDynamic");
        label = new JLabel("Compiling");
        task = new JLabel("What should I do?");

        JPanel host = new JPanel();
        this.add(host);

        GroupLayout layout = new GroupLayout(host);
        host.setLayout(layout);

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(label)
                .addComponent(task));

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(label)
                .addComponent(task));

        setLocationRelativeTo(null);
        setResizable(false);
        setSize(new Dimension(200, 100));
        setVisible(true);
    }

    public void cycle()
    {
        String text = label.getText();

        switch(text)
        {
            case "Compiling":
                label.setText("Compiling.");
                break;
            case "Compiling.":
                label.setText("Compiling..");
                break;
            case "Compiling..":
                label.setText("Compiling...");
                break;
            case "Compiling...":
                label.setText("Compiling");
                break;
        }
    }

    public void updateTask(String newTask)
    {
        this.task.setText(newTask);
    }
}
