package io.github.keheck.window.dialogs;

import io.github.keheck.Main;

import javax.swing.*;
import java.awt.*;

public class DialogDynamic extends JDialog
{
    private JLabel label;

    public DialogDynamic()
    {
        super(Main.frame, "Compiling...");
        label = new JLabel("Compiling");

        JPanel host = new JPanel();
        this.add(host);

        GroupLayout layout = new GroupLayout(host);
        host.setLayout(layout);

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(label));

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(label));

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
}
